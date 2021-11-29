package TerminalArea;

import devstudio.generatedcode.*;
import devstudio.generatedcode.datatypes.FederateStateEnum;
import devstudio.generatedcode.datatypes.SimulationPaceEnum;
import devstudio.generatedcode.datatypes.SimulationTimeStruct;
import devstudio.generatedcode.exceptions.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class main {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;
    String AD_filepath = "./resources/ADinfo.csv";
    String RWY_filepath = "./resources/RWYinfo.csv";
    long ref_start_time = 0;
    long ref_end_time = 0;
    boolean ind_resume = true;
//    ExerciseRunCommandEnum sim_state = ExerciseRunCommandEnum.ERC_STOP;
    FederateStateEnum sim_state = FederateStateEnum.AS_FROZEN;
    SimulationPaceEnum sim_rate = SimulationPaceEnum.PACE1X;



    private final HlaInteractionListener _simulationProcessListener = new HlaInteractionListener.Adapter(){
        public void simulationProcessCmdMsg(
                boolean local,
                HlaInteractionManager.HlaSimulationProcessCmdMsgParameters parameters,
                HlaTimeStamp timeStamp,
                HlaLogicalTime logicalTime
        ) {
//            sim_state = parameters.getExerciseRunCommand();
            System.out.println("Received cmd: ");
        }
    };

    private final HlaInteractionListener _simulationRunListener = new HlaInteractionListener.Adapter(){
        public void simulationRunMsg(
                boolean local,
                HlaInteractionManager.HlaSimulationRunMsgParameters parameters,
                HlaTimeStamp timeStamp,
                HlaLogicalTime logicalTime
        ) {
            System.out.println("Received cmd2: " );
        }
    };

    private final HlaMasterSimulationStateValueListener _masterListener = new HlaMasterSimulationStateValueListener.Adapter() {
        public void simulationTimeUpdated(HlaMasterSimulationState masterSimulationState, SimulationTimeStruct simulationTime, boolean validOldSimulationTime, SimulationTimeStruct oldSimulationTime, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
            if (masterSimulationState.getHlaInstanceName().equals("Master")){
                System.out.println("Received master time: " + masterSimulationState.getSimulationTime().timeInSeconds);
//                System.out.println("Received pace: " + masterSimulationState.getSimulationPace());
//                System.out.println("Received state: " + masterSimulationState.getFederateState());
                sim_state = masterSimulationState.getFederateState();
                sim_rate = masterSimulationState.getSimulationPace();
                if(ind_resume && TerminalManageDisplay.ind_start){
                    ref_start_time = masterSimulationState.getSimulationTime().timeInSeconds;
                    System.out.println("start time: " + ref_start_time);
                    ind_resume = false;
                }
                else if(!ind_resume){
                    ref_end_time = masterSimulationState.getSimulationTime().timeInSeconds;
                    System.out.println("start time: " + ref_start_time);
                    System.out.println("end time: " + ref_end_time);
                }
            }
        }
    };

    private final HlaFederateSimulationStateValueListener _fedListener = new HlaFederateSimulationStateValueListener.Adapter() {
        public void simulationTimeUpdated(HlaFederateSimulationState federateSimulationState, SimulationTimeStruct simulationTime, boolean validOldSimulationTime, SimulationTimeStruct oldSimulationTime, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
//            System.out.println("Received fed time: " + federateSimulationState.getSimulationTime().timeInSeconds);
        }
    };

    public main(){
        _hlaWorld = HlaWorld.Factory.create(new HlaSettings() {
            @Override
            public boolean getTimeConstrained() {
                return USE_TIME_MANAGEMENT;
            }

            @Override
            public boolean getTimeRegulating() {
                return USE_TIME_MANAGEMENT;
            }

            @Override
            public String getFederationName() {
                return "ATOS110802";
            }

            @Override
            public String getCrcHost() {
                return "192.168.46.241";
            }

            @Override
            public String getFederateName() {
                return "TermianlAreaFed";
            }

        });
        _hlaWorld.getHlaInteractionManager().addHlaInteractionListener(_simulationProcessListener);
        _hlaWorld.getHlaInteractionManager().addHlaInteractionListener(_simulationRunListener);
        _hlaWorld.getHlaMasterSimulationStateManager().addHlaMasterSimulationStateDefaultInstanceValueListener(_masterListener);
        _hlaWorld.getHlaFederateSimulationStateManager().addHlaFederateSimulationStateDefaultInstanceValueListener(_fedListener);
        _hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS);
    }

    public void createADEntity(HashSet<ADStruct> AD_set) throws HlaRtiException, HlaNotConnectedException, HlaInternalException {
//        HlaAerodromeEntity[] AD_list = new HlaAerodromeEntity[AD_set.size()];
        int i = 0;
        for (ADStruct s:AD_set) {
            _hlaWorld.getHlaAerodromeEntityManager().createLocalHlaAerodromeEntity(s.ICAOCodeID);
//            HlaAerodromeEntityUpdater updater = AD_list[i].getHlaAerodromeEntityUpdater();
        }
//        return AD_list;
    }

    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException, InterruptedException, HlaInTimeAdvancingStateException {
        int plan_count = 0;
        _hlaWorld.connect();
//        ArrayList<ADStruct> AD_set = getADInfo(); // 用来创建机场实例
//        createADEntity(AD_set);
        // 根据放行时间间隔来发送飞行计划：1、监听系统开始状态  2、监听系统时间
         Thread.sleep(30);
         while(true){
             Thread.sleep(30);
             if(TerminalManageDisplay.ind_start && (!ind_resume)){
                 System.out.println("开始执行放行！");
                 break;
             }
         }

        while(true){
            if(sim_state.equals(FederateStateEnum.AS_FROZEN)){

            }
            else if (sim_state.equals(FederateStateEnum.AS_RUNNING)){
                if((ref_end_time - ref_start_time) >= TerminalManageDisplay.delta_time){
                    ref_start_time =  _hlaWorld.getHlaMasterSimulationStateManager().getMasterSimulationStateByHlaInstanceName("Master").getSimulationTime().timeInSeconds;
                    // 判断当前是否还有计划
                    if(plan_count < TerminalManageDisplay.FLP_list.size()){
                        HlaInteractionManager.HlaFlightPlanMsgInteraction fpl = _hlaWorld.getHlaInteractionManager().getHlaFlightPlanMsgInteraction();
                        fpl.setFltNo(TerminalManageDisplay.FLP_list.get(plan_count).FltNo);
                        fpl.setDepAD(TerminalManageDisplay.FLP_list.get(plan_count).DepAD);
                        fpl.setArrAD(TerminalManageDisplay.FLP_list.get(plan_count).ArrAD);
                        fpl.setACRegisteredNum(TerminalManageDisplay.FLP_list.get(plan_count).ACRigNum);
                        fpl.setACType(TerminalManageDisplay.FLP_list.get(plan_count).ACType);
                        fpl.setCruAlt(TerminalManageDisplay.FLP_list.get(plan_count).CruAlt);
                        fpl.setCruIAS(TerminalManageDisplay.FLP_list.get(plan_count).CruIAS);
                        fpl.setDepRWY(TerminalManageDisplay.FLP_list.get(plan_count).RWYInfo);
                        fpl.setArrRWY(TerminalManageDisplay.FLP_list.get(plan_count).ArrRWYInfo);
                        fpl.sendInteraction();
                        System.out.println(ref_start_time);
                        System.out.println("Send FlightPlan " + plan_count + ": " + TerminalManageDisplay.FLP_list.get(plan_count).FltNo + " " + TerminalManageDisplay.FLP_list.get(plan_count).DepAD
                                + " " + TerminalManageDisplay.FLP_list.get(plan_count).ArrAD + " " + TerminalManageDisplay.FLP_list.get(plan_count).ACRigNum
                                + " " + TerminalManageDisplay.FLP_list.get(plan_count).ACType + " " + TerminalManageDisplay.FLP_list.get(plan_count).CruAlt + " " + TerminalManageDisplay.FLP_list.get(plan_count).CruIAS
                                + " " + TerminalManageDisplay.FLP_list.get(plan_count).RWYInfo.rWYCode + " " + TerminalManageDisplay.FLP_list.get(plan_count).RWYInfo.magHeading + " " +
                                TerminalManageDisplay.FLP_list.get(plan_count).RWYInfo.thresholdAlt
                                + " " + TerminalManageDisplay.FLP_list.get(plan_count).ArrRWYInfo.rWYCode + " " + TerminalManageDisplay.FLP_list.get(plan_count).ArrRWYInfo.magHeading + " "
                                + " " + TerminalManageDisplay.FLP_list.get(plan_count).ArrRWYInfo.thresholdAlt);
                        plan_count++;
                    }
                    else{
                        System.out.println("Plan execution completed!");
                        break;
                    }
                }
            }
            _hlaWorld.advanceToNextFrame();
//            _hlaPacer.pace();
        }



    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException, InterruptedException, HlaInTimeAdvancingStateException {
        TerminalManageDisplay ds = new TerminalManageDisplay();
        ds.gui();
        main m = new main();
        m.simulate();
        Thread.sleep(30);
    }
}

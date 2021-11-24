package TerminalArea;

import devstudio.generatedcode.*;
import devstudio.generatedcode.datatypes.FederateStateEnum;
import devstudio.generatedcode.datatypes.HLARWYSturct;
import devstudio.generatedcode.datatypes.SimulationPaceEnum;
import devstudio.generatedcode.datatypes.SimulationTimeStruct;
import devstudio.generatedcode.exceptions.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class main {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;
    String FLP_filepath = "./resources/FlightPlan.csv";
    String AD_filepath = "./resources/ADinfo.csv";
    String RWY_filepath = "./resources/RWYinfo.csv";
    long ref_start_time = 0;
    int delta_time = 10;
    String[] APLType = new String[]{"A320", "A350","B737","B747","ARJ21"};
    String[] company = new String[]{"MU", "CGH","SC","MF","RA"};
    int[] initial_Alt = new int[]{7200, 8900};
//    ExerciseRunCommandEnum sim_state = ExerciseRunCommandEnum.ERC_STOP;
    FederateStateEnum sim_state = FederateStateEnum.AS_FROZEN;
    SimulationPaceEnum sim_rate = SimulationPaceEnum.PACE1X;

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

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
            System.out.println("Received master time: " + masterSimulationState.getSimulationTime().timeInSeconds);
            System.out.println("Received pace: " + masterSimulationState.getSimulationPace());
            System.out.println("Received state: " + masterSimulationState.getFederateState());
            sim_state = masterSimulationState.getFederateState();
            sim_rate = masterSimulationState.getSimulationPace();
            ref_start_time = masterSimulationState.getSimulationTime().timeInSeconds;
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

    public ArrayList<ADStruct> getADInfo() throws IOException {
        ArrayList<ADStruct> AD_list = new ArrayList<ADStruct>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(AD_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        String line;
        String id;
        double lon;
        double lat;
        double elevation;
        int num;
        while((line = reader.readLine()) != null){
            ADStruct temp_AD = new ADStruct();
            String item[] = line.split(",");
            id = item[0].trim();
            lon = Double.parseDouble(item[1].trim());
            lat = Double.parseDouble(item[2].trim());
            elevation = Double.parseDouble(item[3].trim());
            num = Integer.parseInt(item[4].trim());
            temp_AD.ICAOCodeID = id;
            temp_AD.ADLon = lon;
            temp_AD.ADLat = lat;
            temp_AD.ADElevation = elevation;
            temp_AD.RWYNum = num;
            AD_list.add(temp_AD);
        }
        return AD_list;
    }

    public HashSet<RWYStruct> getRWYInfo() throws IOException {
        HashSet<RWYStruct> RWY_list = new HashSet<RWYStruct>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(RWY_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        String line;
        String RWYCode;
        double MagHeading;
        double lon;
        double lat;
        double alt;
        String CODE_AIRPORT;
        double len;
        double wid;
        while((line = reader.readLine()) != null){
            RWYStruct temp_RWY = new RWYStruct();
            String item[] = line.split(",");
            RWYCode = item[0].trim();
            MagHeading = Double.parseDouble(item[1].trim());
            lon = Double.parseDouble(item[2].trim());
            lat = Double.parseDouble(item[3].trim());
            alt = Double.parseDouble(item[4].trim());
            CODE_AIRPORT = item[5].trim();
            len = Double.parseDouble(item[6].trim());
            wid = Double.parseDouble(item[7].trim());
            temp_RWY.RWYCode = RWYCode;
            temp_RWY.MagHeading = MagHeading;
            temp_RWY.lon = lon;
            temp_RWY.lat = lat;
            temp_RWY.alt = alt;
            temp_RWY.CODE_AIRPORT = CODE_AIRPORT;
            temp_RWY.len = len;
            temp_RWY.wid = wid;
            RWY_list.add(temp_RWY);
        }
        return RWY_list;
    }

    public ArrayList<FLPStruct> generateFLPInfo(int num) throws IOException {
        ArrayList<FLPStruct> FLP_list = new ArrayList<FLPStruct>();
        int i = 0;
        FLPStruct FLP_temp = new FLPStruct();
        String line;
        String rWYCode;
        String temp_ACRNo;
        short magHeading;
        double thresholdLon;
        double thresholdLat;
        double thresholdAlt;

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FLP_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        // 读取第一行，其他自行生成
        line = reader.readLine();
        String item[] = line.split(",");
        FLP_temp.FltNo = item[0].trim();
        FLP_temp.DepAD = item[1].trim();
        FLP_temp.ArrAD = item[2].trim();
        temp_ACRNo = item[3].trim();
        FLP_temp.ACRigNum = temp_ACRNo + "1";
        FLP_temp.ACType = item[4].trim();
        FLP_temp.CruAlt = Short.parseShort(item[5].trim());
        FLP_temp.CruIAS = Short.parseShort(item[6].trim());
        rWYCode = item[7].trim();
        magHeading = Short.parseShort(item[8].trim());
        thresholdLon = Double.parseDouble(item[9].trim());
        thresholdLat = Double.parseDouble(item[10].trim());
        thresholdAlt = Double.parseDouble(item[11].trim());
        FLP_temp.RWYInfo = HLARWYSturct.create(rWYCode, magHeading, thresholdLon, thresholdLat, thresholdAlt);
        rWYCode = item[12].trim();
        magHeading = Short.parseShort(item[13].trim());
        thresholdLon = Double.parseDouble(item[14].trim());
        thresholdLat = Double.parseDouble(item[15].trim());
        thresholdAlt = Double.parseDouble(item[16].trim());
        FLP_temp.ArrRWYInfo = HLARWYSturct.create(rWYCode, magHeading, thresholdLon, thresholdLat, thresholdAlt);

        FLP_list.add(FLP_temp);
        i++;
        // 循环创建num次
        while (i < num){
            FLPStruct FLP_temp_2 = new FLPStruct();
            FLP_temp_2.FltNo = company[getRandomNumberInRange(0,company.length - 1)] + getRandomNumberInRange(1000, 9999);
            FLP_temp_2.DepAD = FLP_temp.DepAD;
            FLP_temp_2.ArrAD = FLP_temp.ArrAD;
            FLP_temp_2.ACRigNum = temp_ACRNo + (i + 1);
            FLP_temp_2.ACType = APLType[getRandomNumberInRange(0, APLType.length - 1)];
            FLP_temp_2.CruAlt = (short) (initial_Alt[getRandomNumberInRange(0, 1)] + 300 * getRandomNumberInRange(0, 4));
            FLP_temp_2.CruIAS = (short) (400 + 10 * getRandomNumberInRange(0, 10));
            FLP_temp_2.RWYInfo = FLP_temp.RWYInfo;
            FLP_temp_2.ArrRWYInfo = FLP_temp.ArrRWYInfo;
            FLP_list.add(FLP_temp_2);
            i++;
        }
        return FLP_list;
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
        int time_count = 0;
        int plan_count = 0;
        _hlaWorld.connect();
        ArrayList<ADStruct> AD_set = getADInfo(); // 用来创建机场实例
//        HashSet<RWYStruct> RWY_set = getRWYInfo(); // 用来存储跑道信息
//        createADEntity(AD_set);
        ArrayList<FLPStruct> FLP_set = generateFLPInfo(50);  // 获取飞行计划
        // 根据放行时间间隔来发送飞行计划：1、监听系统开始状态  2、监听系统时间
         Thread.sleep(30);
        while(true){
            if(sim_state.equals(FederateStateEnum.AS_FROZEN)){

            }
            else if (sim_state.equals(FederateStateEnum.AS_RUNNING)){
                if(time_count == delta_time){
                    // 判断当前是否还有计划
                    if(plan_count < FLP_set.size()){
                        HlaInteractionManager.HlaFlightPlanMsgInteraction fpl = _hlaWorld.getHlaInteractionManager().getHlaFlightPlanMsgInteraction();
                        fpl.setFltNo(FLP_set.get(plan_count).FltNo);
                        fpl.setDepAD(FLP_set.get(plan_count).DepAD);
                        fpl.setArrAD(FLP_set.get(plan_count).ArrAD);
                        fpl.setACRegisteredNum(FLP_set.get(plan_count).ACRigNum);
                        fpl.setACType(FLP_set.get(plan_count).ACType);
                        fpl.setCruAlt(FLP_set.get(plan_count).CruAlt);
                        fpl.setCruIAS(FLP_set.get(plan_count).CruIAS);
                        fpl.setDepRWY(FLP_set.get(plan_count).RWYInfo);
                        fpl.setArrRWY(FLP_set.get(plan_count).ArrRWYInfo);
                        fpl.sendInteraction();
                        System.out.println("Send FlightPlan " + plan_count + ": " + FLP_set.get(plan_count).FltNo + " " + FLP_set.get(plan_count).DepAD
                                + " " + FLP_set.get(plan_count).ArrAD + " " + FLP_set.get(plan_count).ACRigNum
                                + " " + FLP_set.get(plan_count).ACType + " " + FLP_set.get(plan_count).CruAlt + " " + FLP_set.get(plan_count).CruIAS
                                + " " + FLP_set.get(plan_count).RWYInfo.rWYCode + " " + FLP_set.get(plan_count).RWYInfo.thresholdAlt
                                + " " + FLP_set.get(plan_count).ArrRWYInfo.rWYCode + " " + FLP_set.get(plan_count).ArrRWYInfo.thresholdAlt);
                        plan_count++;
                    }
                    System.out.println("Plan execution completed!");
                    time_count = 0;
                }
                else{
                    time_count++;
                }
            }
            _hlaWorld.advanceToNextFrame();
            _hlaPacer.pace();
        }



    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException, InterruptedException, HlaInTimeAdvancingStateException {
        new main().simulate();

        Thread.sleep(30);
    }
}

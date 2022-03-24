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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class main {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;
    String AD_filepath = "./resources/ADinfo.csv";

    String[] ZSSS = new String[]{"S", "L","S","L","S"};
    String[] ZGGG = new String[]{"M", "S","S","H","S"};
    String[] ZBAA = new String[]{"L", "H","S","S","H"};

    static long ref_start_time;
    static long ref_end_time = 0;
    boolean ind_resume = true;
    static HashSet<ADStruct> AD_set;
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
//                System.out.println("Received master time: " + masterSimulationState.getSimulationTime().timeInSeconds);
//                System.out.println("Received pace: " + masterSimulationState.getSimulationPace());
//                System.out.println("Received state: " + masterSimulationState.getFederateState());
                sim_state = masterSimulationState.getFederateState();
                sim_rate = masterSimulationState.getSimulationPace();
                if(ind_resume && TerminalManageDisplay.ind_start){
                    ref_start_time = masterSimulationState.getSimulationTime().timeInSeconds;
                    System.out.println("start time: " + get_cur_time(ref_start_time));
                    ind_resume = false;
                }
                else if(!ind_resume){
                    ref_end_time = masterSimulationState.getSimulationTime().timeInSeconds;
//                    System.out.println("start time: " + ref_start_time);
//                    System.out.println("end time: " + ref_end_time);
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

    public HashSet<ADStruct> getADInfo() throws IOException {
        HashSet<ADStruct> AD_list = new HashSet<ADStruct>();
        HashSet<String> AD_name = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(AD_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        String line;
        String id;
        double lon;
        double lat;
        double elevation;
        while((line = reader.readLine()) != null){
            String item[] = line.split(",");
            id = item[0].trim();
            if(!AD_name.contains(id)){
                lat = Double.parseDouble(item[1].trim());
                lon = Double.parseDouble(item[2].trim());
                elevation = Double.parseDouble(item[3].trim());
                ADStruct temp_AD = new ADStruct();
                temp_AD.ICAOCodeID = id;
                temp_AD.ADLon = lon;
                temp_AD.ADLat = lat;
                temp_AD.ADElevation = elevation;
                temp_AD.time_interval = TerminalManageDisplay.getRandomNumberInRange(10, 180);
                AD_list.add(temp_AD);
                AD_name.add(id);
            }
        }
        return AD_list;
    }

    public void createADEntity(HashSet<ADStruct> AD_set) throws HlaRtiException, HlaNotConnectedException, HlaInternalException, HlaAttributeNotOwnedException, HlaIllegalInstanceNameException, HlaInstanceNameInUseException {
//        HlaAerodromeEntity[] AD_list = new HlaAerodromeEntity[AD_set.size()];
        int i = 0;
        for (ADStruct s:AD_set) {
            if (_hlaWorld.getHlaAerodromeEntityManager().getAerodromeEntityByHlaInstanceName(s.ICAOCodeID) == null){
                HlaAerodromeEntity temp = _hlaWorld.getHlaAerodromeEntityManager().createLocalHlaAerodromeEntity(s.ICAOCodeID, s.ICAOCodeID);
                HlaAerodromeEntityUpdater updater = temp.getHlaAerodromeEntityUpdater();
                updater.sendICAOCodeID();
                updater.setADLat(s.ADLat);
                updater.setADLon(s.ADLon);
                updater.setADElevation(s.ADElevation);
                updater.sendUpdate();
                System.out.println("Create Airport " + temp.getICAOCodeID());
            }
        }
//        return AD_list;
    }

    public static String get_cur_time(long TimeInt){
        TimeInt = TimeInt + 28800;
        long TimeAllSec = TimeInt % 86400;//取当天时间的全秒数
        long TimeHour = (TimeAllSec / 3600);
        long TimeMinite = ((TimeAllSec - 3600 * TimeHour) / 60);
        long TimeSecond = TimeAllSec % 60;
        String TimeHour_s;
        String TimeMinite_s;
        String TimeSecond_s;
        if(TimeHour < 10){
            TimeHour_s = "0" + TimeHour;
        }
        else{
            TimeHour_s = String.valueOf(TimeHour);
        }
        if(TimeMinite < 10){
            TimeMinite_s = "0" + TimeMinite;
        }
        else{
            TimeMinite_s = String.valueOf(TimeMinite);
        }
        if(TimeSecond < 10){
            TimeSecond_s = "0" + TimeSecond;
        }
        else{
            TimeSecond_s = String.valueOf(TimeSecond);
        }
        String format = TimeHour_s + ":" + TimeMinite_s + ":" + TimeSecond_s;
        return format;
    }


    public static FLPStruct getSpecialFLP(ADStruct AD, String type, ArrayList<ADStruct> O_AD){
        FLPStruct FLP_temp = new FLPStruct();
        double temp_dis = 0;
        int ind;
        do{
            ind = TerminalManageDisplay.getRandomNumberInRange(0, AD_set.size()-1);
            temp_dis = TerminalManageDisplay.distance(TerminalManageDisplay.AD_list.get(ind).ADLat,
                    AD.ADLat, TerminalManageDisplay.AD_list.get(ind).ADLon, AD.ADLon);
        } while(temp_dis < 400);
        FLP_temp.FltNo = TerminalManageDisplay.company[TerminalManageDisplay.getRandomNumberInRange(0,
                TerminalManageDisplay.company.length - 1)] + TerminalManageDisplay.getRandomNumberInRange(1000, 9999);
        FLP_temp.DepAD = AD.ICAOCodeID;
        FLP_temp.ArrAD = TerminalManageDisplay.AD_list.get(ind).ICAOCodeID;
        FLP_temp.ACRigNum = "C" + TerminalManageDisplay.getRandomNumberInRange(1,9999);
        List temp_list = TerminalManageDisplay.APL_ref.get(type);
        FLP_temp.type = type;
        FLP_temp.ACType = (String) temp_list.get(TerminalManageDisplay.getRandomNumberInRange(0, temp_list.size() - 1));
        FLP_temp.CruAlt = (short) (TerminalManageDisplay.initial_Alt[TerminalManageDisplay.getRandomNumberInRange(0, 1)]
                + 300 * TerminalManageDisplay.getRandomNumberInRange(0, 4));
        FLP_temp.CruIAS = (short) (400 + 10 * TerminalManageDisplay.getRandomNumberInRange(0, 10));
        FLP_temp.RWYInfo = HLARWYSturct.create(O_AD.get(0).RWYCode, (short) (Short.parseShort(O_AD.get(0).RWYCode) * 10),
                AD.ADLon, AD.ADLat, AD.ADElevation);
        FLP_temp.ArrRWYInfo = HLARWYSturct.create(TerminalManageDisplay.AD_list.get(ind).RWYCode,
                (short) (Short.parseShort(TerminalManageDisplay.AD_list.get(ind).RWYCode) * 10),
                TerminalManageDisplay.AD_list.get(ind).ADLon, TerminalManageDisplay.AD_list.get(ind).ADLat,
                TerminalManageDisplay.AD_list.get(ind).ADElevation);
        return FLP_temp;
    }

    public void updateDisplayData(ADStruct ad, boolean init_state, Object[][] tableValues){
        int count = 0;
        if(init_state){ // 初始化表格，未执行前
            for(FLPStruct q: ad.FLPque){
                if(count >= 5){
                    break;
                }
                else{
                    tableValues[count][0] = q.FltNo;
                    tableValues[count][1] = q.ACType + "(" + q.type + ")";
                    tableValues[count][3] = "待放行";
                    count++;
                }
            }
        }
        else{ // 更新表格，开始执行计划
            // 3条已放行，2条未放行
            if (ad.ReleasedFLP.size() >= 3){
                for(int i = 0; i < 3; i++){
                    tableValues[2-i][0] = ad.ReleasedFLP.get(ad.ReleasedFLP.size() - 1 -i).FltNo;
                    tableValues[2-i][1] = ad.ReleasedFLP.get(ad.ReleasedFLP.size() - 1 -i).ACType + "(" + ad.ReleasedFLP.get(ad.ReleasedFLP.size() - 1 -i).type + ")";
                    tableValues[2-i][2] = ad.ReleasedFLP.get(ad.ReleasedFLP.size() - 1 -i).Release_std_Time;
                    tableValues[2-i][3] = ad.ReleasedFLP.get(ad.ReleasedFLP.size() - 1 -i).time_interval;
                }
                int j = 0;
                for(FLPStruct flp: ad.FLPque){
                    if((j >= 2) || (flp==null)){
                        break;
                    }
                    else{
                        tableValues[3 + j][0] = flp.FltNo;
                        tableValues[3 + j][1] = flp.ACType + "(" + flp.type + ")";
                        tableValues[3 + j][2] = "";
                        tableValues[3 + j][3] = "待放行";
                        j++;
                    }
                }
            }
            else if(ad.ReleasedFLP.size() ==2){
                for(int i = 0; i < 2; i++){
                    tableValues[1-i][0] = ad.ReleasedFLP.get(1-i).FltNo;
                    tableValues[1-i][1] = ad.ReleasedFLP.get(1-i).ACType + "(" + ad.ReleasedFLP.get(1-i).type + ")";
                    tableValues[1-i][2] = ad.ReleasedFLP.get(1-i).Release_std_Time;
                    tableValues[1-i][3] = ad.ReleasedFLP.get(1-i).time_interval;
                }
                int j = 0;
                for(FLPStruct flp: ad.FLPque){
                    if((j >= 3) || (flp==null)){
                        break;
                    }
                    else{
                        tableValues[2 + j][0] = flp.FltNo;
                        tableValues[2 + j][1] = flp.ACType + "(" + flp.type + ")";
                        tableValues[2 + j][2] = "";
                        tableValues[2 + j][3] = "待放行";
                        j++;
                    }
                }
            }
            else if(ad.ReleasedFLP.size() ==1){
                tableValues[0][0] = ad.ReleasedFLP.get(0).FltNo;
                tableValues[0][1] = ad.ReleasedFLP.get(0).ACType + "(" + ad.ReleasedFLP.get(0).type + ")";
                tableValues[0][2] = ad.ReleasedFLP.get(0).Release_std_Time;
                tableValues[0][3] = ad.ReleasedFLP.get(0).time_interval;
                int j = 0;
                for(FLPStruct flp: ad.FLPque){
                    if((j >= 4) || (flp==null)){
                        break;
                    }
                    else{
                        tableValues[1 + j][0] = flp.FltNo;
                        tableValues[1 + j][1] = flp.ACType + "(" + flp.type + ")";
                        tableValues[1 + j][2] = "";
                        tableValues[1 + j][3] = "待放行";
                        j++;
                    }
                }
            }
            }
        }

    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException, InterruptedException, HlaInTimeAdvancingStateException, HlaAttributeNotOwnedException, HlaIllegalInstanceNameException, HlaInstanceNameInUseException {
        int plan_count = 0;
        ADStruct AD_ZSSS = new ADStruct();
        ADStruct AD_ZGGG = new ADStruct();
        ADStruct AD_ZBAA = new ADStruct();
        _hlaWorld.connect();
        AD_set = getADInfo(); // 用来创建机场实例
        createADEntity(AD_set);
        // 根据放行时间间隔来发送飞行计划：1、监听系统开始状态  2、监听系统时间
        Thread.sleep(30);
        for(ADStruct ad: AD_set){
            if(ad.ICAOCodeID.equals("ZSSS")){
                AD_ZSSS = ad;
                for (int i = 0; i < ZSSS.length; i++){
                    ad.FLPque.offer(getSpecialFLP(ad, ZSSS[i], TerminalManageDisplay.AD_RWY_ZSSS));
                }
            }
            if(ad.ICAOCodeID.equals("ZGGG")){
                AD_ZGGG = ad;
                for (int i = 0; i < ZGGG.length; i++){
                    ad.FLPque.offer(getSpecialFLP(ad, ZGGG[i], TerminalManageDisplay.AD_RWY_ZGGG));
                }
            }
            if(ad.ICAOCodeID.equals("ZBAA")){
                AD_ZBAA = ad;
                for (int i = 0; i < ZBAA.length; i++){
                    ad.FLPque.offer(getSpecialFLP(ad, ZBAA[i], TerminalManageDisplay.AD_RWY_ZBAA));
                }
            }
        }

        updateDisplayData(AD_ZSSS, true, TerminalManageDisplay.tableValues_ZSSS);
        updateDisplayData(AD_ZGGG, true, TerminalManageDisplay.tableValues_ZGGG);
        updateDisplayData(AD_ZBAA, true, TerminalManageDisplay.tableValues_ZBAA);

        Thread.sleep(30);
         while(true){
             Thread.sleep(30);
             if(TerminalManageDisplay.ind_start && (!ind_resume)){
                 System.out.println("开始执行放行！");
                 for(ADStruct ad: main.AD_set){
                     ad.ref_time = main.ref_start_time;
                 }
                 break;
             }
         }

        while(true){
            if(sim_state.equals(FederateStateEnum.AS_FROZEN)){

            }
            else if (sim_state.equals(FederateStateEnum.AS_RUNNING)){
                // 遍历一次所有机场
                for(ADStruct ad: AD_set){
                    if (ad.ReleasedFLP.empty()){ // 首发，放行第一架飞机，不需要考虑尾流
                        if((ref_end_time - ad.ref_time) >= ad.time_interval){ // 可以放行
                            FLPStruct temp_FLP = ad.FLPque.poll();
                            if (temp_FLP != null){
                                long time = _hlaWorld.getHlaMasterSimulationStateManager().getMasterSimulationStateByHlaInstanceName("Master").getSimulationTime().timeInSeconds;
                                temp_FLP.Release_std_Time = get_cur_time(time);
                                temp_FLP.time_interval = ad.time_interval;
                                ad.ref_time = time;
                                HlaInteractionManager.HlaFlightPlanMsgInteraction fpl = _hlaWorld.getHlaInteractionManager().getHlaFlightPlanMsgInteraction();
                                fpl.setFltNo(temp_FLP.FltNo);
                                fpl.setDepAD(temp_FLP.DepAD);
                                fpl.setArrAD(temp_FLP.ArrAD);
                                fpl.setACRegisteredNum(temp_FLP.ACRigNum);
                                fpl.setACType(temp_FLP.ACType);
                                fpl.setCruAlt(temp_FLP.CruAlt);
                                fpl.setCruIAS(temp_FLP.CruIAS);
                                fpl.setDepRWY(temp_FLP.RWYInfo);
                                fpl.setArrRWY(temp_FLP.ArrRWYInfo);
                                fpl.sendInteraction();
                                ad.ReleasedFLP.push(temp_FLP);
                                System.out.println(ad.ICAOCodeID + " Send FlightPlan: " + temp_FLP.FltNo + " " + temp_FLP.DepAD
                                        + " " + temp_FLP.ArrAD + " " + temp_FLP.ACRigNum
                                        + " " + temp_FLP.ACType + " " + temp_FLP.CruAlt + " " + temp_FLP.CruIAS
                                        + " " + temp_FLP.RWYInfo.rWYCode + " " + temp_FLP.RWYInfo.magHeading + " " +
                                        temp_FLP.RWYInfo.thresholdAlt
                                        + " " + temp_FLP.ArrRWYInfo.rWYCode + " " + temp_FLP.ArrRWYInfo.magHeading + " "
                                        + " " + temp_FLP.ArrRWYInfo.thresholdAlt);
                                plan_count++;
                                System.out.println("当前机场已执行计划:" + ad.ReleasedFLP.size() + ", 剩余计划:" + ad.FLPque.size());
                                System.out.println("首发！按放行间隔" + ad.time_interval + "秒执行放行。\n 已执行计划："
                                        + plan_count + ", 未执行计划: " + Math.max(0, (TerminalManageDisplay.FLP_list.size()-plan_count)));
                            }
                        }
                    }
                    else{ // 次发，判断尾流间隔和放行间隔的最大值作为放行间隔
                        if (ad.ICAOCodeID.equals("ZSSS") || ad.ICAOCodeID.equals("ZGGG")
                                || ad.ICAOCodeID.equals("ZBAA")){
                            if (ad.FLPque.isEmpty()){
                                // 补充飞行计划
                                if(ad.ICAOCodeID.equals("ZSSS")){
                                    for (int i = 0; i < ZSSS.length; i++){
                                        ad.FLPque.offer(getSpecialFLP(ad, ZSSS[i], TerminalManageDisplay.AD_RWY_ZSSS));
                                    }
                                }
                                if(ad.ICAOCodeID.equals("ZGGG")){
                                    for (int i = 0; i < ZGGG.length; i++){
                                        ad.FLPque.offer(getSpecialFLP(ad, ZGGG[i], TerminalManageDisplay.AD_RWY_ZGGG));
                                    }
                                }
                                if(ad.ICAOCodeID.equals("ZBAA")){
                                    for (int i = 0; i < ZBAA.length; i++){
                                        ad.FLPque.offer(getSpecialFLP(ad, ZBAA[i], TerminalManageDisplay.AD_RWY_ZBAA));
                                    }
                                }
                            }
                        }

                        if(ad.FLPque.peek()==null){
//                            System.out.println( ad.ICAOCodeID + "机场无后续飞行计划！");
                            continue;
                        }
                        FLPStruct temp_FLP = ad.FLPque.peek();
                        String WL_ind = temp_FLP.type + "-" + ad.ReleasedFLP.peek().type;
                        int time_interval = Math.max(TerminalManageDisplay.WTC_ref.get(WL_ind) * 60 +
                                TerminalManageDisplay.getRandomNumberInRange(0,20), ad.time_interval);
                        if((ref_end_time - ad.ref_time) >= time_interval){ // 可以放行
                            if (temp_FLP != null){
                                ad.FLPque.poll();
                                long time = _hlaWorld.getHlaMasterSimulationStateManager().getMasterSimulationStateByHlaInstanceName("Master").getSimulationTime().timeInSeconds;
                                temp_FLP.Release_std_Time = get_cur_time(time);
                                temp_FLP.time_interval = time_interval;
                                ad.ref_time = time;
                                HlaInteractionManager.HlaFlightPlanMsgInteraction fpl = _hlaWorld.getHlaInteractionManager().getHlaFlightPlanMsgInteraction();
                                fpl.setFltNo(temp_FLP.FltNo);
                                fpl.setDepAD(temp_FLP.DepAD);
                                fpl.setArrAD(temp_FLP.ArrAD);
                                fpl.setACRegisteredNum(temp_FLP.ACRigNum);
                                fpl.setACType(temp_FLP.ACType);
                                fpl.setCruAlt(temp_FLP.CruAlt);
                                fpl.setCruIAS(temp_FLP.CruIAS);
                                fpl.setDepRWY(temp_FLP.RWYInfo);
                                fpl.setArrRWY(temp_FLP.ArrRWYInfo);
                                fpl.sendInteraction();
                                System.out.println(ad.ICAOCodeID + " Send FlightPlan: " + temp_FLP.FltNo + " " + temp_FLP.DepAD
                                        + " " + temp_FLP.ArrAD + " " + temp_FLP.ACRigNum
                                        + " " + temp_FLP.ACType + " " + temp_FLP.CruAlt + " " + temp_FLP.CruIAS
                                        + " " + temp_FLP.RWYInfo.rWYCode + " " + temp_FLP.RWYInfo.magHeading + " " +
                                        temp_FLP.RWYInfo.thresholdAlt
                                        + " " + temp_FLP.ArrRWYInfo.rWYCode + " " + temp_FLP.ArrRWYInfo.magHeading + " "
                                        + " " + temp_FLP.ArrRWYInfo.thresholdAlt);
                                ad.ReleasedFLP.push(temp_FLP);
                                plan_count++;
                                System.out.println("当前机场已执行计划:" + ad.ReleasedFLP.size() + ", 剩余计划:" + ad.FLPque.size());
                                System.out.println("次发！按放行间隔" + Math.max(TerminalManageDisplay.WTC_ref.get(WL_ind) * 60, ad.time_interval)
                                        + "秒执行放行。\n 已执行计划：" + plan_count + ", 未执行计划: " + Math.max(0, TerminalManageDisplay.FLP_list.size()-plan_count));
                            }
                        }
                    }
                    updateDisplayData(AD_ZSSS, false, TerminalManageDisplay.tableValues_ZSSS);
                    updateDisplayData(AD_ZGGG, false, TerminalManageDisplay.tableValues_ZGGG);
                    updateDisplayData(AD_ZBAA, false, TerminalManageDisplay.tableValues_ZBAA);
                }
            }
            _hlaWorld.advanceToNextFrame();
//            _hlaPacer.pace();
        }



    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException, InterruptedException, HlaInTimeAdvancingStateException, HlaAttributeNotOwnedException, HlaIllegalInstanceNameException, HlaInstanceNameInUseException {
        TerminalManageDisplay ds = new TerminalManageDisplay();
        ds.gui();
        main m = new main();
        m.simulate();
        Thread.sleep(30);
    }
}

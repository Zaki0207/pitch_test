import devstudio.generatedcode.*;
import devstudio.generatedcode.exceptions.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RecvAPL {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;
//    HlaBaseAircraft[] APL_ZUUU = new HlaBaseAircraft[19];
    String[] APL_ZUUU_name = new String[19];
    int num_APL = 0;
    private final double point_lati1 = 30.602221;
    private final double point_lati2 = 30.513233;
    private final double point_longt1 = 103.926338;
    private final double point_longt2 = 103.96788;
    private final double point_height = 3000;

    private final HlaBaseAircraftListener _baseAircraftListener = new HlaBaseAircraftListener.Adapter() {
        @Override // listen to the leader's attributes(any)
        public void attributesUpdated(HlaBaseAircraft baseAircraft, Set<HlaBaseAircraftAttributes.Attribute> attributes, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
//          if(baseAircraft.getHlaInstanceName().startsWith("O"))
//          System.out.println(baseAircraft.getHlaInstanceName() + ": " + baseAircraft.getLocation3D());
//            System.out.println(baseAircraft.getOrientation().heading);
            if(baseAircraft.hasLocation3D()){
//                if( (baseAircraft.getLocation3D().location2D.latitude > point_lati2) && (baseAircraft.getLocation3D().location2D.latitude < point_lati1) &&
//                        (baseAircraft.getLocation3D().location2D.longitude > point_longt1) ){
//                System.out.println(baseAircraft.getHlaInstanceName() + " in test: " + baseAircraft.getLocation3D() + baseAircraft.getIsFlightTask());
//
//                    System.out.println(baseAircraft.getHlaInstanceName() + " in ZUUU: " + baseAircraft.getLocation3D());}
               if( (baseAircraft.getLocation3D().location2D.latitude > point_lati2) && (baseAircraft.getLocation3D().location2D.latitude < point_lati1) &&
                       (baseAircraft.getLocation3D().location2D.longitude > point_longt1) && (baseAircraft.getLocation3D().location2D.longitude < point_longt2) && (baseAircraft.getLocation3D().altitude < point_height) ){
//                   System.out.println(baseAircraft.getHlaInstanceName() + " in ZUUU: " + baseAircraft.getLocation3D());
                   if (!Arrays.asList(APL_ZUUU_name).contains(baseAircraft.getHlaInstanceName())){ // 进入区间
                        if(num_APL < 20){ //还有空余飞机空间
                            int temp_index = Arrays.asList(APL_ZUUU_name).indexOf(null);
                            if (temp_index > -1){
                                APL_ZUUU_name[temp_index] = baseAircraft.getHlaInstanceName();
                                num_APL += 1;
                                System.out.println(baseAircraft.getHlaInstanceName() + " entered! num_APL: " + num_APL);
                                XPlaneUpdate(_hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(baseAircraft.getHlaInstanceName()) , temp_index);
                            }
                        }
                   }
                   else{
                       XPlaneUpdate(_hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(baseAircraft.getHlaInstanceName()) , Arrays.asList(APL_ZUUU_name).indexOf(baseAircraft.getHlaInstanceName()));
                   }
               }
               else if(Arrays.asList(APL_ZUUU_name).contains(baseAircraft.getHlaInstanceName())){ // 离开区间
                   int temp_index = Arrays.asList(APL_ZUUU_name).indexOf(baseAircraft.getHlaInstanceName());
                   APL_ZUUU_name[temp_index] = null;
                   num_APL -= 1;
                   System.out.println(baseAircraft.getHlaInstanceName() + " leave!!! num_APL: " + num_APL);
               }
            }
        }
    };

    public void XPlaneUpdate(HlaBaseAircraft baseAircraft, int ind_APL){
        try(XPlaneConnect xpc = new XPlaneConnect()){
//            xpc.getDREF("sim/test/test_float");
            double[] posi = new double[] {baseAircraft.getLocation3D().getLocation2D().latitude, baseAircraft.getLocation3D().getLocation2D().longitude, baseAircraft.getLocation3D().altitude + 500, 0, 0, baseAircraft.getOrientation().heading, 1};
            xpc.sendPOSI(posi, ind_APL);
//            Thread.sleep(50);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void XPlaneInit() throws SocketException {
        try(XPlaneConnect xpc = new XPlaneConnect()){
            xpc.getDREF("sim/test/test_float");
            double[] posi = new double[] {30.573038, 103.947993, 400, 0, 0, 90, 1};
            for (int i = 0; i < 20; i++){
                xpc.sendPOSI(posi, i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RecvAPL(){
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
                return "XPlane";
            }

        });
        _hlaWorld.getHlaBaseAircraftManager().addHlaBaseAircraftDefaultInstanceListener(_baseAircraftListener);
        _hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS);
    }

    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, SocketException {
        XPlaneInit();
        _hlaWorld.connect();
        System.out.println("Simualtor start!");
        while (true){
//            for (int i = 0; i < 19; i++){
//                if(APL_ZUUU_name[i] != null){
//                    HlaBaseAircraft baseAircraft = _hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(APL_ZUUU_name[i]);
//                    XPlaneUpdate(baseAircraft, i);
//                    System.out.println(APL_ZUUU_name[i] + " updated in XPlane");
//                }
//            }
        }
    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, SocketException {
        new RecvAPL().simulate();
        System.out.println();

    }


}

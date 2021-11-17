package DataManage;

import devstudio.generatedcode.*;
import devstudio.generatedcode.datatypes.ATCCmdTypeFlightEnum;
import devstudio.generatedcode.exceptions.*;

import java.util.concurrent.TimeUnit;

public class inter_test {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;

    public inter_test(){
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
                return "REROUTE_Fed";
            }

        });
        _hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS);
    }

    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, InterruptedException, HlaInTimeAdvancingStateException {
        _hlaWorld.connect();
        String name = "TV5288";
        HlaBaseAircraft temp_APL = _hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(name);
        double lat = temp_APL.getLocation3D().location2D.latitude;
        double longt = temp_APL.getLocation3D().location2D.longitude;
        double temp = 0.4;

        while (true){
            HlaInteractionManager.HlaATCCmdMsgFlightInteraction ATCcmdMsg = _hlaWorld.getHlaInteractionManager().getHlaATCCmdMsgFlightInteraction();
            ATCcmdMsg.setGateID(name);
            temp = 0.4;
            ATCcmdMsg.setTargetPointID("point1_33031_" + (lat + temp) + "_" + (longt + temp) + " " +
                    "point2_33032_" + (lat - temp) + "_" + (longt + temp) + " " +
                    "point3_33033_" + (lat - temp) + "_" + (longt - temp) + " " +
                    "point4_33034_" + (lat + temp) + "_" + (longt - temp) + " " +
                    "point5_33035_" + (lat + temp) + "_" + (longt + temp)) ;
            ATCcmdMsg.setMsgType(ATCCmdTypeFlightEnum.WAIT);
            ATCcmdMsg.sendInteraction();
            Thread.sleep(5000);

            HlaInteractionManager.HlaATCCmdMsgFlightInteraction ATCcmdMsg2 = _hlaWorld.getHlaInteractionManager().getHlaATCCmdMsgFlightInteraction();
            ATCcmdMsg2.setGateID(name);
            lat = temp_APL.getLocation3D().location2D.latitude;
            longt = temp_APL.getLocation3D().location2D.longitude;
            temp = 0.5;
            ATCcmdMsg2.setTargetPointID("point1_33031_" + (lat + temp) + "_" + (longt + temp) + " " +
                    "point2_33032_" + (lat - temp) + "_" + (longt + temp) + " " +
                    "point3_33033_" + (lat - temp) + "_" + (longt - temp) + " " +
                    "point4_33034_" + (lat + temp) + "_" + (longt - temp) + " " +
                    "point5_33035_" + (lat + temp) + "_" + (longt + temp)) ;
            ATCcmdMsg2.setMsgType(ATCCmdTypeFlightEnum.REROUTE);
            ATCcmdMsg2.sendInteraction();
            Thread.sleep(5000);
            _hlaWorld.advanceToNextFrame();
            _hlaPacer.pace();
        }
    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaInTimeAdvancingStateException, HlaConnectException, HlaInternalException, InterruptedException {
        new inter_test().simulate();

    }
}

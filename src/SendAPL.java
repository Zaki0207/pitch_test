import devstudio.generatedcode.*;
import devstudio.generatedcode.datatypes.*;
import devstudio.generatedcode.exceptions.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SendAPL {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;

    private final HlaBaseAircraftListener _baseAircraftListener = new HlaBaseAircraftListener.Adapter() {
        @Override // listen to the leader's attributes(any)
        public void attributesUpdated(HlaBaseAircraft baseAircraft, Set<HlaBaseAircraftAttributes.Attribute> attributes, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
            if(baseAircraft.getHlaInstanceName().contains("UAV")) {
                System.out.println(baseAircraft.getHlaInstanceName() + ": " + baseAircraft.getLocation3D());
            }
        }
        };

    public SendAPL() {
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
                return "ATOS102702";
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
        _hlaPacer = HlaPacer.create(50, TimeUnit.MILLISECONDS);
    }

    public void updaterAPL(HlaBaseAircraft[] APL, int i, double[] temp_pos) throws HlaRtiException, HlaNotConnectedException, HlaInternalException, HlaAttributeNotOwnedException {
        HlaBaseAircraftUpdater updater = APL[i].getHlaBaseAircraftUpdater();
        updater.setLocation3D(Location3DStruct.create((float)temp_pos[2], Location2DStruct.create((float)temp_pos[0], (float)temp_pos[1])));
        updater.setOrientation(OrientationStruct.create(0, true, 0, 0, 0));
        updater.setAircraftType("A380");
        updater.setIsFlightTask(true);
        updater.setBelongFederate("XPlaneFed");
        updater.setAirSpeed(AirSpeedStruct.create(300, 300, 300));
        updater.sendUpdate();
    }


    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, HlaIllegalInstanceNameException, HlaInstanceNameInUseException, HlaAttributeNotOwnedException {
        _hlaWorld.connect();
        HlaFederateSimulationState XPlane = _hlaWorld.getHlaFederateSimulationStateManager().createLocalHlaFederateSimulationState("XPlane");
        HlaFederateSimulationStateUpdater XPlaneupdate = XPlane.getHlaFederateSimulationStateUpdater();
        XPlaneupdate.setFederateName("XPlaneFed");
        XPlaneupdate.setFederateState(FederateStateEnum.AS_RUNNING);
        XPlaneupdate.sendUpdate();
        int count = 0;

        System.out.println("Simualtor start!");
        HlaBaseAircraft[] APL = new HlaBaseAircraft[5];
        for(int i = 1; i < 6; i++){
            APL[i-1] = _hlaWorld.getHlaBaseAircraftManager().createLocalHlaBaseAircraft("UAV00" + i,"UAV00" + i);
        }
        try(XPlaneConnect xpc = new XPlaneConnect()){
            xpc.getDREF("sim/test/test_float");
            double[] posi = new double[] {30.524142, 103.939925, 600, 0, 0, 0, 1};
            double[] posi_1 = new double[] {30.524142-0.0008, 103.939925-0.0004, 600, 0, 0, 0, 1}; // -0.0001
            double[] posi_2 = new double[] {30.524142-0.00012, 103.939925-0.0008, 600, 0, 0, 0, 1};
            double[] posi_3 = new double[] {30.524142-0.0008, 103.939925+0.0004, 600, 0, 0, 0, 1}; // +0.0001
            double[] posi_4 = new double[] {30.524142-0.00012, 103.939925+0.0008, 600, 0, 0, 0, 1};
            updaterAPL(APL, 0, posi);
            updaterAPL(APL, 1, posi_1);
            updaterAPL(APL, 2, posi_2);
            updaterAPL(APL, 3, posi_3);
            updaterAPL(APL, 4, posi_4);
            while(true){
                count += 1;
                if(count > 2000){
                    posi = new double[] {30.524142, 103.939925, 600, 0, 0, 0, 1};
                    posi_1 = new double[] {30.524142-0.0008, 103.939925-0.0004, 600, 0, 0, 0, 1}; // -0.0001
                    posi_2 = new double[] {30.524142-0.00012, 103.939925-0.0008, 600, 0, 0, 0, 1};
                    posi_3 = new double[] {30.524142-0.0008, 103.939925+0.0004, 600, 0, 0, 0, 1}; // +0.0001
                    posi_4 = new double[] {30.524142-0.00012, 103.939925+0.0008, 600, 0, 0, 0, 1};
                    count = 0;
                }
                xpc.sendPOSI(posi);
                xpc.sendPOSI(posi_1,1);
                xpc.sendPOSI(posi_2,2);
                xpc.sendPOSI(posi_3,3);
                xpc.sendPOSI(posi_4,4);
                Thread.sleep(30);
                posi[0] += 0.000008;
                posi_1[0] += 0.000008;
                posi_2[0] += 0.000008;
                posi_3[0] += 0.000008;
                posi_4[0] += 0.000008;
                updaterAPL(APL, 0, posi);
                updaterAPL(APL, 1, posi_1);
                updaterAPL(APL, 2, posi_2);
                updaterAPL(APL, 3, posi_3);
                updaterAPL(APL, 4, posi_4);
                _hlaWorld.advanceToNextFrame();
                _hlaPacer.pace();
            }
        } catch (IOException | InterruptedException | HlaAttributeNotOwnedException | HlaInTimeAdvancingStateException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, SocketException, HlaIllegalInstanceNameException, HlaInstanceNameInUseException, HlaAttributeNotOwnedException {
        new SendAPL().simulate();

    }
}

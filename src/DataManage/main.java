package DataManage;

import devstudio.generatedcode.*;
import devstudio.generatedcode.datatypes.ATCCmdTypeFlightEnum;
import devstudio.generatedcode.exceptions.*;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class main {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;

    public byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public byte[] concat(byte[] a, byte[] b, byte[] c, byte[] d, byte[] e, byte[] f) {
        byte[] t= new byte[a.length+b.length+c.length+d.length+e.length+f.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        System.arraycopy(c, 0, t, a.length+b.length, c.length);
        System.arraycopy(d, 0, t, a.length+b.length+c.length, d.length);
        System.arraycopy(e, 0, t, a.length+b.length+c.length+d.length, e.length);
        System.arraycopy(f, 0, t, a.length+b.length+c.length+d.length+e.length, f.length);
        return t;
    }

    public void APLexec(int APL_ind){
        List<HlaBaseAircraft> temp_list =  _hlaWorld.getHlaBaseAircraftManager().getAllHlaBaseAircrafts();
        for (int i = 0; i < temp_list.size(); i++){
            HlaBaseAircraft tempAPL = temp_list.get(i);
            if (_hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(Display.APL_list.get(APL_ind).APLNo)==null){
                break;
            }
            if (Display.APL_list.get(APL_ind).target_type.equals("PFD")){
                int DEST_PORT = Integer.parseInt(Display.APL_list.get(APL_ind).target_port);
                String DEST_IP = Display.APL_list.get(APL_ind).target_ip;
                HlaBaseAircraft temp_APL = _hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(Display.APL_list.get(APL_ind).APLNo);
                byte[] a = double2Bytes(temp_APL.getLocation3D().altitude);
                byte[] b = double2Bytes(0);
                byte[] c = double2Bytes(0);
                byte[] d = double2Bytes(0);
                byte[] e = double2Bytes(0);
                byte[] f = double2Bytes(0);

                if(temp_APL.getOrientation().pitch != 0){
                    b = double2Bytes(temp_APL.getOrientation().pitch);
                }
                if(temp_APL.getOrientation().roll != 0){
                    c = double2Bytes(temp_APL.getOrientation().roll);
                }
                if(temp_APL.getOrientation().heading != 0){
                    d = double2Bytes(temp_APL.getOrientation().heading);
                }
                if(temp_APL.getOrientation().heading != 0){
                    e = double2Bytes(temp_APL.getAirSpeed().trueAirSpeed);
                    f = double2Bytes(temp_APL.getAirSpeed().trueAirSpeed);
                }
                byte[] buff = concat(a,b,c,d,e,f);
                DatagramPacket outPacket = null;
                try{
                    DatagramSocket socket = new DatagramSocket();
                    outPacket = new DatagramPacket(new byte[0], 0, InetAddress.getByName(DEST_IP), DEST_PORT);
                    outPacket.setData(buff);
                    socket.send(outPacket);
                    socket.close();
                } catch (SocketException | UnknownHostException excep) {
                    excep.printStackTrace();
                } catch (IOException excep) {
                    excep.printStackTrace();
                }
                break;
            }

            if (isTraffciAPL(_hlaWorld.getHlaBaseAircraftManager().getBaseAircraftByHlaInstanceName(Display.APL_list.get(APL_ind).APLNo) , tempAPL)){
                int DEST_PORT = Integer.parseInt(Display.APL_list.get(APL_ind).target_port);
                String DEST_IP = Display.APL_list.get(APL_ind).target_ip;
                String text = null;
                // 本机
                if(Display.APL_list.get(APL_ind).APLNo.equals(temp_list.get(i).getHlaInstanceName())){
                    // 判断600/601/602
                    if (Display.APL_list.get(APL_ind).cmdType.equals(ATCCmdTypeFlightEnum.NODATA)){
                        text = "600,," + tempAPL.getHlaInstanceName() + ",," + tempAPL.getLocation3D().getLocation2D().longitude +
                                ",," + tempAPL.getLocation3D().getLocation2D().latitude + ",," + tempAPL.getLocation3D().altitude + ",," +
                                "0,,0,," + tempAPL.getOrientation().heading + ",," + "1" + ",," + "0" + ",," + "ATOS";
                    }
                    else if(Display.APL_list.get(APL_ind).cmdType.equals(ATCCmdTypeFlightEnum.WAIT)){
                        text = "601,," + tempAPL.getHlaInstanceName() + ",," + tempAPL.getLocation3D().getLocation2D().longitude +
                                ",," + tempAPL.getLocation3D().getLocation2D().latitude + ",," + tempAPL.getLocation3D().altitude + ",," +
                                "0,,0,," + tempAPL.getOrientation().heading + ",," + "1" + ",," + Display.APL_list.get(APL_ind).TargetPointID +  ",," + "ATOS";
                    }
                    else if(Display.APL_list.get(APL_ind).cmdType.equals(ATCCmdTypeFlightEnum.REROUTE)){
                        text = "602,," + tempAPL.getHlaInstanceName() + ",," + tempAPL.getLocation3D().getLocation2D().longitude +
                                ",," + tempAPL.getLocation3D().getLocation2D().latitude + ",," + tempAPL.getLocation3D().altitude + ",," +
                                "0,,0,," + tempAPL.getOrientation().heading + ",," + "1" + ",," + Display.APL_list.get(APL_ind).TargetPointID + ",," + "ATOS";
                    }
                }

                // 交通机
                else{
                    text = "603,," + tempAPL.getHlaInstanceName() + ",," + tempAPL.getLocation3D().getLocation2D().longitude +
                            ",," + tempAPL.getLocation3D().getLocation2D().latitude + ",," + tempAPL.getLocation3D().altitude + ",," +
                            "0,,0,," + tempAPL.getOrientation().heading + ",," + "1" + ",," + "0" + ",," + "ATOS";
                }
                sendAPL(DEST_PORT, DEST_IP, text);
                System.out.println("Send to: "+ Display.APL_list.get(APL_ind).APLNo);
                System.out.println(DEST_PORT + " " + DEST_IP);
                System.out.println(text);
            }
        }
    }

    public boolean isTraffciAPL(HlaBaseAircraft ownAPL, HlaBaseAircraft elseAPL){
        boolean ind = false;
        if((Math.abs(ownAPL.getLocation3D().getLocation2D().longitude - elseAPL.getLocation3D().getLocation2D().longitude) < 2) &&
                (Math.abs(ownAPL.getLocation3D().getLocation2D().latitude - elseAPL.getLocation3D().getLocation2D().latitude) < 2)){
            ind = true;
        }
        return ind;
    }

    public static void sendAPL(int DEST_PORT, String DEST_IP, String text){
        DatagramPacket outPacket = null;
        try{
            DatagramSocket socket = new DatagramSocket();
            outPacket = new DatagramPacket(new byte[0], 0, InetAddress.getByName(DEST_IP), DEST_PORT);
            byte[] buff = text.getBytes();
            outPacket.setData(buff);
            socket.send(outPacket);
//            System.out.println("send flight data");
            socket.close();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final HlaBaseAircraftListener _baseAircraftListener = new HlaBaseAircraftListener.Adapter() {
        @Override
        public void attributesUpdated(HlaBaseAircraft baseAircraft, Set<HlaBaseAircraftAttributes.Attribute> attributes, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
            System.out.println(baseAircraft.getHlaInstanceName());
        }
    };

    private final HlaInteractionListener aTCCmdMsg_Flight = new HlaInteractionListener.Adapter() {
        @Override
        public void aTCCmdMsgFlight(boolean local, HlaInteractionManager.HlaATCCmdMsgFlightParameters parameters, HlaTimeStamp timeStamp, HlaLogicalTime logicalTime) {
            String FlyNo = parameters.getGateID();
            String TargetPointID = parameters.getTargetPointID();
            ATCCmdTypeFlightEnum cmdType = parameters.getMsgType();
            System.out.println("Received cmdMsg" + FlyNo+ TargetPointID + cmdType);
            for (int i = 0; i < Display.APL_list.size(); i++){
                if(Display.APL_list.get(i).APLNo.equals(FlyNo)){
                    Display.APL_list.get(i).TargetPointID = TargetPointID;
                    Display.APL_list.get(i).cmdType = cmdType;
                }
            }
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
                return "EFIS_Fed";
            }

        });
        _hlaWorld.getHlaBaseAircraftManager().addHlaBaseAircraftDefaultInstanceListener(_baseAircraftListener);
        _hlaWorld.getHlaInteractionManager().addHlaInteractionListener(aTCCmdMsg_Flight);
        _hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS);
    }

    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, InterruptedException {
        _hlaWorld.connect();
        while (true){
            for (int i = 0; i < Display.APL_list.size(); i++){
                APLexec(i);
            }
            Thread.sleep(300);
        }
    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, InterruptedException {
        new Display().gui();
        new main().simulate();
    }
}

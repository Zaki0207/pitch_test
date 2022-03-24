package ISS_Display;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiveIMThread implements Runnable{
    private DatagramSocket socket;
    APLposition IM_apLposition = new APLposition();
    @Override
    public void run() {
        try{
            socket = new DatagramSocket(main.IM_port);
            while (true){
                byte[] buf = new byte[1024];
                DatagramPacket recv_msg = new DatagramPacket(buf, buf.length);
                socket.receive(recv_msg);
                System.out.println("IM:" + IM_apLposition.longti + " " + IM_apLposition.lat + " " +IM_apLposition.height + " "
                +IM_apLposition.heading + " " + IM_apLposition.roll + " " + IM_apLposition.pitch);
                if(recv_msg.getAddress().getHostAddress().equals(main.IM_ip)){
                    byte[] datas = recv_msg.getData();
                    byte[] temp_data_1 = new byte[4];
                    System.arraycopy(datas, 0, temp_data_1, 0,4);
                    int data_1 = main.bytesToInt(temp_data_1);
                    byte[] temp_data_2 = new byte[8];
                    System.arraycopy(datas, 8, temp_data_2, 0,8);
                    double data_2 = main.bytes2Double(temp_data_2);
                    IM_apLposition.setLongti(data_2);

                    byte[] temp_data_3 = new byte[8];
                    System.arraycopy(datas, 16, temp_data_3, 0,8);
                    double data_3 = main.bytes2Double(temp_data_3);
                    IM_apLposition.setLat(data_3);

                    byte[] temp_data_4 = new byte[8];
                    System.arraycopy(datas, 24, temp_data_4, 0,8);
                    double data_4 = main.bytes2Double(temp_data_4) * 0.3048;
                    IM_apLposition.setHeight(data_4);

                    byte[] temp_data_5 = new byte[8];
                    System.arraycopy(datas, 64, temp_data_5, 0,8);
                    double data_5 = main.bytes2Double(temp_data_5);
                    IM_apLposition.setRoll(data_5);

                    byte[] temp_data_6 = new byte[8];
                    System.arraycopy(datas, 72, temp_data_6, 0,8);
                    double data_6 = main.bytes2Double(temp_data_6);
                    IM_apLposition.setPitch(data_6);

                    byte[] temp_data_7 = new byte[8];
                    System.arraycopy(datas, 80, temp_data_7, 0,8);
                    double data_7 = main.bytes2Double(temp_data_7);
                    IM_apLposition.setHeading(data_7);

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null){
                socket.close();
                System.out.println("Recive IM traffic close...");
            }
        }
    }
}

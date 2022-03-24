package ISS_Display;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiveTGTThread implements Runnable{
    private DatagramSocket socket;
    int num = 18;
    APLposition[] TGT_apLposition_list = new APLposition[num];

    public ReceiveTGTThread(){
        for(int i = 0; i < num; i++){
            TGT_apLposition_list[i] = new APLposition();
        }
    }

    @Override
    public void run() {
        try{
            socket = new DatagramSocket(main.TGT_port);
            while (true){
                byte[] buf = new byte[1024];
                DatagramPacket recv_msg = new DatagramPacket(buf, buf.length);
                socket.receive(recv_msg);
                System.out.println("Received TGT traffic from ip: " + recv_msg.getAddress().getHostAddress());
//                if(recv_msg.getAddress().getHostAddress().equals(main.TGT_ip)){
                    byte[] datas = recv_msg.getData();
                    for(int i = 0; i < num; i++){
                        byte[] temp_data_1 = new byte[16];
                        System.arraycopy(datas, 8 + 56 * i, temp_data_1, 0,8);
                        char[] data_1 = main.getChars(temp_data_1);
                        TGT_apLposition_list[i].setFlightNo(data_1);

                        byte[] temp_data_2 = new byte[8];
                        System.arraycopy(datas, 8 + 56 * i + 8, temp_data_2, 0,8);
                        double data_2 = main.bytes2Double(temp_data_2);
                        TGT_apLposition_list[i].setLongti(data_2);

                        byte[] temp_data_3 = new byte[8];
                        System.arraycopy(datas, 8 + 56 * i + 16, temp_data_3, 0,8);
                        double data_3 = main.bytes2Double(temp_data_3);
                        TGT_apLposition_list[i].setLat(data_3);

                        byte[] temp_data_4 = new byte[8];
                        System.arraycopy(datas, 8 + 56 * i + 24, temp_data_4, 0,8);
                        double data_4 = main.bytes2Double(temp_data_4);
                        TGT_apLposition_list[i].setHeight(data_4);

                        byte[] temp_data_5 = new byte[8];
                        System.arraycopy(datas, 8 + 56 * i + 32, temp_data_5, 0,8);
                        double data_5 = main.bytes2Double(temp_data_5);
                        TGT_apLposition_list[i].setHeading(data_5);

                        byte[] temp_data_6 = new byte[8];
                        System.arraycopy(datas, 8 + 56 * i + 40, temp_data_6, 0,8);
                        double data_6 = main.bytes2Double(temp_data_6);
                        TGT_apLposition_list[i].setPitch(data_6);

                        byte[] temp_data_7 = new byte[8];
                        System.arraycopy(datas, 8 + 56 * i + 48, temp_data_7, 0,8);
                        double data_7 = main.bytes2Double(temp_data_7);
                        TGT_apLposition_list[i].setRoll(data_7);

                        System.out.println("TGT:" + TGT_apLposition_list[i].longti + " " + TGT_apLposition_list[i].lat + " " +TGT_apLposition_list[i].height + " "
                                +TGT_apLposition_list[i].heading + " " + TGT_apLposition_list[i].roll + " " + TGT_apLposition_list[i].pitch);
                    }
//                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null){
                socket.close();
                System.out.println("Recive TGT traffic close...");
            }
        }
    }
}

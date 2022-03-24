package ISS_Display;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Properties;

public class main {
    public static String IM_ip;
    public static String TGT_ip;
    public static int IM_port;
    public static int TGT_port ;

    // 两个线程，一个接本机，一个接交通机
    public static void getProperty() throws IOException{
        Properties prop = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream("./conf/aircraft.properties"));
        prop.load(in);
        IM_ip = prop.getProperty("IM_ip");
        TGT_ip = prop.getProperty("TGT_ip");
        IM_port = Integer.parseInt(prop.getProperty("IM_port"));
        TGT_port = Integer.parseInt(prop.getProperty("TGT_port"));
        in.close();
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    public static int bytesToInt(byte[] bs) {
        int a = 0;
        for (int i = bs.length - 1; i >= 0; i--) {
            a += bs[i] * Math.pow(0xFF, bs.length - i - 1);
        }
        return a;
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    public static void main(String[] args) {
        try {
            getProperty();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReceiveIMThread rIMt = new ReceiveIMThread();
        ReceiveTGTThread rTGTt = new ReceiveTGTThread();

        Thread t1 = new Thread(rIMt);
        Thread t2 = new Thread(rTGTt);

        t1.start();
        t2.start();

        try (XPlaneConnect xpc = new XPlaneConnect()) {
            xpc.getDREF("sim/ISS_Display.test/test_float"); // 在这里停一会
            xpc.getDREF("sim/test/test_float");
//            Thread.sleep(15);
            double[] posi = new double[7];
            xpc.pauseSim(true);
            while (true) {
//                Thread.sleep(10);
                if (rIMt.IM_apLposition.longti > 0.1){ // 本机
                    posi[0] = rIMt.IM_apLposition.lat;
                    posi[1] = rIMt.IM_apLposition.longti;
                    posi[2] = rIMt.IM_apLposition.height;
                    posi[3] = rIMt.IM_apLposition.pitch;
                    posi[4] = rIMt.IM_apLposition.roll;
                    posi[5] = rIMt.IM_apLposition.heading;
                    posi[6] = 0;
                    xpc.sendPOSI(posi);
                    Thread.sleep(10);
                }
                Thread.sleep(10);
                if (rTGTt.TGT_apLposition_list[0].longti > 0.1){
                    for (int i = 0;i < rTGTt.num; i++){
//                        Thread.sleep(5);
                        if (rTGTt.TGT_apLposition_list[i].longti > 0.1){
                            double[] posi_t = new double[7];
                            posi_t[0] = rTGTt.TGT_apLposition_list[i].lat;
                            posi_t[1] = rTGTt.TGT_apLposition_list[i].longti;
                            posi_t[2] = rTGTt.TGT_apLposition_list[i].height;
                            posi_t[3] = rTGTt.TGT_apLposition_list[i].pitch;
                            posi_t[4] = rTGTt.TGT_apLposition_list[i].roll;
                            posi_t[5] = rTGTt.TGT_apLposition_list[i].heading;
                            posi_t[6] = 0;
                            xpc.sendPOSI(posi_t, i+1);
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}

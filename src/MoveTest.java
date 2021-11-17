import java.io.IOException;
import java.net.SocketException;

public class MoveTest {
    public static void main(String[] args) throws SocketException {
        try(XPlaneConnect xpc = new XPlaneConnect()){
            xpc.getDREF("sim/test/test_float");
            double[] posi = new double[] {30.573038, 103.947993, 600, 0, 0, 0, 1};
            double[] posi_1 = new double[] {30.572838, 103.947893, 600, 0, 0, 0, 1};
            double[] posi_2 = new double[] {30.572638, 103.947793, 600, 0, 0, 0, 1};
            double[] posi_3 = new double[] {30.572838, 103.948093, 600, 0, 0, 0, 1};
            double[] posi_4 = new double[] {30.572638, 103.948193, 600, 0, 0, 0, 1};
            while(true){
                xpc.sendPOSI(posi);
                xpc.sendPOSI(posi_1,1);
                xpc.sendPOSI(posi_2,2);
                xpc.sendPOSI(posi_3,3);
                xpc.sendPOSI(posi_4,4);
                Thread.sleep(30);
                posi[0] += 0.000002;
                posi_1[0] += 0.000002;
                posi_2[0] += 0.000002;
                posi_3[0] += 0.000002;
                posi_4[0] += 0.000002;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

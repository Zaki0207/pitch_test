import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        String[] APL_ZUUU_name = new String[19];
        APL_ZUUU_name[8] = "SSS";
        System.out.println(Arrays.asList(APL_ZUUU_name).indexOf("SSS"));
        APL_ZUUU_name = null;
        APL_ZUUU_name[3] = "SSS";
        System.out.println(Arrays.asList(APL_ZUUU_name).indexOf("SSS"));
    }
}

package TerminalArea;

import devstudio.generatedcode.HlaAerodromeEntity;
import devstudio.generatedcode.HlaPacer;
import devstudio.generatedcode.HlaSettings;
import devstudio.generatedcode.HlaWorld;
import devstudio.generatedcode.exceptions.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class main {
    private static final boolean USE_TIME_MANAGEMENT=false;
    private final HlaWorld _hlaWorld;
    private final HlaPacer _hlaPacer;
    String RWY_filepath = "./resources/RWYinfo.csv";
    String AD_filepath = "./resources/ADinfo.csv";

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
                return "TermianlArea_Fed";
            }

        });
        _hlaPacer = HlaPacer.create(1, TimeUnit.SECONDS);
    }

    public HashSet<ADStruct> getADInfo() throws IOException {
        HashSet<ADStruct> AD_list = new HashSet<ADStruct>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(AD_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        String line;
        String id;
        double lon;
        double lat;
        double elevation;
        int num;
        while((line = reader.readLine()) != null){
            ADStruct temp_AD = new ADStruct();
            String item[] = line.split(",");
            id = item[0].trim();
            lon = Double.parseDouble(item[1].trim());
            lat = Double.parseDouble(item[2].trim());
            elevation = Double.parseDouble(item[3].trim());
            num = Integer.parseInt(item[4].trim());
            temp_AD.ICAOCodeID = id;
            temp_AD.ADLon = lon;
            temp_AD.ADLat = lat;
            temp_AD.ADElevation = elevation;
            temp_AD.RWYNum = num;
            AD_list.add(temp_AD);
        }
        return AD_list;
    }

    public HashSet<RWYStruct> getRWYInfo() throws IOException {
        HashSet<RWYStruct> RWY_list = new HashSet<RWYStruct>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(RWY_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        String line;
        String RWYCode;
        double MagHeading;
        double lon;
        double lat;
        double alt;
        String CODE_AIRPORT;
        double len;
        double wid;
        while((line = reader.readLine()) != null){
            RWYStruct temp_RWY = new RWYStruct();
            String item[] = line.split(",");
            RWYCode = item[0].trim();
            MagHeading = Double.parseDouble(item[1].trim());
            lon = Double.parseDouble(item[2].trim());
            lat = Double.parseDouble(item[3].trim());
            alt = Double.parseDouble(item[4].trim());
            CODE_AIRPORT = item[5].trim();
            len = Double.parseDouble(item[6].trim());
            wid = Double.parseDouble(item[7].trim());
            temp_RWY.RWYCode = RWYCode;
            temp_RWY.MagHeading = MagHeading;
            temp_RWY.lon = lon;
            temp_RWY.lat = lat;
            temp_RWY.alt = alt;
            temp_RWY.CODE_AIRPORT = CODE_AIRPORT;
            temp_RWY.len = len;
            temp_RWY.wid = wid;
            RWY_list.add(temp_RWY);
        }
        return RWY_list;
    }

    public HlaAerodromeEntity[] createADEntity(HashSet<ADStruct> AD_set) throws HlaRtiException, HlaNotConnectedException, HlaInternalException {
        HlaAerodromeEntity[] AD_list = new HlaAerodromeEntity[AD_set.size()];
        int i = 0;
        for (ADStruct s:AD_set) {
            AD_list[i] = _hlaWorld.getHlaAerodromeEntityManager().createLocalHlaAerodromeEntity(s.ICAOCodeID);
            i++;
        }
        return AD_list;
    }

    public void simulate() throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException {
        _hlaWorld.connect();
        HashSet<ADStruct> AD_set = getADInfo(); // 用来创建机场实例
        HashSet<RWYStruct> RWY_set = getRWYInfo(); // 用来存储跑道信息
        HlaAerodromeEntity[] AD_list = createADEntity(AD_set);

    }

    public static void main(String[] args) throws HlaRtiException, HlaInvalidLogicalTimeException, HlaFomException, HlaNotConnectedException, HlaConnectException, HlaInternalException, IOException, InterruptedException {
        new main().simulate();

        Thread.sleep(30);
    }
}

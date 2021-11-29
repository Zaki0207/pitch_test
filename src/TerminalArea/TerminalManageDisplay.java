package TerminalArea;

import devstudio.generatedcode.datatypes.HLARWYSturct;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class TerminalManageDisplay {
    static String AD_filepath = "./resources/ADinfo.csv";
    static String[] APLType = new String[]{"A320", "A350","B737","B747","ARJ21"};
    static String[] company = new String[]{"MU", "CGH","SC","MF","RA"};
    static ArrayList<ADStruct> AD_list;
    public static ArrayList<FLPStruct> FLP_list;
    static int[] initial_Alt = new int[]{7200, 8900};
    static boolean ind_start = false;
    static int delta_time;

    public TerminalManageDisplay() throws IOException {
        AD_list = getADInfo();
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2) {  // 返回：km
        final int R = 6371; // 地球半径
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // 单位转换成米
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance) / 1000;
    }

    public ArrayList<ADStruct> getADInfo() throws IOException {
        ArrayList<ADStruct> AD_list = new ArrayList<ADStruct>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(AD_filepath), "utf-8"));
        reader.readLine();//显示标题行,没有则注释掉
        String line;
        String id;
        double lon;
        double lat;
        double elevation;
        String RWYcode;
        while((line = reader.readLine()) != null){
            ADStruct temp_AD = new ADStruct();
            String item[] = line.split(",");
            id = item[0].trim();
            lat = Double.parseDouble(item[1].trim());
            lon = Double.parseDouble(item[2].trim());
            elevation = Double.parseDouble(item[3].trim());
            RWYcode = item[4].trim();
            temp_AD.ICAOCodeID = id;
            temp_AD.ADLon = lon;
            temp_AD.ADLat = lat;
            temp_AD.ADElevation = elevation;
            temp_AD.RWYCode = RWYcode;
            AD_list.add(temp_AD);
        }
        return AD_list;
    }

    public static ArrayList<FLPStruct> generateFLPInfo(int num) throws IOException {
        ArrayList<FLPStruct> FLP_list = new ArrayList<FLPStruct>();
        int i = 0;
        do {
            FLPStruct FLP_temp = new FLPStruct();
            int ind_1 = getRandomNumberInRange(0, AD_list.size()-1);
            int ind_2 = getRandomNumberInRange(0, AD_list.size()-1);
            double temp_dis = distance(AD_list.get(ind_1).ADLat, AD_list.get(ind_2).ADLat, AD_list.get(ind_1).ADLon, AD_list.get(ind_2).ADLon);
            if (temp_dis >= 400){
                FLP_temp.FltNo = company[getRandomNumberInRange(0,company.length - 1)] + getRandomNumberInRange(1000, 9999);
                FLP_temp.DepAD = AD_list.get(ind_1).ICAOCodeID;
                FLP_temp.ArrAD = AD_list.get(ind_2).ICAOCodeID;
                FLP_temp.ACRigNum = "B" + (i + 1);
                FLP_temp.ACType = APLType[getRandomNumberInRange(0, APLType.length - 1)];
                FLP_temp.CruAlt = (short) (initial_Alt[getRandomNumberInRange(0, 1)] + 300 * getRandomNumberInRange(0, 4));
                FLP_temp.CruIAS = (short) (400 + 10 * getRandomNumberInRange(0, 10));
                FLP_temp.RWYInfo = HLARWYSturct.create(AD_list.get(ind_1).RWYCode, (short) (Short.parseShort(AD_list.get(ind_1).RWYCode) * 10),
                        AD_list.get(ind_1).ADLon, AD_list.get(ind_1).ADLat, AD_list.get(ind_1).ADElevation);
                FLP_temp.ArrRWYInfo = HLARWYSturct.create(AD_list.get(ind_2).RWYCode, (short) (Short.parseShort(AD_list.get(ind_2).RWYCode) * 10),
                        AD_list.get(ind_2).ADLon, AD_list.get(ind_2).ADLat, AD_list.get(ind_2).ADElevation);
                FLP_list.add(FLP_temp);
                i++;
            }
        }
        while (i < num);
        return FLP_list;
    }

    public static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel label1_1 = new JLabel("飞行计划:");
        label1_1.setFont(new Font("宋体",Font.BOLD,18));
        label1_1.setBounds(50,20,150,35);
        panel.add(label1_1);

        JTextField text1_1 = new JTextField();
        text1_1.setBounds(190, 20, 120,35);
        text1_1.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text1_1);

        JLabel label1_2 = new JLabel("条");
        label1_2.setFont(new Font("宋体",Font.BOLD,18));
        label1_2.setBounds(320,20,80,35);
        panel.add(label1_2);

        JButton button1_1 = new JButton("生成");
        button1_1.setFont(new Font("宋体",Font.BOLD,14));
        button1_1.setBounds(410, 20, 70,35);
        panel.add(button1_1);
        button1_1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if(text1_1.getText().length() > 0){
                    JOptionPane.showMessageDialog(panel, "生成飞行计划成功！", "提示", JOptionPane.PLAIN_MESSAGE);
                    try {
//                        System.out.println(Integer.parseInt(text1_1.getText()));
                        FLP_list = generateFLPInfo(Integer.parseInt(text1_1.getText()));
                        System.out.println("生成飞行计划" + FLP_list.size() + "条");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    button1_1.setEnabled(false);
                    text1_1.setEnabled(false);
                }
                else{
                    JOptionPane.showMessageDialog(panel, "请输入！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        JLabel label2_1 = new JLabel("放行间隔:");
        label2_1.setFont(new Font("宋体",Font.BOLD,18));
        label2_1.setBounds(50,60,150,35);
        panel.add(label2_1);

        JTextField text2_1 = new JTextField();
        text2_1.setBounds(190, 60, 120,35);
        text2_1.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text2_1);

        JLabel label2_2 = new JLabel("秒");
        label2_2.setFont(new Font("宋体",Font.BOLD,18));
        label2_2.setBounds(320,60,80,35);
        panel.add(label2_2);

        JButton button2_1 = new JButton("执行");
        button2_1.setFont(new Font("宋体",Font.BOLD,14));
        button2_1.setBounds(410, 60, 70,35);
        panel.add(button2_1);
        button2_1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if((text2_1.getText().length() > 0) && !(button1_1.isEnabled())){
                    JOptionPane.showMessageDialog(panel, "设置放行间隔成功！", "提示", JOptionPane.PLAIN_MESSAGE);
                    //                        System.out.println(Integer.parseInt(text1_1.getText()));
                    ind_start = true;
//                        System.out.println(FLP_list.size());
                    button1_1.setEnabled(false);
                    text1_1.setEnabled(false);
                    delta_time = Integer.parseInt(text2_1.getText());
                    System.out.println("执行放行间隔：" + delta_time + "秒");
                }
                else{
                    JOptionPane.showMessageDialog(panel, "请输入！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
    }

    public void gui() {
        JFrame jf = new JFrame("TerminalAreaManage");
        jf.setSize(700,330);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        jf.add(panel);
        placeComponents(panel);
        jf.setVisible(true);
    }

//    public static void main(String[] args) throws IOException {
//        TerminalManageDisplay ds = new TerminalManageDisplay();
//        ds.gui();
//        System.out.println();
//    }
}

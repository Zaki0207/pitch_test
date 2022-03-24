package TerminalArea;

import devstudio.generatedcode.datatypes.HLARWYSturct;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TerminalManageDisplay<model_ZGGG> {
    static String AD_filepath = "./resources/ADinfo.csv";

    static HashMap<String, ArrayList> APL_ref = new HashMap<>();
    static HashMap<String, Integer> WTC_ref = new HashMap<>();

    static String[] APLType = new String[]{"L", "M","H","S"};
    static String[] company = new String[]{"MU", "CA","SC","MF","RA","KN","ZH","CJ","CZ","HU","PN","FM","JR","3Q","3U","WU","GP","FJ",
            "JD","HO","9C","KE","NH"};
    static ArrayList<ADStruct> AD_list;
    public static ArrayList<FLPStruct> FLP_list;
    static int[] initial_Alt = new int[]{7200, 8900};
    static boolean ind_start = false;
    static int delta_time;

    static ArrayList<ADStruct> AD_RWY_ZSSS = new ArrayList<>();
    static ArrayList<ADStruct> AD_RWY_ZGGG = new ArrayList<>();
    static ArrayList<ADStruct> AD_RWY_ZBAA = new ArrayList<>();

    static Object[][] tableValues_ZSSS = new Object[5][4];
    static Object[][] tableValues_ZGGG = new Object[5][4];
    static Object[][] tableValues_ZBAA = new Object[5][4];
    static String[] columnNames = {"航班号", "机型", "放行时间", "放行间隔"};



    public TerminalManageDisplay() throws IOException {
        AD_list = getADInfo();
        APL_ref.put("L", new ArrayList<String>(){
            {
                add("ARJ21");
            }
        });
        APL_ref.put("M", new ArrayList<String>(){
            {
                add("A320");
                add("B737");
            }
        });
        APL_ref.put("H", new ArrayList<String>(){
            {
                add("B777");
                add("A350");
            }
        });
        APL_ref.put("S", new ArrayList<String>(){
            {
                add("A380");
                add("B747");
            }
        });
        // 后机-前机
        WTC_ref.put("S-S", new Integer(2));
        WTC_ref.put("S-H", new Integer(2));
        WTC_ref.put("S-M", new Integer(2));
        WTC_ref.put("S-L", new Integer(2));
        WTC_ref.put("H-S", new Integer(3));
        WTC_ref.put("H-H", new Integer(2));
        WTC_ref.put("H-M", new Integer(2));
        WTC_ref.put("H-L", new Integer(2));
        WTC_ref.put("M-S", new Integer(4));
        WTC_ref.put("M-H", new Integer(3));
        WTC_ref.put("M-M", new Integer(2));
        WTC_ref.put("M-L", new Integer(2));
        WTC_ref.put("L-S", new Integer(5));
        WTC_ref.put("L-H", new Integer(4));
        WTC_ref.put("L-M", new Integer(3));
        WTC_ref.put("L-L", new Integer(2));
    }

    public static int getRandomNumberInRange(int min, int max) {
        if (min > max) {
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
            if(id.equals("ZSSS")){
                AD_RWY_ZSSS.add(temp_AD);
            }
            if(id.equals("ZGGG")){
                AD_RWY_ZGGG.add(temp_AD);
            }
            if(id.equals("ZBAA")){
                AD_RWY_ZBAA.add(temp_AD);
            }
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
            if(AD_list.get(ind_1).ICAOCodeID.equals("ZSSS") || AD_list.get(ind_1).ICAOCodeID.equals("ZGGG")
                    || AD_list.get(ind_1).ICAOCodeID.equals("ZBAA")){
            }
            else{
                double temp_dis = distance(AD_list.get(ind_1).ADLat, AD_list.get(ind_2).ADLat, AD_list.get(ind_1).ADLon, AD_list.get(ind_2).ADLon);
                if (temp_dis >= 400){
                    FLP_temp.FltNo = company[getRandomNumberInRange(0,company.length - 1)] + getRandomNumberInRange(1000, 9999);
                    FLP_temp.DepAD = AD_list.get(ind_1).ICAOCodeID;
                    FLP_temp.ArrAD = AD_list.get(ind_2).ICAOCodeID;
                    FLP_temp.ACRigNum = "B" + (i + 1);
                    String type = APLType[getRandomNumberInRange(0, APLType.length - 1)];
                    FLP_temp.type = type;
                    List temp_list = APL_ref.get(type);
                    FLP_temp.ACType = (String) temp_list.get(getRandomNumberInRange(0, temp_list.size() - 1));
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
        }
        while (i < num);
        return FLP_list;
    }

    public static void setColumnColor(JTable table, Object[][] tablevalue) {
        try
        {
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer(){
                private static final long serialVersionUID = 1L;
                public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus,int row, int column){
                    if(tablevalue[row][3]!=null){
                        if(tablevalue[row][3].equals("待放行"))
                            setBackground(Color.lightGray);//设置偶数行底色
                        else
                            setBackground(Color.green);//设置奇数行底色
                    }
                    return super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
                }
            };
            for(int i = 0; i < table.getColumnCount(); i++) {
                table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
            }
            tcr.setHorizontalAlignment(JLabel.CENTER);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void placeComponents(JPanel panel) {
        panel.setLayout(null);
        JTable table_ZSSS = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table_ZGGG = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table_ZBAA = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel model_ZSSS = new DefaultTableModel(tableValues_ZSSS, columnNames);
        DefaultTableModel model_ZGGG = new DefaultTableModel(tableValues_ZGGG, columnNames);
        DefaultTableModel model_ZBAA = new DefaultTableModel(tableValues_ZBAA, columnNames);


        table_ZSSS.setModel(model_ZSSS);
        table_ZGGG.setModel(model_ZGGG);
        table_ZBAA.setModel(model_ZBAA);


        Timer timer = new Timer(50,new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                table_ZBAA.setModel(new DefaultTableModel(tableValues_ZBAA, columnNames));
                table_ZSSS.setModel(new DefaultTableModel(tableValues_ZSSS, columnNames));
                table_ZGGG.setModel(new DefaultTableModel(tableValues_ZGGG, columnNames));
                setColumnColor(table_ZSSS, tableValues_ZSSS);
                setColumnColor(table_ZBAA, tableValues_ZBAA);
                setColumnColor(table_ZGGG, tableValues_ZGGG);
            }
        });
        timer.start();

        table_ZSSS.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table_ZSSS.setRowHeight(33);
        table_ZSSS.setFont(new Font(Font.DIALOG,Font.PLAIN,14));
        table_ZSSS.getTableHeader().setFont(new Font("宋体",Font.BOLD,14));
        table_ZSSS.getTableHeader().setPreferredSize(new Dimension(1,30));
        //居中对齐
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table_ZSSS.setDefaultRenderer(Object.class, r);

        //放入滚动条
        JScrollPane scrollPane = new JScrollPane(table_ZSSS);
        scrollPane.setBounds(550, 40, 500,200);   // 表格位置
        panel.add(scrollPane);

        JLabel label_ZSSS = new JLabel("上海虹桥");
        label_ZSSS.setFont(new Font("宋体",Font.BOLD,16));
        label_ZSSS.setBounds(670,5,80,35);
        panel.add(label_ZSSS);

        table_ZGGG.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table_ZGGG.setRowHeight(33);
        table_ZGGG.setFont(new Font(Font.DIALOG,Font.PLAIN,14));
        table_ZGGG.getTableHeader().setFont(new Font("宋体",Font.BOLD,14));
        table_ZGGG.getTableHeader().setPreferredSize(new Dimension(1,30));
        //居中对齐
        DefaultTableCellRenderer r_ZGGG = new DefaultTableCellRenderer();
        r_ZGGG.setHorizontalAlignment(JLabel.CENTER);
        table_ZGGG.setDefaultRenderer(Object.class, r_ZGGG);

        //放入滚动条
        JScrollPane scrollPane_ZGGG = new JScrollPane(table_ZGGG);
        scrollPane_ZGGG.setBounds(550, 40+250, 500,200);   // 表格位置
        panel.add(scrollPane_ZGGG);

        JLabel label_ZGGG = new JLabel("广州白云");
        label_ZGGG.setFont(new Font("宋体",Font.BOLD,16));
        label_ZGGG.setBounds(670,5+250,80,35);
        panel.add(label_ZGGG);

        table_ZBAA.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table_ZBAA.setRowHeight(33);
        table_ZBAA.setFont(new Font(Font.DIALOG,Font.PLAIN,14));
        table_ZBAA.getTableHeader().setFont(new Font("宋体",Font.BOLD,14));
        table_ZBAA.getTableHeader().setPreferredSize(new Dimension(1,30));
        //居中对齐
        DefaultTableCellRenderer r_ZBAA = new DefaultTableCellRenderer();
        r_ZBAA.setHorizontalAlignment(JLabel.CENTER);
        table_ZBAA.setDefaultRenderer(Object.class, r_ZBAA);

        //放入滚动条
        JScrollPane scrollPane_ZBAA = new JScrollPane(table_ZBAA);
        scrollPane_ZBAA.setBounds(550, 40+500, 500,200);   // 表格位置
        panel.add(scrollPane_ZBAA);

        JLabel label_ZBAA = new JLabel("首都机场");
        label_ZBAA.setFont(new Font("宋体",Font.BOLD,16));
        label_ZBAA.setBounds(670,5+500,80,35);
        panel.add(label_ZBAA);


        JLabel label1_1 = new JLabel("飞行计划:");
        label1_1.setFont(new Font("宋体",Font.BOLD,18));
        label1_1.setBounds(50,20+250,150,35);
        panel.add(label1_1);

        JTextField text1_1 = new JTextField();
        text1_1.setBounds(190, 20+250, 120,35);
        text1_1.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text1_1);

        JLabel label1_2 = new JLabel("条");
        label1_2.setFont(new Font("宋体",Font.BOLD,18));
        label1_2.setBounds(320,20+250,80,35);
        panel.add(label1_2);

        JButton button1_1 = new JButton("生成");
        button1_1.setFont(new Font("宋体",Font.BOLD,14));
        button1_1.setBounds(410, 20+250, 70,35);
        panel.add(button1_1);
        button1_1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if(text1_1.getText().length() > 0){
                    try {
//                        System.out.println(Integer.parseInt(text1_1.getText()));
                        FLP_list = generateFLPInfo(Integer.parseInt(text1_1.getText()));
                        System.out.println("生成飞行计划" + FLP_list.size() + "条");
                        for(FLPStruct flp: FLP_list){
                            for(ADStruct ad: main.AD_set){
                                if (flp.DepAD.equals(ad.ICAOCodeID)){
                                    ad.FLPque.offer(flp);
                                    break;
                                }
                            }
                        }
                        System.out.println("计划配对机场完成！");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(panel, "生成飞行计划成功！", "提示", JOptionPane.PLAIN_MESSAGE);
                    button1_1.setEnabled(false);
                    text1_1.setEnabled(false);
                }
                else{
                    JOptionPane.showMessageDialog(panel, "请输入！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        JLabel label3_1 = new JLabel("机场:");
        label3_1.setFont(new Font("宋体",Font.BOLD,18));
        label3_1.setBounds(50,60+250,150,35);
        panel.add(label3_1);

        JTextField text3_1 = new JTextField();
        text3_1.setBounds(190, 60+250, 120,35);
        text3_1.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text3_1);

        JLabel label3_2 = new JLabel();
        label3_2.setFont(new Font("宋体",Font.BOLD,18));
        label3_2.setBounds(320,60+250,80,35);
        panel.add(label3_2);


        JLabel label2_1 = new JLabel("放行间隔:");
        label2_1.setFont(new Font("宋体",Font.BOLD,18));
        label2_1.setBounds(50,100+250,150,35);
        panel.add(label2_1);

        JTextField text2_1 = new JTextField();
        text2_1.setBounds(190, 100+250, 120,35);
        text2_1.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text2_1);

        JButton button3_1 = new JButton("选择");
        button3_1.setFont(new Font("宋体",Font.BOLD,14));
        button3_1.setBounds(410, 60+250, 70,35);
        panel.add(button3_1);
        button3_1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if(text3_1.getText().length() > 0){
                    int ind = 0;
//                    JOptionPane.showMessageDialog(panel,  text3_1.getText() + "放行间隔设置为" + text2_1.getText() + "秒", "提示", JOptionPane.PLAIN_MESSAGE);
                    for(ADStruct ad: main.AD_set){
                        if(ad.ICAOCodeID.equals(text3_1.getText())){
                            label3_2.setText(ad.time_interval + "秒");
                            ind = 1;
                            break;
                        }
                    }
                    if(ind==0){
                        JOptionPane.showMessageDialog(panel,  "无该机场", "提示",JOptionPane.PLAIN_MESSAGE);
                    }
                    else if (ind == 1){
                        JOptionPane.showMessageDialog(panel,  "选择成功！", "提示",JOptionPane.PLAIN_MESSAGE);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(panel, "请输入！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        JLabel label2_2 = new JLabel("秒");
        label2_2.setFont(new Font("宋体",Font.BOLD,18));
        label2_2.setBounds(320,100+250,80,35);
        panel.add(label2_2);

        JButton button5_1 = new JButton("设置");
        button5_1.setFont(new Font("宋体",Font.BOLD,14));
        button5_1.setBounds(410, 100+250, 70,35);
        panel.add(button5_1);
        button5_1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if(text2_1.getText().length() > 0){
                    JOptionPane.showMessageDialog(panel,  text3_1.getText() + "放行间隔设置为" + text2_1.getText() + "秒", "提示", JOptionPane.PLAIN_MESSAGE);
                    for(ADStruct ad: main.AD_set){
                        if(ad.ICAOCodeID.equals(text3_1.getText())){
                            ad.time_interval = Integer.parseInt(text2_1.getText());
                            label3_2.setText(ad.time_interval + "秒");
                            break;
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(panel, "请输入！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        JButton button4_1 = new JButton("执行");
        button4_1.setFont(new Font("宋体",Font.BOLD,14));
        button4_1.setBounds(190, 140+250, 70,35);
        panel.add(button4_1);
        button4_1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!button1_1.isEnabled()){
                    JOptionPane.showMessageDialog(panel, "执行成功！", "提示", JOptionPane.PLAIN_MESSAGE);
                    ind_start = true;
                }
                else{
                    JOptionPane.showMessageDialog(panel, "请生成飞行计划！", "提示", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });





    }

    public void gui() {
        JFrame jf = new JFrame("TerminalAreaManage");
        jf.setSize(1300,800);
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

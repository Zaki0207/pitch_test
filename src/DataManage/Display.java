package DataManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Display {
    static ArrayList<APL_struct> APL_list = new ArrayList<APL_struct>();

    public static int isInArray(String FltNo, String Type){ // 返回-1则是不在，返回正数则是索引
        int index = -1;
        for(int i = 0; i < APL_list.size(); i++){
            if ((FltNo.equals(APL_list.get(i).APLNo)) && (Type.equals(APL_list.get(i).target_type))){
                index = i;
                break;
            }
        }
        return index;
    }

    public static String[] getStringArray(){
        String[] temp = new String[APL_list.size()];
        for (int i = 0; i < APL_list.size(); i++){
            temp[i] = APL_list.get(i).APLNo + " " + APL_list.get(i).target_type + " " + APL_list.get(i).target_ip + " " + APL_list.get(i).target_port;
        }
        return temp;
    }

    public static void placeComponents(JPanel panel){
        panel.setLayout(null);
        JLabel label1 = new JLabel("航班号:");
        label1.setFont(new Font("宋体",Font.BOLD,18));
        label1.setBounds(10,20,80,35);
        panel.add(label1);

        JTextField text1 = new JTextField();
        text1.setBounds(120, 20, 120,35);
        text1.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text1);

        JLabel label2 = new JLabel("发送目标:");
        label2.setFont(new Font("宋体",Font.BOLD,18));
        label2.setBounds(10,60,100,35);
        panel.add(label2);

        JComboBox combobox1 = new JComboBox();
        combobox1.addItem("CDTI");
        combobox1.addItem("PFD");
        combobox1.setBounds(120, 60, 120,35);
        panel.add(combobox1);

        JLabel label3 = new JLabel("目标ip:");
        label3.setFont(new Font("宋体",Font.BOLD,18));
        label3.setBounds(10,100,100,35);
        panel.add(label3);

        JTextField text2 = new JTextField();
        text2.setBounds(120, 100, 120,35);
        text2.setFont(new Font("宋体",Font.PLAIN,14));
        panel.add(text2);

        JLabel label4 = new JLabel("目标端口:");
        label4.setFont(new Font("宋体",Font.BOLD,18));
        label4.setBounds(10,140,100,35);
        panel.add(label4);

        JTextField text3 = new JTextField();
        text3.setBounds(120, 140, 120,35);
        text3.setFont(new Font("宋体",Font.PLAIN,18));
        panel.add(text3);

        JList list = new JList();
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBounds(300, 20, 300,150);
        scrollPane.setPreferredSize(new Dimension(300,150));
        scrollPane.setViewportView(list);
//        list.setListData(new String[]{"aaa","bbb","bbb","bbb","bbb","bbb","bbb","bbb","bbb","bbb"});
        list.setBounds(300, 20, 300,150);
        panel.add(scrollPane);

        JButton button1 = new JButton("添加");
        button1.setFont(new Font("宋体",Font.BOLD,14));
        button1.setBounds(80, 200, 70,40);
        panel.add(button1);
        button1.addActionListener(new ActionListener() { // 添加
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((e.getSource() == button1) && (text1.getText().length() > 0) && (text2.getText().length() > 0) && (text3.getText().length() > 0)) {// 判断触发源是否为按钮
                    int i = isInArray(text1.getText(), (String)combobox1.getSelectedItem());
                    if (i != -1){
                        System.out.println("update " + APL_list.get(i).APLNo + " " + APL_list.get(i).target_type);
                        APL_list.get(i).target_ip = text2.getText();
                        APL_list.get(i).target_port = text3.getText();
                        list.setListData(getStringArray());
                    }
                    else if (i == -1){
                        APL_struct temp = new APL_struct();
                        temp.APLNo = text1.getText();
                        temp.target_type = (String)combobox1.getSelectedItem();
                        temp.target_ip = text2.getText();
                        temp.target_port = text3.getText();
                        APL_list.add(temp);
                        System.out.println("add " + temp.APLNo + " " + temp.target_type);
                        list.setListData(getStringArray());
                    }
                    text1.setText("");
                    text2.setText("");
                    text3.setText("");
                    combobox1.setSelectedItem("CDTI");
                }
            }
        });

        JButton button2 = new JButton("删除");
        button2.setFont(new Font("宋体",Font.BOLD,14));
        button2.setBounds(180, 200, 70,40);
        panel.add(button2);
        button2.addActionListener(new ActionListener() { // 删除
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == button2) {
                    int temp_ind = list.getSelectedIndex();
                    APL_list.remove(temp_ind);
                    list.setListData(getStringArray());
                }

            }
        });

        JButton button3 = new JButton("重置");
        button3.setFont(new Font("宋体",Font.BOLD,14));
        button3.setBounds(280, 200, 70,40);
        panel.add(button3);
        button3.addActionListener(new ActionListener() { // 删除
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == button3) {
                    APL_list.clear();
                    list.setListData(getStringArray());
                }

            }
        });
    }


    public static void gui() {
        JFrame jf = new JFrame("DataManage");
        jf.setSize(700,400);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        jf.add(panel);
        placeComponents(panel);
        jf.setVisible(true);
    }
}

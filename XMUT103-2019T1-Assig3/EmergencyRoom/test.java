import ecs100.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*; 
public class test{
    //field
    JFrame Graphics = new JFrame();
    Set<JButton> button = new HashSet<JButton>();
    JFrame buttonPanel = new JFrame();
    JPanel jpanel = new JPanel() {
        public void paint(Graphics graphics) {
            super.paint(graphics);
            graphics.drawOval(100, 70, 30, 30);// 头部（画圆形）
            graphics.drawRect(105, 100, 20, 30);// 身体（画矩形）
            graphics.drawLine(105, 100, 75, 120);// 左臂（画直线）
            graphics.drawLine(125, 100, 150, 120);// 右臂（画直线）
            graphics.drawLine(105, 130, 75, 150);// 左腿（画直线）
            graphics.drawLine(125, 130, 150, 150);// 右腿（画直线）
        }
    };
    public test(){
        buttonPanel.setLayout(new FlowLayout());
        this.setUpGUI();
        Graphics.setVisible(true);
        Graphics.setSize(500, 500);
        buttonPanel.setSize(500, 500);
        buttonPanel.setVisible(true);
        // Graphics.add(buttonPanel);
        Graphics.add(jpanel);
    }
    public void setUpGUI(){
        this.addButton("Line", ()->{this.drawLine(100,100,100,100);});
        this.addButton("2", ()->{this.drawLine(100,100,100,100);});
        this.addButton("3", ()->{this.drawLine(100,100,100,100);});
        this.addButton("4", ()->{this.drawLine(100,100,100,100);});
        for(JButton b:button){
            buttonPanel.getContentPane().add(b);
        }
    }
    public void drawLine(int x,int y,int l,int w){
        JPanel panel = new JPanel() {
            public void paint(Graphics graphics) {
                super.paint(graphics);
                graphics.drawLine(x,y,l,w);
            }
        };
        Graphics.remove(jpanel);
        Graphics.add(panel);
    }
    public void addButton(String name,UIButtonListener controller){
        JButton j = new JButton(name);        
        j.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                controller.buttonPerformed();
                drawLine(100,100,100,100);
                System.out.println("!!!");
            }
        });
        button.add(j);
    }
    public static void main(String[] args) {
        new test();
    }
}

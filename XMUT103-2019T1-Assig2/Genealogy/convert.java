import ecs100.*;
import java.util.*;
public class convert{
   public convert(){
       UI.addTextField("Binary to decimal", this::BtoD);
       UI.addTextField("Decimal to binary", this::DtoB);
       UI.addTextField("Decimal to Hexdecimal", this::DtoH);
       UI.addTextField("Hexdecimal to decimal", this::HtoD);
       UI.addTextField("Enter integer number", this::IntegerToSign);
       UI.addButton("Integer to 1's Complement", this::IntegerTo1s);
       UI.addButton("Integer to 2's Complement", this::IntegerTo2s);
       UI.addButton("Clear", UI::clearText);
       UI.addButton("Quit", UI::quit);
   }
   public void BtoD(String s){
       long sum = 0;
       for(int i = 0;i<s.length();i++){
           if(s.substring(i, i+1).equals("1")){
               int x = 1;   int times = s.length() - i - 1;
               for(int j = 1;j<=times;j++){
                   x = 2*x;
               }
               sum = sum + x;
           }else if(s.substring(i, i+1).equals("0")){
               
           }else{
               UI.println("You entered a wrong binary number!");
               sum = 0;
               return;
           }
       }
       UI.println(sum);
   }
   ArrayList<Integer> binary = new ArrayList<Integer>();
   public void DtoB(String s){
       int num = Integer.valueOf(s);
       binary = new ArrayList<Integer>();
       while(num!=0){
           int b = num%2;
           binary.add(b);
           num = (int)num/2;
       }
       Collections.reverse(binary);
       UI.println(binary.toString());
   }
   public void HtoD(String s){
       long sum = 0;
       int t = 1;
       for(int i = 0;i<s.length();i++){
           if(s.substring(i, i+1).equals("0")){
               t = 0;
           }else if(s.substring(i, i+1).equals("1")){
               t = 1;
           }else if(s.substring(i, i+1).equals("2")){
               t = 2;
           }else if(s.substring(i, i+1).equals("3")){
               t = 3;
           }else if(s.substring(i, i+1).equals("4")){
               t = 4;
           }else if(s.substring(i, i+1).equals("5")){
               t = 5;
           }else if(s.substring(i, i+1).equals("6")){
               t = 6;
           }else if(s.substring(i, i+1).equals("7")){
               t = 7;
           }else if(s.substring(i, i+1).equals("8")){
               t = 8;
           }else if(s.substring(i, i+1).equals("9")){
               t = 9;
           }else if(s.substring(i, i+1).equalsIgnoreCase("A")){
               t = 10;
           }else if(s.substring(i, i+1).equalsIgnoreCase("B")){
               t = 11;
           }else if(s.substring(i, i+1).equalsIgnoreCase("C")){
               t = 12;
           }else if(s.substring(i, i+1).equalsIgnoreCase("D")){
               t = 13;
           }else if(s.substring(i, i+1).equalsIgnoreCase("E")){
               t = 14;
           }else if(s.substring(i, i+1).equalsIgnoreCase("F")){
               t = 15;
           }
           else{
               UI.println("You entered a wrong binary number!");
               sum = 0;
               return;
           }
           int x = 1;   int times = s.length() - i - 1;
           for(int j = 1;j<=times;j++){
               x = 16*x;
           }
           sum = sum + x*t;
       }
       UI.println("Hex: " + s);
       UI.println("Dec: " + sum);
       UI.println();
   }
   public void DtoH(String s){
       int num = Integer.valueOf(s);
       ArrayList<String> hex = new ArrayList<String>();
       while(num!=0){
           int b = num%16;
           String bit = null;
           if(b == 10){
               bit = "A";
           }else if(b == 11){
               bit = "B";
           }else if(b == 11){
               bit = "B";
           }else if(b == 12){
               bit = "C";
           }else if(b == 13){
               bit = "D";
           }else if(b == 14){
               bit = "E";
           }else if(b == 15){
               bit = "F";
           }else{
               bit = b + "";
           }
           hex.add(bit + "");
           num = (int)num/16;
       }
       UI.println("Decemal: " + s);
       UI.print("Hex: ");
       for(int i = hex.size()-1;i>=0;i--){
           UI.print(hex.get(i));
       }
       UI.println();
   }
   public void IntegerToSign(String s){
       boolean Pos = true;
       int num  = 0;
       if(s.startsWith("-")){
           Pos = false;
           num  = -Integer.valueOf(s);
       }else{
           num = Integer.valueOf(s);
       }
       binary = new ArrayList<Integer>();
       while(num!=0){
           int b = num%2;
           binary.add(b);
           num = (int)num/2;
       }
       if(Pos){
           binary.add(0);
       }else{
           binary.add(1);
       }
       Collections.reverse(binary);
       UI.println("Signed Magnitude");
       UI.println(binary.toString());
   }
   boolean Fs = false;
   public void IntegerTo1s(){
       int length = binary.size();
       for(int i = 0;i<4-length%4;i++){
           binary.add(0,0);
       }
       for(int i = 0;i<binary.size();i++){
           if(binary.get(i) == 0){
               binary.set(i, 1);
           }else if(binary.get(i) == 1){
               binary.set(i, 0);
           }
       }
       Fs = true;
       UI.println("1's complement");
       UI.println(binary.toString());
   }
   public void IntegerTo2s(){
       if(Fs){
           Collections.reverse(binary);
           binary.set(0, binary.get(0) + 1);
           int i = 0;
           while(binary.get(i) == 2){
               binary.set(i,0);
               binary.set(i + 1,binary.get(i) + 1);
               i++;
           }
           Collections.reverse(binary);
       }else{
        
       }
       UI.println("2's complement");
       UI.println(binary.toString());
   }
   public static void main(String [] args){
       new convert();
   }
}

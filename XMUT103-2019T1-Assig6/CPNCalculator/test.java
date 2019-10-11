import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;
public class test{
    private ArrayList<Integer> data = new ArrayList<Integer>();
    public test(){
        UI.addButton("SetArray", this::intPutArray);
        UI.addTextField("Index of", this::doIndexOf);
    }
    public void intPutArray(){
        while(true){
            int a = UI.askInt("Enter a number(666 means you done all input)");
            if(a == 666){break;}
            data.add(a);
        }
        UI.println("Finished");
        UI.print("You entered:");
        UI.println(data.toString());
        Collections.sort(data);
        UI.print("Sorted:");
        UI.println(data.toString());
    }
    public int indexOf(List<Integer> data,int start,int end,int key){
        int mid = (end - start) / 2 + start;   
        if (data.get(mid) == key) {   
            return mid;   
        }   
        if (start >= end) {   
            return -1;   
        } else if (key > data.get(mid)) {   
            return indexOf(data, mid + 1, end, key);   
        } else if (key < data.get(mid)) {   
            return indexOf(data, start, mid - 1, key);   
        }   
        return -1;
    }
    public void doIndexOf(String v){
        int number = Integer.valueOf(v);
        UI.println("Number " + number);
        UI.println("index: " + this.indexOf(data, 0, this.data.size()-1, number));
    }
    public static void main(String[] args){
        new test();
    }
}

import ecs100.*;
import java.awt.Color;

import java.util.*;
public class test{
    private Set<Integer> set = new HashSet<Integer>();
    public test(){
        UI.addButton("add", this::addSet);
        UI.addButton("print", this::print);
    }
    public void addSet(){
        while(true){
            int num = UI.askInt("Enter:");
            if(num == 666){
                break;
            }else{
                set.add(num);
            }
        }
    }
    public void print(){
        for(Integer i:this.set){
            UI.print(i);
        }
        UI.println(set.toString());
    }
    public static void main(String[] args){
        new test();
    }
}

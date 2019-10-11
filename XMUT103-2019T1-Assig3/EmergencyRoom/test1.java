import ecs100.*;
import java.util.*;
public class test1{
    public test1(){
        UI.addButton("Test", this::test);
    }
    public void test(){
        String s = UI.askString("Enter:");
        CharSequence input = s;
        UI.println(this.checkBrackets(input));
    }
    public boolean isOpenBracket(char c){
        return(c == '{'||c == '('||c == '<'||c == '[');
    }
    public boolean isCloseBracket(char c){
        return(c == '}'||c == ')'||c == '>'||c == ']');
    }
    public boolean matches(char opening,char closing){
        return((opening == '{'&&closing == '}')||
            (opening == '('&&closing == ')')||
            (opening == '['&&closing == ']')||
            (opening == '<'&&closing == '>')
        );
    }
    public boolean checkBrackets(CharSequence input){
        Stack<Character> open = new Stack<Character>();
        for(int i = 0;i<input.length();i++){
            char c = input.charAt(i);
            if(this.isOpenBracket(c)){
                open.push(c);
            }else if(this.isCloseBracket(c)){
                if(open.isEmpty()){
                    return false;
                }else if(this.matches(open.peek(), c)){
                    open.pop();
                }
            }
        }
        if(open.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public void main(String []args){
        new test1();
    }
}

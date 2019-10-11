// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 6
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

/** 
 * Calculator for Cambridge-Polish Notation expressions
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template is based on the version in the lectures,
 *  which only handled + - * /, and did not do any checking
 *  for valid expressions
 * The program should handle a wider range of operators and
 *  check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    /**
     * Main Read-evaluate-print loop
     * Reads an expression from the user then evaluates it and prints the result
     * Invalid expressions could cause errors when reading.
     * The try-catch prevents these errors from crashing the programe - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public static void main(String[] args){
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            Scanner sc = new Scanner(UI.askString("expr:"));
            try {
                GTNode<String> expr = readExpr(sc);  
                if(sc.hasNext()){
                    UI.print("End reading early, the rest of expression is : ");
                    while(sc.hasNext()){
                        UI.print(sc.next());
                    }
                    UI.println("You might have one more closed brackets in the middle");
                }
                printExpr(expr,null);
                UI.println(" -> " + evaluate(expr));
            }catch(Exception e){UI.println("invalid expression"+e);}
        }
    }

    /**
     * Recursively construct expression tree from scanner input
     */
    public static GTNode<String> readExpr(Scanner sc){
        if (sc.hasNext("\\(")) {                     // next token is an opening bracket
            sc.next();                               // the opening (
            String op = sc.next();                   // the operator
            if(op.equals(")")){UI.println("An empty bracket");return null;}
            GTNode<String> node = new GTNode<String>(op);  
            while (! sc.hasNext("\\)")){
                GTNode<String> child = readExpr(sc); // the arguments
                if(child == null){return null;}
                node.addChild(child);          
            }
            sc.next();                               // the closing )
            return node;
        }
        else { 
            // next token must be a number
            if(!sc.hasNext()){UI.println("Missed a closed brackets in the end");return null;}
            String number = sc.next();
            if(number.equalsIgnoreCase("PI")){
                number = Math.PI+"";
            }else if(number.equalsIgnoreCase("E")){
                number = Math.E+"";
            }
            return new GTNode<String>(number);
        }
    }
    //static String lastOperator;
    public static void printExpr(GTNode<String> expr,String lastOperator){
        if (expr==null){ return;}
        if (expr.numberOfChildren()==0){            // must be a number
            try { 
                double num = Double.parseDouble(expr.getItem());
                if(num<0){
                    UI.print("(" + num + ")");
                }else{
                    UI.print(num);
                }
            }
            catch(Exception e){return;}
        }else{
            String operator = expr.getItem();
            if(!operator.equals("+")&&!operator.equals("-")&&!operator.equals("*")&&!operator.equals("/")&&!operator.equals("^")){
                UI.print(operator);
                UI.print("(");
                for(GTNode<String> child: expr) {
                    printExpr(child,operator);
                }
                UI.print(")");
            }else if(needBrack(operator,lastOperator)){
                UI.print("(");
                for(int i=0; i<expr.numberOfChildren()-1; i++){
                    printExpr(expr.getChild(i),operator);
                    UI.print(operator);
                }
                printExpr(expr.getChild(expr.numberOfChildren()-1),operator);
                UI.print(")");
            }else{
                for(int i=0; i<expr.numberOfChildren()-1; i++){
                    printExpr(expr.getChild(i),operator);
                    UI.print(operator);
                }
                printExpr(expr.getChild(expr.numberOfChildren()-1),operator);
            }
        } 
    }
    public static boolean needBrack(String operator,String lastOperator){
        if(operator.equals("-")||operator.equals("+")){
            if(lastOperator!=null&&!lastOperator.equals("+")&&!lastOperator.equals("-")){
                return true;
            }
        }
        // if(lastOperator!=null&&lastOperator.equals("*")&&lastOperator.equals("/")){
            // if(operator.equals("+")||operator.equals("-")){
                // return true;
            // }
        // }
        return false;
    }
    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     */
    public static double evaluate(GTNode<String> expr){
        if (expr==null){ return Double.NaN; }
        if (expr.numberOfChildren()==0){            // must be a number
            try { return Double.parseDouble(expr.getItem());}
            catch(Exception e){return Double.NaN;}
        }
        else {
            double ans = Double.NaN;                // answer if no valid operator
            if (expr.getItem().equals("+")){        // addition operator
                if(expr.numberOfChildren()<2){ UI.println("+ needs at least two numbers");return Double.NaN;}
                ans = 0;
                for(GTNode<String> child: expr) {
                    ans += evaluate(child);
                }
            }
            else if (expr.getItem().equals("*")) {  // multiplication operator 
                if(expr.numberOfChildren()<2){ UI.println("* needs at least two numbers");return Double.NaN;}
                ans = 1;
                for(GTNode<String> child: expr) {
                    ans *= evaluate(child);
                }
            }
            else if (expr.getItem().equals("-")){  // subtraction operator 
                if(expr.numberOfChildren()<2){ UI.println("- needs at least two numbers");return Double.NaN;}
                ans = evaluate(expr.getChild(0));
                for(int i=1; i<expr.numberOfChildren(); i++){
                    ans -= evaluate(expr.getChild(i));
                }
            }
            else if (expr.getItem().equals("/")){  // division operator   
                if(expr.numberOfChildren()<2){ UI.println("/ needs at least two numbers");return Double.NaN;}
                ans = evaluate(expr.getChild(0));
                for(int i=1; i<expr.numberOfChildren(); i++){
                    ans /= evaluate(expr.getChild(i));
                }
            }
            /*# YOUR CODE HERE */
            else if (expr.getItem().equals("sqrt")){    
                if(expr.numberOfChildren()!=1){ UI.println("sqrt only needs one number!");return Double.NaN;}
                ans = Math.sqrt(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("^")){  
                if(expr.numberOfChildren()<2){ UI.println("^ needs at least two numbers");return Double.NaN;}
                ans = evaluate(expr.getChild(0));
                for(int i=1; i<expr.numberOfChildren(); i++){
                    ans = Math.pow(ans, evaluate(expr.getChild(i)));
                }
            }else if (expr.getItem().equals("ln")){  
                if(expr.numberOfChildren()!=1){ UI.println("ln only needs one number!");return Double.NaN;}
                ans = Math.log(evaluate(expr.getChild(0)));
                //ans = Math.log1p(evaluate(expr.getChild(0))-1);
            }else if (expr.getItem().equals("log")){  
                if(expr.numberOfChildren()!=1){ UI.println("log only needs one number!");return Double.NaN;}
                ans = Math.log10(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("sin")){  
                if(expr.numberOfChildren()!=1){ UI.println("sin only needs one number!");return Double.NaN;}
                ans = Math.sin(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("cos")){
                if(expr.numberOfChildren()!=1){ UI.println("cos only needs one number!");return Double.NaN;}
                ans = Math.cos(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("tan")){   
                if(expr.numberOfChildren()!=1){ UI.println("tan only needs one numbers!");return Double.NaN;}
                ans = Math.tan(evaluate(expr.getChild(0)));
            }else if (expr.getItem().equals("dist")){    
                if(expr.numberOfChildren()!=2){ UI.println("dist needs two number!");return Double.NaN;}
                double x = Math.abs(evaluate(expr.getChild(0))-evaluate(expr.getChild(2)));
                double y = Math.abs(evaluate(expr.getChild(1))-evaluate(expr.getChild(3)));
                ans = Math.sqrt(x*x+y*y);
            }else if (expr.getItem().equals("avg")){  
                ans = 0;
                for(GTNode<String> child: expr) {
                    ans += evaluate(child);
                }
                if(expr.numberOfChildren()!=0){
                    ans = ans/expr.numberOfChildren();
                }else{
                    ans = 0;
                }
            }else{
                UI.println("Operator : " + expr.getItem() + " is invalid");
                ans = Double.NaN;
            }
            return ans; 
        }
    }

}


// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 3
 * Name:Eric
 * Username:xmut_1712409237
 * ID:1712409237
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*; 
/**
 * Simulation of an EmergencyRoom,
 * The Emergency room has a collection of departments for treating patients (ER beds, X-ray,
 *  Operating Theatre, MRI, Ultrasound, etc).
 * 
 * When patients arrive at the emergency room, they are immediately assessed by the
 *  triage team who determine the priority of the patient and a sequence of treatments
 *  that the patient will need.
 *
 * Each department has
 *  - a Set of patients that they are currently treating,
 *    (There is a maximum size of this set for each department)
 *  - a Queue for the patients waiting for that department.
 *
 * The departments should be in a Map, with the department name (= treatment type) as the key.
 * 
 * When a patient has finished a treatment, they should be moved to the
 *   department for the next treatment they require.
 *
 * When a patient has finished all their treatments, they should be discharged:
 *  - a record of their total time, treatment time, and wait time should be printed,
 *  - the details should be added to the statistics. 
 *
 *
 * The main simulation should consist of
 * a setting up phase which initialises all the queues,
 * a loop that steps through time:
 *   - advances the time by one "tick"
 *   - Processes one time tick for each patient currently in each department
 *     (either making them wait if they are on the queue, or
 *      advancing their treatment if they are being treated)
 *   - Checks for any patients who have completed their current treatment,
 *      and remove from the department
 *   - Move all Patients that completed a treatment to their next department (or discharge them)
 *   - Checks each department, and moves patients from the front of the
 *       waiting queues into the Sets of patients being treated, if there is room
 *   - Gets any new patient that has arrived (depends on arrivalInterval) and
 *       puts them on the appropriate queue
 *   - Redraws all the departments - showing the patients being treated, and
 *     the patients waiting in the queues
 *   - Pauses briefly
 *
 * The simple simulation just has one department - ER beds that can treat 5 people at once.
 * Patients arrive and need treatment for random times.
 */

public class EmergencyRoom{

    private Map<String, Department> departments = new HashMap<String, Department>();
    private boolean running = false;

    // fields controlling the probabilities.
    private int arrivalInterval = 5;   // new patient every 5 ticks, on average
    private double probPri1 = 0.1; // 10% priority 1 patients
    private double probPri2 = 0.2; // 20% priority 2 patients
    private Random random = new Random();  // The random number generator.
    private int time = 0; // The simulated time
    private Map<String,ArrayList<int[]>> numWithTime = new HashMap<String,ArrayList<int[]>>();
    /**
     * Construct a new EmergencyRoom object, setting up the GUI
     */
    public EmergencyRoom(){
        setupGUI();
        reset();
    }
    Set<JButton> button = new HashSet<JButton>();
    Set<JButton> reportButton = new HashSet<JButton>();
    public JButton addButton(String name,UIButtonListener controller){
        JButton j = UI.addButton(name, controller);
        button.add(j);
        return j;
    }
    public JButton addReportButton(String name,UIButtonListener controller){   
        JButton j = UI.addButton(name, controller);
        reportButton.add(j);
        return j;
    }
    JButton start;
    public void setupGUI(){
        this.addButton("Reset", this::reset);
        start = this.addReportButton("Start",()->{this.run();});
        this.addButton("Stop and ShowReport",()->{this.reportStatistics();});
        UI.addSlider("Av arrival interval", 1, 50, arrivalInterval,
            (double val)-> {arrivalInterval = (int)val;});
        UI.addSlider("Prob of Pri 1", 1, 100, probPri1*100,
            (double val)-> {probPri1 = val/100;});
        UI.addSlider("Prob of Pri 2", 1, 100, probPri2*100,
            (double val)-> {probPri2 = Math.min(val/100,1-probPri1);});
        this.addReportButton("DrawPerson", this::drawWaitingPerson);
        this.addReportButton("Time for waiting", this::drawP3);
        this.addReportButton("Graph", this::drawGraph);
        this.addReportButton("CurrentPatients'time", this::drawAverageOfCurrentPatient_Time);
        this.addReportButton("AllPatients'time", this::drawAverageOfAllPatient_Time);
        this.addButton("Quit", UI::quit);
        UI.setWindowSize(1500,800);
        UI.setDivider(0.5);
        this.setButton();
    }

    public void setButton(){
        for(JButton b:this.button){
            b.setBackground(Color.white);
            b.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){
                    Color col = Color.getHSBColor((float)Math.random(),1,1);
                    b.setBackground(col);
                }
                public void mouseExited(MouseEvent e){
                    b.setBackground(Color.white);
                }
            });
        }
        for(JButton b:this.reportButton){
            b.setBackground(Color.white);
            b.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){
                    Color col = Color.getHSBColor((float)Math.random(),1,1);
                    b.setBackground(col);
                }
                public void mouseExited(MouseEvent e){
                    b.setBackground(Color.white);
                }
            });
            b.setVisible(false);
        }
        start.setVisible(true);
    }
    
    /**
     * Define the departments available and put them in the map of departments.
     * Each department needs to have a name and a maximum number of patients that
     * it can be treating at the same time.
     * Simple version: just a collection of 5 ER beds.
     */
    public void reset(){
        UI.clearGraphics();
        UI.clearText();
        running=false;
        time = 0;
        departments.put("ER beds",           new Department("ER beds", 8));
        departments.put("Operating Theatre", new Department("Operating Theatre", 2));
        departments.put("X-ray",             new Department("X-ray", 2));
        departments.put("MRI",               new Department("MRI", 1));
        departments.put("Ultrasound",        new Department("Ultrasound", 3));
        departments.put("ICU",        new Department("ICU", 2));
        for(String name:departments.keySet()){
            this.completedPatients.put(name, new HashSet<Patient>());
        }
        f.setContentPane(reportPane);
        f.setLayout(new FlowLayout());
        f.setSize(800, 600);
        numWithTime = new HashMap<String,ArrayList<int[]>>();
        for(String name:departments.keySet()){
            departmentsReport.add(new JTextArea(name,10,30));
            numWithTime.put(name,new ArrayList<int[]>());
        }
        for(JTextArea JTA:departmentsReport){
            reportPane.add(new JScrollPane(JTA));
            JTA.setEditable(false);
        }
        p = new int[3];
    }
    /*# Creating a new frame and adding all departments as a text area*/
    JFrame f = new JFrame("Report");
    JPanel reportPane = new JPanel();
    ArrayList<JTextArea> departmentsReport = new ArrayList<JTextArea>();
    public void report(){
        //running = false;
        int i = 0;
        for(String name : departments.keySet()){
            departments.get(name).report(departmentsReport.get(i));
            i++;
        }
    }
    /**
     * Main loop of the simulation
     */
    public void run(){
        running = true;
        for(JButton b:button){
           b.setVisible(true);
        }
        for(JButton b:reportButton){
            b.setVisible(false);
        }
        while (running){
            // Hint: if you are stepping through a set, you can't remove
            //   items from the set inside the loop!
            //   If you need to remove items, you can add the items to a
            //   temporary list, and after the loop is done, remove all 
            //   the items on the temporary list from the set.
            /*# YOUR CODE HERE */
            for (String dept : new String[]{"ER beds","Operating Theatre", "X-ray", "Ultrasound", "MRI","ICU"}){
                /*#Geting all departments objects and then get the currentPatients collection.*/
                Collection<Patient>  currentPatients = departments.get(dept).getCurrentPatients();
                /*#Using a temporary set to store the current patients*/
                Set<Patient> tempCP = new HashSet<Patient>();
                for(Patient p : currentPatients){tempCP.add(p);}
                /*# Going through the temprary current patients*/
                for(Patient p : tempCP){
                        /*# Cheek if any patients completed their current treatment*/
                        if(p.completedCurrentTreatment()){
                            /*# Cheek if this patient completed all treatments*/
                            if(p.completedAllTreatments()){
                                /*# If it's true,remove this patient form the departments and adding a new patient from the waiting patients*/
                                completedPatients.get(dept).add(p);
                                departments.get(dept).removePatient(p);
                                departments.get(dept).moveFromWaiting();
                                UI.println(p.toString());
                            }else{
                                /*# If it's false,remove this patient and turn him to next treatment*/
                                departments.get(dept).removePatient(p);
                                departments.get(dept).moveFromWaiting();
                                /*# Adding a new patient from the waiting patients*/
                                departments.get(p.getNextTreatment()).addPatient(p);
                                /*# Increase the number of this person*/
                                p.incrementTreatmentNumber();
                            }
                        }else{
                            /*# If this person have not finished this treatment, advance treatment*/
                            p.advanceTreatmentByTick();
                        }
                }
                /*# Getting the waiting patients from the department*/
                Collection<Patient> waitingPatient = departments.get(dept).getWaitingPatients();
                /*#Let all people in waiting set wait for a tick*/
                for(Patient p:waitingPatient){
                    p.waitForATick();
                }
                this.numWithTime.get(dept).add(new int[]{this.departments.get(dept).getCurrentPatients().size(),this.departments.get(dept).getWaitingPatients().size()});
            }
            /*# If the time reaches the specified number*/
            if(this.time % arrivalInterval == 0){
                /*# Creating a new person with random field and adding to his next treatment*/
                int priority  = this.randomPriority();
                Patient p = new Patient(time,priority);
                departments.get(p.getNextTreatment()).addPatient(p);
                allPatient.add(p);
                p.incrementTreatmentNumber();
                report();
            }
            this.time++;
            redraw();
            UI.sleep(300);
        }
        // Stopped
        reportStatistics();
    }
    private ArrayList<Patient> allPatient = new ArrayList<Patient>();
    private Map<String,Set<Patient>> completedPatients = new HashMap<String,Set<Patient>>();
    int[] p = new int[3];
    public void drawGraph(){
        UI.clearGraphics();
        UI.setFontSize(10);
        double arc  = 0;
        for(int i = 0;i<p.length;i++){
            double pArc = arc;
            arc += 360*p[i]/(p[0] + p[1] + p[2]);
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            int j = 0;
            while(j<arc-pArc){
                UI.fillArc(150, 100, 100, 100, pArc, j);
                j+=1;
                UI.sleep(1);
            }
            UI.fillRect(50, 50*i, 20, 20);
            UI.setColor(Color.BLACK);
            UI.drawString("priority" + i+1 + "(" + (int)(100*(arc-pArc)/360)  + "%" +")", 80, 20+50*i - 10);
        }
        UI.drawString("The percentage of the priority of all patients", 100, 210);
        this.drawP();
        this.drawP2();
        this.drawBed();
    }
    public void drawP(){
        double arc = 0;
        int departmentNum = 0;
        for(String name:this.departments.keySet()){
            int personNum = this.departments.get(name).getCurrentPatients().size()+this.departments.get(name).getWaitingPatients().size() + this.completedPatients.get(name).size();
            double pArc = arc;
            arc = arc + 360*personNum/this.allPatient.size();
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            int i =0;
            while(i<arc-pArc){
                UI.fillArc(350, 100, 100, 100, pArc, i);
                i+=1;
                UI.sleep(1);
            }
            UI.fillRect(250, 35*departmentNum, 20, 20);
            UI.setColor(Color.BLACK);
            UI.drawString(name + "(" + (int)(100*(arc - pArc)/360) + "%" + ")", 280, 20+35*departmentNum - 10);
            departmentNum++;
        }
        UI.drawString("The percentage of the patient in each department", 350, 210);
        UI.drawString("(Including the completed patient)", 350, 220);
    }
    public void drawP2(){
        double arc = 0;
        int departmentNum = 0;
        int allCurrentNum = 0;
        int sum = 0;
        for(String name:this.departments.keySet()){
            sum += this.departments.get(name).getMax();
        }
        for(String name:this.departments.keySet()){
            allCurrentNum = allCurrentNum + this.departments.get(name).getWaitingPatients().size() + this.departments.get(name).getCurrentPatients().size();
        }
        for(String name:this.departments.keySet()){
            int personNum = this.departments.get(name).getCurrentPatients().size()+this.departments.get(name).getWaitingPatients().size();
            double pArc = arc;
            arc = arc + 360*personNum/allCurrentNum;
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            int i =0;
            while(i<arc-pArc){
                UI.fillArc(150, 300, 100, 100, pArc, i);
                i+=1;
                UI.sleep(1);
            }
            UI.fillRect(50, 220+35*departmentNum, 20, 20);
            UI.setColor(Color.BLACK);
            UI.drawString(name + "(" + (int)(100*(arc - pArc)/360) + "%" + ")", 80, 240+35*departmentNum - 10);
            if((departments.get(name).getMax()/sum) - (arc-pArc)/360 > 0.2){
                this.departmentsReport.get(departmentNum).append("\n" + "The percentage of the patient in each department : " + (int)(100*(arc-pArc)/360)+"%");
                this.departmentsReport.get(departmentNum).append("\n" +"The percentage of the number of bed in all departments : " + (int)(100*departments.get(name).getMax()/sum)+"%");
                this.departmentsReport.get(departmentNum).append("\n" +"So the  " + name + " should add more beds.");
                Date date = new Date();
                this.departmentsReport.get(departmentNum).append("\n" + date.toString());
                this.departmentsReport.get(departmentNum).setCaretPosition(this.departmentsReport.get(departmentNum).getText().length());
                this.f.setVisible(true);
            }else if((arc-pArc)/360 - (departments.get(name).getMax()/sum)  > 0.2){
                this.departmentsReport.get(departmentNum).append("\n" +"The percentage of the patient in each department : " + (int)(100*(arc-pArc)/360) + "%");
                this.departmentsReport.get(departmentNum).append("\n" +"The percentage of the number of bed in all departments : " + (int)(100*departments.get(name).getMax()/sum)+"%");
                this.departmentsReport.get(departmentNum).append("\n" +"So the  " + name + " has more beds than need");
                Date date = new Date();
                this.departmentsReport.get(departmentNum).append("\n" + date.toString());
                this.departmentsReport.get(departmentNum).setCaretPosition(this.departmentsReport.get(departmentNum).getText().length());
                this.f.setVisible(true);
            }
            departmentNum++;
        }
        UI.drawString("The percentage of the patient in each department", 100, 420);
        UI.drawString("(Just for the current patient)", 100, 430);
    }
    public void drawBed(){
        UI.setFontSize(14);
        UI.drawString("The number of the beds of each department : ", 350, 250);
        int i = 1;
        int sum = 0;
        for(String name:this.departments.keySet()){
            sum += this.departments.get(name).getMax();
        }
        for(String name:this.departments.keySet()){
            UI.drawString(name + " : " + this.departments.get(name).getMax()  + "(" +(int)(100*this.departments.get(name).getMax()/sum) +"% In total)", 350, 250 + 15*i);
            i++;
        }
    }
    private int BASE_GROUND = 500;
    private int LEFT = 100;
    public void drawBase(){
        UI.setFontSize(10);
        UI.setColor(Color.BLACK);
        this.running = false;
        UI.clearGraphics();
        UI.drawLine(LEFT-50, BASE_GROUND, LEFT-50, 0);
        UI.drawLine(LEFT-50, BASE_GROUND, 1000, BASE_GROUND);
        UI.drawString("Departments'name:", 0, BASE_GROUND+10);
    }
    public void drawWaitingPerson(){
        this.drawBase();
        int departmentNum = 0;
        int max = Integer.MIN_VALUE;
        for(String name:new String[]{"ER beds","Operating Theatre", "X-ray", "Ultrasound", "MRI","ICU"}){
            int waitingNum = this.departments.get(name).getWaitingPatients().size();
            int currentNum = this.departments.get(name).getCurrentPatients().size();
            if(max < waitingNum){
                max = waitingNum;
            }
            if(max < currentNum){
                max = currentNum;
            }
        }
        if(max == 0){
            max = 1;
        }
        UI.drawString("Left:number of current patient", LEFT + 50, 50);
        UI.drawString("Right:number of waiting patient", LEFT + 50, 60);
        for(String name:this.departments.keySet()){
            UI.drawString(name, LEFT + departmentNum*100 - name.length() , BASE_GROUND + 10);
            int currentNum = (300*this.departments.get(name).getCurrentPatients().size()) / max;
            int waitingNum = (300*this.departments.get(name).getWaitingPatients().size()) / max;
            UI.println(name + " current :" + this.departments.get(name).getCurrentPatients().size() + " waiting : " + this.departments.get(name).getWaitingPatients().size());
            int i = 0;
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            UI.drawString(this.departments.get(name).getCurrentPatients().size()+"", LEFT + departmentNum*100,  BASE_GROUND - currentNum);
            while(i<currentNum){
                UI.fillRect(LEFT + departmentNum*100, BASE_GROUND - i , 30, i);
                i++;
                UI.sleep(1);
            }
            i = 0;
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            UI.drawString(this.departments.get(name).getWaitingPatients().size()+"", LEFT + departmentNum*100 + 30, BASE_GROUND - waitingNum);
            while(i<waitingNum){
                UI.fillRect(LEFT + departmentNum*100 + 30, BASE_GROUND - i , 30, i);
                i++;
                UI.sleep(1);
            }
            departmentNum++;
        }
    }
    public void drawAverageOfCurrentPatient_Time(){
        this.drawBase();
        int departmentNum = 0;
        for(String name:this.departments.keySet()){
            Department d = this.departments.get(name);
            UI.drawString(name, LEFT + departmentNum*100 - name.length() , BASE_GROUND + 10);
            int allCurrentWaitTime = 0;
            int allCurrentTotalTime = 0;
            for(Patient p : d.getWaitingPatients()){
                allCurrentWaitTime = allCurrentWaitTime + p.waitingTime();
                allCurrentTotalTime = allCurrentTotalTime + p.treatmentTime();
            }
            for(Patient p : d.getCurrentPatients()){
                allCurrentWaitTime = allCurrentWaitTime + p.waitingTime();
                allCurrentTotalTime = allCurrentTotalTime + p.treatmentTime();
            }
            int avrCW = 0,avrCT = 0;
            if(!((d.getWaitingPatients().size()+d.getCurrentPatients().size()) == 0)){
                avrCW = 10*allCurrentWaitTime/(d.getWaitingPatients().size()+d.getCurrentPatients().size());
                avrCT = 10*allCurrentTotalTime/(d.getWaitingPatients().size()+d.getCurrentPatients().size());
            }
            if(avrCW>=400){
                UI.drawString(avrCW/10+"", LEFT + departmentNum*100, BASE_GROUND - 400);
                avrCW = 400;
                UI.drawString("----"+"", LEFT + departmentNum*100, BASE_GROUND - avrCW-20);
            }else{
                UI.drawString(avrCW/10+"", LEFT + departmentNum*100, BASE_GROUND - avrCW);
            }
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            for(int i=0;i<avrCW;i++){
                UI.fillRect(LEFT + departmentNum*100, BASE_GROUND - i , 30, i);
                UI.sleep(1);
            }
            if(avrCT>=400){
                UI.drawString(avrCT/10+"", LEFT + departmentNum*100 + 30, BASE_GROUND - 400);
                avrCT = 400;
                UI.drawString("----"+"", LEFT + departmentNum*100+30, BASE_GROUND - avrCT-20);
            }else{
                UI.drawString(avrCT/10+"", LEFT + departmentNum*100 + 30, BASE_GROUND - avrCT);
            }
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            for(int i=0;i<avrCT;i++){
                UI.fillRect(LEFT + departmentNum*100+30, BASE_GROUND - i , 30, i);
                UI.sleep(1);
            }
            departmentNum++;
        }
    }
    public void drawAverageOfAllPatient_Time(){
        this.drawBase();
        int departmentNum = 0;
        for(String name:this.departments.keySet()){
            UI.drawString(name, LEFT + departmentNum*100 - name.length() , BASE_GROUND + 10);
            Department d = this.departments.get(name);
            int allWaitTime = 0;
            int allTotalTime = 0;
            for(Patient p: this.completedPatients.get(name)){
                Map<String,int[]> Time = p.getTreatmentsTime();
                int[] time = Time.get(name);
                allWaitTime = allWaitTime + time[0];
                allTotalTime = allTotalTime + allWaitTime + time[1];
            }
            int avrWT = 0,avrTT = 0;
            if(!((d.getWaitingPatients().size()+d.getCurrentPatients().size()) == 0)){
                avrWT = 10*allWaitTime/this.completedPatients.size();
                avrTT = 10*allTotalTime/this.completedPatients.size();
            }
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            if(avrWT>=400){
                UI.drawString(avrWT/10+"", LEFT + departmentNum*10, BASE_GROUND - 400);
                avrWT = 400;
                UI.drawString("----"+"", LEFT + departmentNum*100, BASE_GROUND - avrWT-20);
            }else{
                UI.drawString(avrTT/10+"", LEFT + departmentNum*100, BASE_GROUND - avrWT);
            }
            for(int i=0;i<avrWT;i++){
                UI.fillRect(LEFT + departmentNum*100, BASE_GROUND - i , 30, i);
                UI.sleep(1);
            }
            UI.setColor(new Color((int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble()),(int)(255*this.random.nextDouble())));
            if(avrTT>=400){
                UI.drawString(avrTT/10+"", LEFT + departmentNum*100 + 30, BASE_GROUND - 400);
                avrTT = 400;
                UI.drawString("----"+"", LEFT + departmentNum*100+30, BASE_GROUND - avrTT-20);
            }else{
                UI.drawString(avrTT/10+"", LEFT + departmentNum*100 + 30, BASE_GROUND - avrTT);
            }
            for(int i=0;i<avrTT;i++){
                UI.fillRect(LEFT + departmentNum*100+30, BASE_GROUND - i , 30, i);
                UI.sleep(1);
            }
            departmentNum++;
        }
    }
    public void drawP3(){
        this.running = false;
        UI.clearGraphics();
        UI.setFontSize(13);
        int y = 100;
        UI.setColor(Color.RED);
        UI.invertLine(480,20,500,20);
        UI.setColor(Color.GREEN);
        UI.invertLine(480,40,500,40);
        UI.setColor(Color.BLACK);
        UI.drawString("Current person", 480,35);
        UI.drawString("Waiting person", 480,55);
        UI.drawString("Other color means both of them ", 480,70);
        UI.drawString("Thick lines represent more than 10", 480,85);
        for(String name:this.numWithTime.keySet()){
            UI.setFontSize(13);
            UI.setLineWidth(1);
            int x = 30;
            UI.setColor(Color.BLACK);
            UI.drawLine(x, y, x, y-100);
            UI.drawLine(x, y, x + 500, y);
            UI.drawString(name, x, y+13);
            UI.drawString("time-->", x + 480, y+13);
            int[] First = this.numWithTime.get(name).get(0);
            int temp1 = First[0];
            int temp2 = First[1];
            for(int i = 0;i<this.numWithTime.get(name).size();i++){
                int[] num = this.numWithTime.get(name).get(i);
                UI.setColor(Color.RED);
                if(num[1] >= 10){
                    UI.setLineWidth(2);
                    num[1] = 10;
                }
                UI.invertLine(i*5 +x, y -temp1*10, (i+1)*5 +x ,y - num[0]*10);
                UI.setColor(Color.GREEN);
                UI.invertLine(i*5 +x , y-temp2*10, (i+1)*5 +x ,y - num[1]*10);
                temp1 = num[0];
                temp2 = num[1];
            }
            y = y + 115;
        }
    }
    
    /**
     * Report that a patient has been discharged, along with any
     * useful statistics about the patient
     */
    public void discharge(Patient p){
        UI.println("Discharge: " + p);
    }

    /**
     * Report summary statistics about the simulation
     */
    public void reportStatistics(){
        running=false;
        UI.clearGraphics();
        f.setVisible(true);
        for(JButton b:button){
           b.setVisible(false);
        }
        for(JButton b:reportButton){
            b.setVisible(true);
        }
    }

    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);
        double y = 80;
        for (String dept : new String[]{"ER beds","Operating Theatre", "X-ray", "Ultrasound", "MRI","ICU"}){
            departments.get(dept).redraw(y);
            UI.drawLine(0,y+2,400, y+2);
            y += 50;
        }
    }

    /**  (COMPLETION)
     * Returns a random priority 1 - 3
     * Probability of a priority 1 patient should be probPri1
     * Probability of a priority 2 patient should be probPri2
     * Probability of a priority 3 patient should be (1-probPri1-probPri2)
     */
    public int randomPriority(){
        /*# YOUR CODE HERE */
        double priority  = random.nextDouble();
        if(priority<probPri1){
            p[0] = p[0] + 1;
            return 1;
        }else if(priority < probPri1 + probPri2){
            p[1] = p[1] + 1;
            return 2;
        }else{
            p[2] = p[2] + 1;
            return 3;
        }
    }

    public static void main(String[] arguments){
        new EmergencyRoom();
    }        
}
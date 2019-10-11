// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 3
 * Name:Eric
 * Username:xmut_1712409237
 * ID:17124096237
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import javax.swing.JTextArea;
/**
 * A treatment Department (operating theatre, X-ray room,  ER bed, Ultrasound, etc)
 * Each department has
 * - A name,
 * - A maximum number of patients that can be treated at the same time
 * - A Set of Patients that are currently being treated
 * - A Queue of Patients waiting to be treated.
 *
 * FOR THE CORE AND COMPLETION, YOU CAN COMPLETE THE METHODS HERE,
 * BUT DO NOT MODIFY ANY OF THE CODE THAT IS GIVEN!!!
 */  

public class Department{

    private String name;
    private int maxPatients;
    private Set<Patient> currentPatients;
    private Queue<Patient> waitingPatients;

    /**
     * Construct a new Department object
     * Initialise the waiting queue and the current Set.
     */
    public Department(String name, int max){
        /*# YOUR CODE HERE */
        this.name = name;
        this.maxPatients = max;
        waitingPatients = new PriorityQueue<Patient>();
        currentPatients = new HashSet<Patient>(maxPatients);
    }
    public int getMax(){
        return this.maxPatients;
    }
    /**
     * Return the collection of patients currently being treated
     * Note: returns it as an unmodifiable collection - the calling code
     * can step through the Patients, can call methods on the Patients,
     * but can't add new patients to the set or remove patients from the set.
     */
    public Collection<Patient> getCurrentPatients(){
        return Collections.unmodifiableCollection(currentPatients);
    }

    /**
     * Return the collection of patients waiting in the department
     * Note: returns it as an unmodifiable collection - the calling code
     * can step through the Patients, but can't add to them or remove them.
     */
    public Collection<Patient> getWaitingPatients(){
        return Collections.unmodifiableCollection(waitingPatients);
    }
    
    /**
     * A new patient for the department.
     * Always starts by being put on the wait queue.
     */
    public void addPatient(Patient p){
        /*# YOUR CODE HERE */
        if(currentPatients.size()<this.maxPatients){
            currentPatients.add(p);
            p.getNextTreatment();
        }else{
            waitingPatients.offer(p);
        }
    }

    /**
     * Move patients off the wait queue (if any) into the set of patients
     * being treated (if there is space - fewer than the maximum)
     */
    public void moveFromWaiting(){
        /*# YOUR CODE HERE */
        if(!this.waitingPatients.isEmpty()){
            Patient p = waitingPatients.poll();
            currentPatients.add(p);
        }
    }
    
    /**
     * Move patient out of the department
     */
    public void removePatient(Patient p){
        /*# YOUR CODE HERE */
        currentPatients.remove(p);
    }

    /**
     * Draw the department: the patients being treated and the patients waiting 
     */
    public void redraw(double y){
        UI.setFontSize(14);
        UI.drawString(name, 0, y-35);
        double x = 10;
        UI.drawRect(x-5, y-30, maxPatients*10, 30);  // box to show max number of patients
        for(Patient p : currentPatients){
            p.redraw(x, y);
            x += 10;
        }
        x = 200;
        for(Patient p : waitingPatients){
            p.redraw(x, y);
            x += 10;
        }
    }
    private int Time = 0;
    public void report(JTextArea reportArea){
        if(this.waitingPatients.size()>5){
            if(Time%5==0||Time == 0){
                reportArea.append("\nToo much people are waiting for " + this.name + " (" +this.waitingPatients.size() + "person"+ ")");
                Date date = new Date();
                reportArea.append("\n" + date.toString());
                reportArea.append("\n"+ "   ");
                reportArea.setCaretPosition(reportArea.getText().length());
            }
            Time++;
        }
        for(Patient p:this.waitingPatients){
            if(p.waitingTime()>20){
                reportArea.append("\n" + p.getName() + " waited too long time!" + "(priority"+p.getPriority()+ ")\n");
                reportArea.append("Waited " + p.waitingTime() + "ticks in this department!");
                Date date = new Date();
                reportArea.append("\n" + date.toString());
                reportArea.append("   " + "\n");
                reportArea.setCaretPosition(reportArea.getText().length());
            }
        }
    }
}

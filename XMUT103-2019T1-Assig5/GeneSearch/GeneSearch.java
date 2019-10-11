// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;


/** GeneSearch   */

public class GeneSearch{

    
    private List<Character> data;    // the genome data to search in
    private List<Character> pattern; // the pattern to search for
    private String patternString;         // the pattern to search for (as a String)
    private int maxErrors = 1;            // number of mismatching characters allowed
    /**
     * Construct a new GeneSearch object
     */
    public GeneSearch(){
        setupGUI();
        loadData();
    }

    /**
     * Initialise the interface
     */
    public void setupGUI(){
        UI.addTextField("Search Pattern", this::setSearchPattern);
        UI.addButton("ExactSearch", this::exactSearch);
        UI.addButton("Approx Search", this::approximateSearch);
        UI.addSlider("# mismatches allowed", 1, 5, maxErrors,
                     (double n)->{maxErrors = (int)n;});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }



    public void setSearchPattern(String v){
        patternString = v.toUpperCase();
        pattern = new ArrayList<Character>();
        for (int i=0; i<v.length(); i++){
            pattern.add(patternString.charAt(i));
        }
        UI.println("Search pattern is now "+ pattern);
    }

    /**
     * Search for all occurrences of the pattern in the data,
     * reporting the position of each occurrence and the total number of matches
     */    
    public void exactSearch(){
        if (pattern==null){UI.println("No pattern");return;}
        UI.println("===================\nExact searching for "+patternString);
        /*# YOUR CODE HERE */

    }


    /**
     * Search for all approximate occurrences of the pattern in the data,
     * (pattern is the same as a sub sequence of the data except for at most
     *  maxErrors characters that differ.)
     * Reports the position and data sequence of each occurrence and
     *  the total number of matches
     */    
    public void approximateSearch(){
        if (pattern==null){UI.println("No pattern");return;}
        UI.println("===================");
        UI.printf("searching for %s, %d mismatches allowed\n", patternString, maxErrors);
        /*# YOUR CODE HERE */

    }


    /**
     * Load gene data from file into ArrayList of characters
     */
    public void loadData(){
        data = new ArrayList<Character>(1000000);
        try{
            Scanner sc = new Scanner(new File("acetobacter_pasteurianus.txt"));
            while (sc.hasNext()){
                String line = sc.nextLine();
                for (int i=0; i<line.length(); i++){
                    data.add(line.charAt(i));
                }
            }
            sc.close();
            UI.println("read "+data.size()+" letters");
        }
        catch(IOException e){UI.println("Fail: " + e);}
    }


    public static void main(String[] arguments){
        new GeneSearch();
    }        


}

// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import java.util.*;
import ecs100.*;
import java.awt.Color;

/**
 * This class contains the main method of the program. 
 * 
 * A SlideShow object represents the slideshow application and sets up the buttons in the UI. 
 * 
 * @author pondy
 */
public class SlideShow {

    public static final int LARGE_SIZE = 450;   // size of images during slide show
    public static final int SMALL_SIZE = 100;   // size of images when editing list
    public static final int GAP = 10;           // gap between images when editing
    public static final int COLUMNS = 6;        // Number of columns of thumbnails


    private List<String> images; //  List of image file names. 

    private int currentImage = -1;     // index of currently selected image.
    // Should always be a valid index if there are any images

    private boolean showRunning;      // flag signalling whether the slideshow is running or not


    /**
     * Constructor 
     */
    public SlideShow() {
        /*# YOUR CODE HERE */
    }

    /**
     * Initialises the UI window, and sets up the buttons. 
     */
    public void setupGUI() {
        UI.initialise();

        UI.addButton("Run show",   this::runShow);
        UI.addButton("Edit show",    this::editShow);
        UI.addButton("add before",   this::addBefore);
        UI.addButton("add after",    this::addAfter);
        UI.addButton("move left",      this:: moveLeft);
        UI.addButton("move right",     this:: moveRight);
        UI.addButton("move to start",  this:: moveStart);
        UI.addButton("move to end",    this:: moveEnd);
        UI.addButton("remove",       this::remove);
        UI.addButton("remove all",   this::removeAll);
        UI.addButton("reverse",      this::reverse);
        UI.addButton("shuffle",      this::shuffle);
        UI.addButton("Testing",      this::setTestList);
        UI.addButton("Quit",         UI::quit);

        UI.setKeyListener(this::doKey);
        UI.setMouseListener(this::doMouse);
        UI.setDivider(0);
        UI.printMessage("Mouse must be over graphics pane to use the keys");

    }


    // RUNNING

    /**
     * As long as the show isn't already running, and there are some
     * images to show, start the show running from the currently selected image.
     * The show should keep running indefinitely, as long as the
     * showRunning field is still true.
     * Cycles through the images, going back to the start when it gets to the end.
     * The currentImage field should always contain the index of the current image.
     */
    public void runShow(){
        /*# YOUR CODE HERE */

    }

    /**
     * Stop the show by changing showRunning to false.
     * Redisplay the list of images, so they can be edited
     */
    public void editShow(){
        /*# YOUR CODE HERE */

    }


    /**
     * Display just the current slide if the show is running.
     * If the show is not running, display the list of images
     * (as thumbnails) highlighting the current image
     */
    public void display(){
        /*# YOUR CODE HERE */

    }


    // Other Methods (you will need quite a lot of additional methods).
                                      
    /*# YOUR CODE HERE */




    // More methods for the user interface: keys (and mouse, for challenge)
    /**
     * Interprets key presses.
     * works in both editing the list and in the slide show.
     */  
    public void doKey(String key) {
        if (key.equals("Left"))         goLeft();
        else if (key.equals("Right"))   goRight();
        else if (key.equals("Home"))    goStart();
        else if (key.equals("End"))     goEnd();
    }


    /**
     * A method that adds a bunch of names to the list of images, for testing.
     */
    public void setTestList(){
        if (showRunning) return;
        String[] names = new String[] {"Atmosphere.jpg", "BachalpseeFlowers.jpg",
                "BoraBora.jpg", "Branch.jpg", "DesertHills.jpg",
                "DropsOfDew.jpg", "Earth_Apollo17.jpg",
                "Frame.jpg", "Galunggung.jpg", "HopetounFalls.jpg",
                "Palma.jpg", "Sky.jpg", "SoapBubble.jpg",
                "Sunrise.jpg", "Winter.jpg"};

        for(String name : names){
            images.add(name);
        }
        currentImage = 0;
        display();
    }


    public static void main(String[] args) {
        new SlideShow();
    }

}

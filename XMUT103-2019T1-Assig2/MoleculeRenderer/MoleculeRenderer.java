// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 2
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** 
 *  Renders a molecule on the graphics pane from different positions.
 *  
 *  A molecule consists of a collection of molecule elements, i.e., atoms.
 *  Each atom has a type or element (eg, Carbon, or Hydrogen, or Oxygen, ..),
 *  and a three dimensional position in the molecule (x, y, z).
 *
 *  Each molecule is described in a file by a list of atoms and their positions.
 *  The molecule is rendered by drawing a colored circle for each atom.
 *  The size and color of each atom is determined by the type of the atom.
 * 
 *  The description of the size and color for rendering the different types of
 *  atoms is stored in the file "element-info.txt" which should be read and
 *  stored in a Map.  When an atom is rendered, the element type should be looked up in
 *  the map to find the size and color.
 * 
 *  A molecule can be rendered from different perspectives, and the program
 *  provides buttons to control the perspective of the rendering.
 *  
 */

public class MoleculeRenderer {

    public static final double MIDX = 300;   // The middle on the (x axis)
    public static final double MIDY = 0;     // The middle on the (y axis)
    public static final double MIDZ = 200;   // The middle depth (z axis)

    // Map containing info about the size and color of each element type.
    private Map<String, ElementInfo> elements; 

    // The collection of the atoms in the current molecule
    private List<Atom> molecule = new ArrayList<Atom>();  

    
    // Constructor:
    /** 
     * Sets up the Graphical User Interface and reads the file of element data of
     * each possible type of atom into a Map: where the type is the key
     * and an ElementInfo object is the value (containing size and color).
     */
    public MoleculeRenderer() {
        setupGUI();
        readElementInfo();    //  Read the atom info
    }

    public void setupGUI(){
        UI.addButton("Molecule", this::loadMolecule);
        UI.addButton("FromFront", this::showFromFront);
        UI.addButton("FromBack", this::showFromBack);
        UI.addButton("FromRight", this::showFromRight);
        UI.addButton("FromLeft", this::showFromLeft);
        UI.addButton("FromTop", this::showFromTop);
        UI.addButton("FromBot", this::showFromBot);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.0);
    }

    /** 
     * Reads the file "element-info.txt" which contains radius and color
     * information about each type of element:
     *   element name, a radius, and red, green, blue, components of the color (integers)
     * Stores the info in a Map in the elements field.
     * The element name is the key.
     */
    private void readElementInfo() {
        UI.printMessage("Reading the element information...");
        try {
            Scanner scan = new Scanner(new File("element-info.txt"));
            /*# YOUR CODE HERE */

            UI.printMessage("Reading element information: " + elements.size() + " elements found.");
        }
        catch (IOException ex) {UI.printMessage("Reading element information FAILED."+ex);}
    }

    /**
     * Ask the user to choose a file that contains info about a molecule,
     * load the information, and render it on the graphics pane.
     */
    public void loadMolecule(){
        String filename = UIFileChooser.open();
        readMoleculeFile(filename);
        showFromFront();
    }

    /** 
     * Reads the molecule data from a file containing one line for
     *  each atom in the molecule.
     * Each line contains an atom type and the 3D coordinates of the atom.
     * For each atom, the method constructs a Atom object,
     * and adds it to the List of molecule elements in the molecule.
     * To obtain the color and the size of each atom, it has to look up the name
     * of the atom in the Map of ElementInfo objects.
     */
    public void readMoleculeFile(String fname) {
        try {
            /*# YOUR CODE HERE */

        }
        catch(IOException ex) {
            UI.println("Reading molecule file " + fname + " failed."+ex);
        }
    }

    /**
     * Renders the molecule from the front.
     * Sorts the Atoms in the List by their z value, back to front
     * Uses the default ordering of the Atoms
     * Then renders each atom at the position (MIDX+x,y)
     */
    public void showFromFront() {
        /*# YOUR CODE HERE */

        UI.clearGraphics();
        for(Atom atom : molecule) {
            atom.render(MIDX+atom.getX(), atom.getY(), elements);
        }
    }

    /**
     * Renders the molecule from the back.
     * Sorts the Atoms in the List by their z value (front to back)
     * Then renders each atom at (MIDX-x,y) position
     */
    public void showFromBack() {
        /*# YOUR CODE HERE */

    }

    /**
     * Renders the molecule from the left.
     * Sorts the Atoms in the List by their x value (larger x first)
     * Then renders each atom at (MIDZ-z,y) position
     */
    public void showFromLeft() {
        /*# YOUR CODE HERE */

    }

    /**
     * Renders the molecule from the right.
     * Sorts the Atoms in the List by their x value (smaller x first)
     * Then renders each atom at (MIDZ+z,y) position
     */
    public void showFromRight() {
        /*# YOUR CODE HERE */

    }

    /**
     * Renders the molecule from the top.
     * Sorts the Atoms in the List by their y value (larger y first)
     * Then renders each atom at (MIDX+x, MIDZ-z) position
     */
    public void showFromTop() {
        /*# YOUR CODE HERE */

    }

    /**
     * Renders the molecule from the bottom.
     * Sorts the Atoms in the List by their y value (smaller y first)
     * Then renders each atom at (MIDX+x, MIDZ+z) position
     */
    public void showFromBot() {
        /*# YOUR CODE HERE */

    }

    public static void main(String args[]) {
        new MoleculeRenderer();
    }
}

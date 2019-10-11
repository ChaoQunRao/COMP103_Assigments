// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 5
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/** <description of class OrganisationChart>
 */

public class OrganisationChart {

    // Fields
    private Position organisation;             // the root of the current organisational chart
    private Position pressedPosition = null;   // the position on which the mouse was pressed
    private Position selectedPosition = null;  // the selected position on which we can modify
    //  the attributes.
    private boolean pressedOnEmployee = false; // was it pressed on the employee?
    private Position newPosition = null;       // the position constructed from the data
    //  the user entered

    private String newInitials = null;         // the data the user entered
    private String newRole = null;

    // constants for the layout
    public static final double NEW_LEFT = 10;  // left of the new position Icon
    public static final double NEW_TOP = 10;   // top of the new position Icon

    public static final double ICON_X = 40;    // location and size of the retirement icon
    public static final double ICON_Y = 100;   
    public static final double ICON_RAD = 20;   

    /**
     * Construct a new OrganisationChart object
     * Set up the GUI
     */
    public OrganisationChart() {
        this.setupGUI();
        organisation = new Position("CEO", null);   // Set the root node of the organisation
        redraw();
    }

    /**
     * Set up the GUI (buttons and mouse)
     */
    public void setupGUI(){
        UI.setMouseMotionListener( this::doMouse );
        UI.addTextField("Role", this::setRole);
        UI.addTextField("Initials", this::setEmployee);
        UI.addButton("Load test tree",  this::makeTestTree); 
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1100,500);
        UI.setDivider(0);
    }

    /**
     * Deal with the entry of a role in the TextField
     * If a Position in the organisation has been selected, update the role of this position
     * Otherwise, update the role in the NewIcon
     */
    public void setRole (String v){
        if (selectedPosition != null) {
            selectedPosition.setRole(v);
        }
        else {
            newRole=v;
        }
        redraw();
    }

    /**
     * Deal with the entry of initials in the TextField
     * Creates an Employee: the initials must contain some text
     * If no Position in the organisation has been selected,
     *    update the employee in the NewIcon
     * If a Position in the organisation has been selected,
     *    update the employee in this position if it is vacant
     */
    public void setEmployee (String v){
        if (selectedPosition != null) {
            if (selectedPosition.isVacant()){
                selectedPosition.fillVacancy(createEmployee(v));
            }
        }
        else {
            newInitials = v;
        }
        redraw();
    }

    /* Creates an Employee: the initials must contain some text */
    private Employee createEmployee (String v){
        // New employee must have initials
        if (v != null && v.trim().length() != 0) {
            // create a new employee
            return new Employee(v);
        }
        else return null;
    }

    /**
     * Most of the work is initiated by the mouse.
     * The action depends on where the mouse is pressed:
     *   on the new icon,
     *   a position in the tree, or
     *   an employee in a position
     * and where it is released:
     *   on the same position,
     *   another position in the tree,
     *   on the retirement Icon, or
     *   empty space
     * 
     * An existing position will be moved around in the tree, removed,
     *    or repositioned.
     * A new position can be added into the tree.
     * An existing employee will be moved into a vacant position or retiring.
     * A new employee can be added into a vacant position.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("pressed")){
            if (onNewIcon(x, y)) {
                // get the new position
                newPosition = new Position(newRole, createEmployee(newInitials));
                pressedPosition = null;
            }
            else {
                // find the pressed position
                pressedPosition = findPosition(x, y, organisation);
                if (pressedPosition != null) {
                    // Did it select the employee?
                    pressedOnEmployee = pressedPosition.onEmployee(x, y);
                    if (pressedOnEmployee) {
                        pressedPosition.pressEmployee(true);
                    }
                    else {
                        pressedPosition.pressPosition(true);
                    }
                }
                newPosition = null;
            }
        }
        else if (action.equals("released")){
            Position targetPosition = findPosition(x, y, organisation);

            // selecting a position in order to modify its attribute
            if (targetPosition != null && targetPosition == pressedPosition) {
                setSelectedPosition();
            }

            // acting on a position in the tree
            else if (pressedPosition != null) {
                // position or employee are not "pressed" anymore
                pressedPosition.pressPosition(false);
                pressedPosition.pressEmployee(false);
                if (onRetirementIcon(x, y) ){
                    if (pressedOnEmployee) {
                        // moving employee to retirement 
                        retireEmployee(pressedPosition); //.makeVacant(); //PONDY do we want a method for this?
                    }
                    else {
                        // moving position to retirement 
                        retirePosition(pressedPosition);
                    }
                }
                else if (targetPosition != null) {
                    if (pressedOnEmployee) {
                        // moving the employee to a different position
                        moveEmployee(pressedPosition, targetPosition);
                    }
                    else {
                        // moving existing position around in the hierarchy.
                        movePosition(pressedPosition, targetPosition);
                    }
                }
                else if (targetPosition == null) { 
                    // repositioning the position
                    pressedPosition.moveOffset(x);
                }
            }

            // acting on the new Position
            else if (newPosition != null && targetPosition != null ) {
                // moving new position to hierarchy.
                addNewPosition(newPosition, targetPosition);
                newRole=null;
                newInitials=null;
            }

            this.redraw();
        }
    }

    /** 
     * Find and return a position that is currently placed over the position (x,y). 
     * Must do a recursive search of the subtree whose root is the given position.
     * [STEP 2:] 
     *    Returns a position if it finds one,
     *    Returns null if it doesn't.
     * [Completion:] If (x,y) is on two positions, it should return the top one.
     */
    private Position findPosition(double x, double y, Position pos){
        if (pos.on(x, y)) {   // base case: (x,y) is on root of subtree
            return pos;  
        }
        else {  // look further in the subtree
            /*# YOUR CODE HERE */
            for(Position memb:pos.getTeam()){
                Position p = this.findPosition(x, y, memb);
                if(p!=null){return p;}
            }
        }
        return null;  // it wasn't found;
    }

    /** [STEP 2:] 
     * Add the new position to the target's team.
     */
    public void addNewPosition(Position newPos, Position target){
        if ((newPos == null) || (target == null)){return;}   //invalid arguments.
        /*# YOUR CODE HERE */
        target.addToTeam(newPos);
    }

    /** [STEP 2:] 
     *    Move a current position (pos) to another position (target)
     *    by adding the position to the team of the target,
     *    (and bringing the whole subtree of the position with them)
     *
     *  [COMPLETION:]
     *   Moving the CEO is a problem, and shouldn't be allowed. 
     *   In general, moving any position to a target that is in the
     *   position's subtree is a problem and should not be allowed. (Why?)
     */
    private void movePosition(Position pos, Position target) {
        if ((pos == null) || (target == null)){return;}   //invalid arguments.
        /*# YOUR CODE HERE */
        if(!this.inSubtree(target, pos)){
            target.addToTeam(pos);
        }
    }

    /** [STEP 2:]
     * Retire a position by removing it from the tree completely.
     * The position must be vacant and cannot be a manager.
     */
    public void retirePosition(Position pos){
        /*# YOUR CODE HERE */
        if(pos.isManager()||!pos.isVacant()){
            UI.println("this position is not vacant or is a manager");
            return;
        }
        pos.getManager().removeFromTeam(pos);
    }

    /** [COMPLETION:]
     *  Return true if person is in the subtree, and false otherwise
     *  Uses == to determine node equality
     *  Check if person is the same as the root of subTree
     *  if not, check if in any of the subtrees of the team members of the root
     *   (recursive call, which must return true if it finds the person)
     */
    private boolean inSubtree(Position person, Position subTree) {
        if (subTree==organisation) { return true; }  // first simple case!!
        if (person==subTree)       { return true; }  // second simple case!!
        // search down the subTree
        /*# YOUR CODE HERE */
        for(Position p : subTree.getTeam()){
            boolean b = this.inSubtree(person,p);
            if(b){return b;}
        }
        return false;
    }

    // Modifying Position attributes ==============================
    /**
     * Selecting a Position to be able to modify its attributes
     */
    private void setSelectedPosition() {
        pressedPosition.pressPosition(false);
        pressedPosition.pressEmployee(false);
        if (selectedPosition == null) {
            // None was already selected
            selectedPosition = pressedPosition;
            selectedPosition.highlightPosition(true);
        }
        else {
            selectedPosition.highlightPosition(false);
            if (selectedPosition == pressedPosition) {
                // Deselecting the position
                selectedPosition = null;
            }
            else {
                // Selecting a new position
                selectedPosition = pressedPosition;
                selectedPosition.highlightPosition(true);
            }
        }
    }

    /** [STEP3:]
    Move the employee from their current position (pos)
     *    to a vacant position (target)
     *    Does nothing if pos is vacant or the target is not vacant.
     */
    private void moveEmployee(Position pos, Position target) {
        if ((pos == null) || (target == null)){return;}   //invalid arguments.
        /*# YOUR CODE HERE */
        if(pos.isVacant()||!target.isVacant()){
            return;
        }
        target.setEmployee(pos.getEmployee());
        pos.setEmployee(null);
    }

    /** [STEP3:]
     * Moving an employee to retirement.
     */
    public void retireEmployee(Position pos){
        if (pos == null){return;}   //invalid arguments.
        /*# YOUR CODE HERE */
        pos.setEmployee(null);
    }

    // Drawing the tree  =========================================
    /**
     * Redraw the chart.
     */
    private void redraw() {
        UI.clearGraphics();
        drawTree(organisation);
        drawNewIcon();
        drawRetireIcon();
    }

    /** [STEP 1:]
     *  Recursive method to draw all nodes in a subtree, given the root node.
     *  (The provided code just draws the root node;
     *   you need to make it draw all the nodes.)
     */
    private void drawTree(Position pos) {
        pos.draw();
        //draw the nodes under pos
        /*# YOUR CODE HERE */
        for(Position meb:pos.getTeam()){
            drawTree(meb);
        }
    }

    // OTHER DRAWING METHODS =======================================
    /**
     * Redraw the new Person box
     */
    private void drawNewIcon(){
        UI.setColor(Position.BACKGROUND);
        UI.fillRect(NEW_LEFT,NEW_TOP,Position.WIDTH, Position.HEIGHT);
        UI.setColor(Color.black);
        UI.drawRect(NEW_LEFT,NEW_TOP,Position.WIDTH, Position.HEIGHT);
        UI.drawString((newRole==null)?"--":newRole, NEW_LEFT+5, NEW_TOP+12); 
        if (newInitials != null) {
            UI.drawOval(NEW_LEFT+Position.EMPLOYEE_LEFT,NEW_TOP+Position.EMPLOYEE_TOP,
                Employee.WIDTH,Employee.HEIGHT);
            UI.drawString(newInitials, NEW_LEFT+10, NEW_TOP+Position.EMPLOYEE_TOP+Employee.HEIGHT/2);
        }       
    }

    /**
     * Redraw the retirement Icon
     */
    private void drawRetireIcon(){
        UI.setColor(Color.red);
        UI.setLineWidth(5);
        UI.drawOval(ICON_X-ICON_RAD, ICON_Y-ICON_RAD, ICON_RAD*2, ICON_RAD*2);
        double off = ICON_RAD*0.68;
        UI.drawLine((ICON_X - off), (ICON_Y - off), (ICON_X + off), (ICON_Y + off));
        UI.setLineWidth(1);
        UI.setColor(Color.black);
    }

    /** is the mouse position on the New Position box */
    private boolean onNewIcon(double x, double y){
        return ((x >= NEW_LEFT) && (x <= NEW_LEFT + Position.WIDTH) &&
            (y >= NEW_TOP) && (y <= NEW_TOP + Position.HEIGHT));
    }

    /** is the mouse position on the retirement icon */
    private boolean onRetirementIcon(double x, double y){
        return (Math.abs(x - ICON_X) < ICON_RAD) && (Math.abs(y - ICON_Y) < ICON_RAD);
    }

    // Testing ==============================================
    /**
     * Makes an initial tree so you can test your program
     */
    private void makeTestTree(){
        organisation = new Position("CEO", new Employee("AA"));
        Position aa = new Position("VP1", new Employee("AS"));
        Position bb = new Position("VP2", new Employee("BV"));
        Position cc = new Position("VP3", new Employee("CW"));
        Position dd = new Position("VP4", new Employee("DM"));
        Position a1 = new Position("AL1", new Employee("AF"));
        Position a2 = new Position("AL2", new Employee("AH"));
        Position b1 = new Position("AS", new Employee("BK"));
        Position b2 = new Position("DPA", new Employee("BL"));
        Position d1 = new Position("DBP", new Employee("CX"));
        Position d2 = new Position("SEP", new Employee("CY"));
        Position d3 = new Position("MSP", new Employee("CZ"));

        organisation.addToTeam(aa); aa.setOffset(-160);
        organisation.addToTeam(bb); bb.setOffset(-50);
        organisation.addToTeam(cc); cc.setOffset(15);
        organisation.addToTeam(dd); dd.setOffset(120);

        aa.addToTeam(a1); a1.setOffset(-25);
        aa.addToTeam(a2); a2.setOffset(25);
        bb.addToTeam(b1); b1.setOffset(-25);
        bb.addToTeam(b2); b2.setOffset(25);
        dd.addToTeam(d1); d2.setOffset(-50);
        dd.addToTeam(d2); 
        dd.addToTeam(d3); d3.setOffset(50);
        organisation.addToTeam(aa); aa.setOffset(-160);

        this.redraw();
    }

    //* Test for printing out the tree structure, indented text */
    private void printTree(Position empl, String indent){
        UI.println(indent+empl+ " " +
            (empl.getManager()==null?"noM":"hasM") + " " +
            empl.getTeam().size()+" reports");
        String subIndent = indent+"  ";
        for (Position tm : empl.getTeam()){
            printTree(tm, subIndent);
        }
    }

    // Main
    public static void main(String[] arguments) {
        new OrganisationChart();
    }

}

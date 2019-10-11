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
import java.awt.Color;

/**
 * Represents a position in an organisation
 * Normal positions have an employee and a role (and could have
 *   further information)
 * All positions (other than the CEO) have a manager, the position
 *   that this position reports to.
 * A manager is a position that has a non empty team consisting of
 *   a set of positions.
 *   (If a position has an empty team, then it is not a manager.)
 * A Position object can also represent a vacant position in the
 *   organisation characterised by the absence of an employee.
 *  A vacant position has a manager and may have a non empty team.
 *  A vacant position is filled by giving it an Employee object.
 *
 * A position is drawn as a rectangle. It contains the role and an
 *   oval representing the employee.
 * Every position (except the CEO) will be drawn with a link to 
 *   their manager.
 * The location where a team member of a manager is drawn is one
 *   layer down from the manager, at a horizontal location specified
 *   by the position's offset - how far to the right (or left) of 
 *   the manager.
 *   This means that if the manager is moved around on the screen, 
 *   the team members (and their team members, and ....) will
 *   automatically move with them.
 */

public class Position {

    // Fields
    private String role;
    private Employee employee;
    private Position manager;
    private Set<Position> team = new HashSet<Position>();
    private double offset;  // horizontal offset relative to the manager
    // negative value to the left of the manager,
    // positive value to the right

    private boolean posPressed = false;
    private boolean posHighlighted = false;
    private boolean emplPressed = false;

    // constant for drawing a position
    public static final double WIDTH = 45;   // width of the box representing the position
    public static final double HEIGHT = 50;  // height of the box representing the position
    public static final double EMPLOYEE_TOP = 18;
    public static final double EMPLOYEE_LEFT = 2;
    public static final double V_SEP = 20;   // vertical separation between layers
    public static final double ROOT_TOP = 50;
    public static final double ROOT_X = 550;
    public static final Color BACKGROUND = new Color(255, 255, 180);
    public static final Color POS_PRESSED = new Color(222, 255, 220);
    public static final Color POS_HIGHLIGHTED = new Color(97, 255, 89);
    public static final Color EMPL_PRESSED = new Color(9, 255, 255);

    // Constructors

    /**
     * Construct a new Position object with a role and a given employee
     */
    public Position(String role, Employee employee) {
        this.role = role;
        this.employee = employee;
    }

    /**
     * Construct a new Position object with the given role, initials, and offset
     * Useful for loading from file 
     */
    public Position(String role, String initials, int id, double offset) {
        this.role = role;
        this.employee = new Employee(initials, id);
        this.offset = offset;
    }

    // Adding and removing members of the team =========================

    /** [STEP 1:]
     * Add a new member to the team managed by this position, and
     * ensure that the new team member has this position as their manager
     */
    public void addToTeam(Position newMemb){
        if (newMemb == null) return;
        /*# YOUR CODE HERE */
        team.add(newMemb);
        //newMemb.manager = this;
        newMemb.setManager(this);
    }
    public void setManager(Position mang){
        this.manager = mang;
    }
    /** [STEP 2:]
     * Remove a member of the team managed by this position, and
     * ensure that the team member no longer has this position as their manager
     */
    public void removeFromTeam(Position teamMemb){
        /*# YOUR CODE HERE */
        this.team.remove(teamMemb);
        teamMemb.manager = null;
        teamMemb.setManager(null);
    }

    // Simple getters and setters  ==================================

    /** Returns the employee in this Position */
    public Employee getEmployee()  {return employee;}
        /** Returns the employee in this Position */
    public void setEmployee(Employee e)  {this.employee = e;}

    /** Returns the manager of this Position */
    public Position getManager()  {return manager;}

    /** Returns the role of this Position */
    public String getRole()  {return role;}

    /** Sets the role of this Position */
    public void setRole(String r)  {role = r;}

    /* Clear the role of the position */
    public void clearRole(){
        role=null;
    }

    /** Returns the set of positions in the team, */
    public Set<Position> getTeam() {
        return Collections.unmodifiableSet(team);
    }
    //Note: By returning an unmodifiable version of the team, other parts
    // of the program can access and loop through the team,
    // but cannot add or remove position from the team

    /**
     * Returns true iff this position is managing any other positions
     */
    public boolean isManager(){
        return (!team.isEmpty());
    }

    /**
     * True if the Position is a vacant empty position, needing to be filled.
     */
    public boolean isVacant(){
        return (employee == null);
    }

    /**
     * Clear employee of the position,
     * but leave the role, the manager and team members.
     * Effectively makes this a vacant position within the hierarchy.
     */
    public void makeVacant(){
        employee = null;
    }

    /**
     * Fill a vacant position with this employee
     * Assumes the Position is vacant.
     */
    public void fillVacancy(Employee emp){
        this.employee = emp;
    }

    /**
     * Move the value offset so that the Position will be drawn at location x 
     * Offset specifies where to draw the position, relative to their manager.
     * Offset is the distance to the right (or left, if negative) of the manager's location.
     */
    public void moveOffset(double x){
        if (manager == null) { // this must be the CEO
            offset = x - ROOT_X;
        }
        else {
            offset=x - manager.getX();
        }
    }

    /**
     * Set the offset value (horizontal location of this position relative to manager)
     * Only needed for constructing test hierarchy or loading from a file.
     */
    public void setOffset(double off){
        offset = off;
    }

    /**
     * Return the top of this position box (internal use only)
     * Calculated 
     */
    private double getTop(){
        if (manager == null) { return ROOT_TOP; }   // this must be the top position
        return manager.getTop() + HEIGHT + V_SEP;
    }

    /**
     * Horizontal center of this position box (internal use only)
     * Recursive method, to compute center from the offset and the center of the manager.
     */
    private double getX(){
        if (manager == null) {  // this must be the CEO
            return ROOT_X + offset;
        }
        else {
            return manager.getX() + offset;
        }
    }

    /**
     * Returns true iff the point (x,y) is on top of where
     *  this position is currently drawn.
     */
    public boolean on(double x,double y){
        return (Math.abs(getX()-x)<=WIDTH/2 && (y >= getTop()) && (y <= getTop()+HEIGHT) );
    }

    /**
     * Returns true iff the point (x,y) is on top of where
     *  this position's employee is currently drawn.
     */
    public boolean onEmployee(double x,double y){
        return (!isVacant() && (y >= getTop()+EMPLOYEE_TOP) && (y <= getTop()+EMPLOYEE_TOP+Employee.HEIGHT) );
    }

    /**
     * Returns a string containing the details of a position.
     * if the role or initials are null, then will be given as "??"
     */
    public String toString() {
        return String.format("%s %-3s", (role==null?"":role), (employee==null?"":employee.toString()));
    }

    /**
     * Returns a string containing the details of a position, plus
     * offset and number of members of their team.
     * initials and role may be "NULL"
     * May be useful for saving to files
     */
    public String toStringFull() {
        return ((employee==null?"NULL":employee.toStringFull()) +" "+
            (role==null||role.equals("")?"NULL":role) +" "+
            (int)(offset) +" "+team.size());
    }

    /**
     * Draw a box representing the Position, and 
     * connect it to its manager (if there is a manager)
     * If the position is filled, the employee is represented by an oval
     */
    public void draw(){
        double left=getX()-WIDTH/2;
        double top=getTop();
        // Background colour
        UI.setColor(posPressed?POS_PRESSED:(posHighlighted?POS_HIGHLIGHTED:BACKGROUND));
        UI.fillRect(left,top,WIDTH,HEIGHT);
        // Outline colour
        UI.setColor(Color.black);
        UI.drawRect(left,top,WIDTH,HEIGHT);
        UI.drawString((role==null)?"--":role, left+5, top+12);
        if (! isVacant()) {
            if (emplPressed){
                UI.setColor(EMPL_PRESSED);
                UI.fillOval(left+EMPLOYEE_LEFT,top+EMPLOYEE_TOP,Employee.WIDTH,Employee.HEIGHT);
            }
            else{
                UI.setColor(Color.black);
                UI.drawOval(left+EMPLOYEE_LEFT,top+EMPLOYEE_TOP,Employee.WIDTH,Employee.HEIGHT);
            }
            UI.drawString(employee.toString(), left+10, top+EMPLOYEE_TOP+Employee.HEIGHT/2);
        }
        if (manager != null) {
            UI.setColor(Color.black);
            // vertical line
            double x1 = manager.getX();
            double y1 = manager.getTop() + HEIGHT;
            double yMid = y1 + V_SEP/2;

            double x2 = x1 + offset;
            double y2 = y1 + V_SEP;

            UI.drawLine(x1, y1, x1, yMid);    // vertical
            UI.drawLine(x1, yMid, x2, yMid);  // horizontal
            UI.drawLine(x2, yMid, x2, y2);    // vertical
        }
    }

    /**
     * Highlight on and off the Position
     */
    public void highlightPosition(boolean flag){
        this.posHighlighted = flag;
    }

    /**
     * Indicate whether mouse pressed on the Position
     */
    public void pressPosition(boolean flag){
        this.posPressed = flag;
        this.draw();
    }

    /**
     * Indicate whether mouse pressed on the Employee
     */
    public void pressEmployee(boolean flag){
        this.emplPressed = flag;
        this.draw();
    }
}

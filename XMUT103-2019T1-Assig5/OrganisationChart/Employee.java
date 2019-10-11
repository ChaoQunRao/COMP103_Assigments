// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 5
 * Name:
 * Username:
 * ID:
 */

/**
 * Represents an employee in an organisation
 * Employees have initials and an ID number
 *   (and could have further information)
 *
 * An employee is drawn as an oval containing 
 *   the initials of the employee.
 */

public class Employee {

    // Fields
    private String initials;
    private int id;
    static int id_counter = 3000;

    // constant for drawing an employee
    public static final double WIDTH = 40;   // width of the circle representing the employee
    public static final double HEIGHT = 30;  // height of the circle representing the employee

    // Constructors

    /**
     * Construct a new Employee object with the given initials
     * Assume initials is a non null and non empty String
     */
    public Employee(String initials) {
        this.initials = initials;
        this.id = id_counter++;
    }

    /**
     * Construct a new Employee object with the given initials and id
     */
    public Employee(String initials, int id) {
        this.initials = initials;
        this.id = id;
        id_counter = (id > id_counter) ? id : (id_counter+1);
    }

    /** Returns the initials of this Employee */
    public String getInitials()  {return initials;}

    /**
     * Returns a string containing the initials of an employee.
     */
    public String toString() {
        return String.format("%-3s", initials);
    }

    /**
     * Returns a string containing the details of an employee
     * May be useful for saving to files
     */
    public String toStringFull() {
        return (initials +" "+ id);
    }

}

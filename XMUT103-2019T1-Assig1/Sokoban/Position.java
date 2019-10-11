// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 1
 * Name:
 * Username:
 * ID:
 */
import java.util.*;
/** 
 *  A pair of row and column representing the coordinates of a cell in the warehouse.
 *  Has a method to return the next Position in a given direction.
 *  Because the fields are final (can't be changed), it is safe to make
 *  the fields public.
 *  If  pos is a variable containing a Position, then pos.row and pos.col
    will be the values of the row and the col in the Position.
 */

public class Position implements Iterable <Position>  {

    /**
     * Fields containing a row and a column
     */
    public final int row; 
    public final int col;  
    private Set<Position> neightbor;
    /**
     * Constructor
     */
    Position (int row, int col) {
        this.row = row;
        this.col = col;
        neightbor = new HashSet<Position>();
        //neightbor.add(_e_)
    }

    /**
     * Return the next position in the specified direction
     */
    public Position next(String direction) {
        if (direction.equals("up"))    return new Position(row-1, col);
        if (direction.equals("down"))  return new Position(row+1, col);
        if (direction.equals("left"))  return new Position(row, col-1);
        if (direction.equals("right")) return new Position(row, col+1);
        return this;
    }
    public Iterator<Position> iterator() { return neightbor.iterator(); }
    /**
     * Return a string with the values of the fields.
     */
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }
}

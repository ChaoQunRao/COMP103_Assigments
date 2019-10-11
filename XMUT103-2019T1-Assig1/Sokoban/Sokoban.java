// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 1
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/** 
 * Sokoban
 */
public class Sokoban {

    private Cell[][] cells;             // the array representing the warehouse
    private int rows;                   // the height of the warehouse
    private int cols;                   // the width of the warehouse
    private int level = 1;              // current level 

    private Position workerPos;         // the position of the worker
    private String workerDir = "left";  // the direction the worker is facing
    Stack<ActionRecord> actions = new Stack<ActionRecord>();
    Stack<ActionRecord> undoActions = new Stack<ActionRecord>();
    //Stack<ActionRecord> allActions = new Stack<ActionRecord>();

    /** 
     *  Constructor: set up the GUI, and load the 0th level.
     */
    public Sokoban() {
        setupGUI();
        doLoad();
    }

    /**
     * Add the buttons and set the key listener.
     */
    public void setupGUI(){
        UI.addButton("New Level", () -> {level++; doLoad();});
        UI.addButton("Restart",   this::doLoad);
        UI.addButton("Undo",      this::undo);
        UI.addButton("Redo",      this::redo);
        UI.addButton("left",      () -> {moveOrPush("left");});
        UI.addButton("up",        () -> {moveOrPush("up");});
        UI.addButton("down",      () -> {moveOrPush("down");});
        UI.addButton("right",     () -> {moveOrPush("right");});
        UI.addButton("Quit",      UI::quit);
        UI.setMouseListener(this::doMouse);
        actions = new Stack();
        UI.setKeyListener(this::doKey);
        UI.setDivider(0.0);
    }
    public void doMouse(String action,double x, double y){
        if(action.equals("released")){
            double xPos = LEFT_MARGIN,yPos = TOP_MARGIN;
            Position currentCell = null;
            for(int i = 0;i<cells.length;i++){
                for(int j = 0;j<cells[5].length;j++){
                    if(x > xPos && x < xPos + CELL_SIZE && y > yPos && y < yPos + CELL_SIZE){
                        currentCell = new Position(i,j);
                        break;
                    }
                    xPos = xPos + CELL_SIZE;
                }
                xPos = LEFT_MARGIN;
                yPos = yPos + CELL_SIZE;
            }
            if(currentCell == null || currentCell.row >= cells.length){
                UI.println("Can't go there!");
                return;
            }
            for(int i = 0; i < cells.length;i++){
                if(currentCell.row> cells[i].length){
                    UI.println("Can't go there!");
                    return;
                }
            }
            if(!cells[currentCell.row][currentCell.col].isFree()){
                UI.println("Can't go there!");
                return;
            }
            UI.println(currentCell.row+"    "+currentCell.col);
            Stack<String> rount = new Stack<String>();
            boolean b = this.found(currentCell.row, currentCell.col, rount,"");
            if(!b){return;}
            UI.println(rount);
            while(!rount.isEmpty()){
                if(!rount.isEmpty()&&rount.peek().equals("D")){this.move("up"); rount.pop();UI.sleep(100);}
                if(!rount.isEmpty()&&rount.peek().equals("R")){this.move("left"); rount.pop();UI.sleep(100);}
                if(!rount.isEmpty()&&rount.peek().equals("U")){this.move("down"); rount.pop();UI.sleep(100);}
                if(!rount.isEmpty()&&rount.peek().equals("L")){this.move("right"); rount.pop();UI.sleep(100);}
            }
        }
    }
    public boolean isOppsite(String a,String b){
        if(a.equals("U")&&b.equals("D")){
            return true;
        }
        if(a.equals("D")&&b.equals("U")){
            return true;
        }
        if(a.equals("R")&&b.equals("L")){
            return true;
        }
        if(a.equals("L")&&b.equals("R")){
            return true;
        }
        return false;
    }
    public Stack<String> findWay(int row,int col){
        String last = "";
        Stack<String> rightWay = new Stack<String>();
        while(true){
            int r = workerPos.row;
            int c = workerPos.col;
            if(row==r&&col==c){
                return rightWay;
            }
            /*# Choose the best way(Cost is the min)*/
            if(col>c&&cells[row][col-1].isFree()&&!last.equals("R")){
                // go left
                last = "L";
                rightWay.push("R");
                UI.println(last);
                col--;
            }else if(col<c&&cells[row][col+1].isFree()&&!last.equals("L")){
                //go Right
                last = "R";
                rightWay.push("L");UI.println(last);
                col++;
            }else if(row>r&&cells[row-1][col].isFree()&&!last.equals("D")){
                //go up
                last = "U";
                rightWay.push("D");UI.println(last);
                row--;
            }else if(row<r&&cells[row+1][col].isFree()&&!last.equals("U")){
                //go down
                last = "D";
                rightWay.push("U");UI.println(last);
                row++;
            }
            /*# If have not any min cost choice,choose other ways(Just need the way is free), maybe the oppsite direction.*/
            else if(cells[row][col-1].isFree()&&!last.equals("R")){
                // go left
                last = "L";
                rightWay.push("R");
                UI.println(last);
                col--;
            }else if(cells[row][col+1].isFree()&&!last.equals("L")){
                //go Right
                last = "R";
                rightWay.push("L");UI.println(last);
                col++;
            }else if(cells[row-1][col].isFree()&&!last.equals("D")){
                //go up
                last = "U";
                rightWay.push("D");UI.println(last);
                row--;
            }else if(cells[row+1][col].isFree()&&!last.equals("U")){
                //go down
                last = "D";
                rightWay.push("U");UI.println(last);
                row++;
            }else{                    
                last = rightWay.pop();
                if(last.equals("U")){
                    row--;
                }else if(last.equals("D")){
                    row++;
                }else if(last.equals("R")){
                    col++;
                }else if(last.equals("L")){
                    col--;
                }
            }
        }
    }
    public Stack<String> recursive(int row,int col){
        Stack<String> ans = new Stack<String>();
        this.find(row, col, ans,"");
        return ans;
    }
    Stack temp;    
    public boolean find(int row,int col,Stack<String> ans,String last){
        int r = workerPos.row;
        int c = workerPos.col;
        if(row==r&&col==c){
            return true;
        }
        if(col>c&&cells[row][col-1].isFree()&&!last.equals("R")){
            // go left
            last = "L";
            ans.push("R");
            this.find(row, col-1, ans, last);
        }else if(col<c&&cells[row][col+1].isFree()&&!last.equals("L")){
            //go Right
            last = "R";
            ans.push("L");UI.println(last);
            this.find(row, col+1, ans, last);
        }else if(row>r&&cells[row-1][col].isFree()&&!last.equals("D")){
            //go up
            last = "U";
            ans.push("D");UI.println(last);
            this.find(row-1, col, ans, last);
        }else if(row<r&&cells[row+1][col].isFree()&&!last.equals("U")){
            //go down
            last = "D";
            ans.push("U");UI.println(last);
            this.find(row+1, col, ans, last);
        }
        else if(cells[row][col-1].isFree()&&!last.equals("R")){
            // go left
            last = "L";
            ans.push("R");
            UI.println(last);
            this.find(row, col-1, ans, last);
        }else if(cells[row][col+1].isFree()&&!last.equals("L")){
            //go Right
            last = "R";
            ans.push("L");UI.println(last);
            this.find(row, col+1, ans, last);
        }else if(cells[row-1][col].isFree()&&!last.equals("D")){
            //go up
            last = "U";
            ans.push("D");UI.println(last);
            this.find(row-1, col, ans, last);
        }else if(cells[row+1][col].isFree()&&!last.equals("U")){
            //go down
            last = "D";
            ans.push("U");UI.println(last);
            this.find(row+1, col, ans, last);
        }else{                    
            last = ans.pop();
            if(last.equals("U")){
                this.find(row-1, col, ans, last);
            }else if(last.equals("D")){
                this.find(row+1, col, ans, last);
            }else if(last.equals("R")){
                this.find(row, col+1, ans, last);
            }else if(last.equals("L")){
                this.find(row, col+1, ans, last);
            }
        }
        return false;
    }
    public boolean found(int row,int col,Stack<String> ans,String last){
        int r = workerPos.row;
        int c = workerPos.col;
        if(row==r&&col==c){
            return true;
        }
        if(cells[row][col-1].isFree()&&!last.equals("R")){
            // go left
            last = "L";
            ans.push("R");
            if(this.found(row, col-1, ans, last)){return true;}            
            ans.pop();
        }else if(cells[row][col+1].isFree()&&!last.equals("L")){
            //go Right
            last = "R";
            ans.push("L");
            if(this.found(row, col+1, ans, last)){return true;}
            ans.pop();
        }else if(cells[row-1][col].isFree()&&!last.equals("D")){
            //go up
            last = "U";
            ans.push("D");
            if(this.found(row-1, col, ans, last)){return true;}
            ans.pop();
        }else if(cells[row+1][col].isFree()&&!last.equals("U")){
            //go down
            last = "D";
            ans.push("U");
            if(this.found(row+1, col, ans, last)){return true;}
            ans.pop();
        }else{
            return false; 
        }
        return false; 
    }
    /** 
     * Respond to key actions
     */
    public void doKey(String key) {
        key = key.toLowerCase();
        if (key.equals("i")|| key.equals("w") ||key.equals("up")) {
            moveOrPush("up");
        }
        else if (key.equals("k")|| key.equals("s") ||key.equals("down")) {
            moveOrPush("down");
        }
        else if (key.equals("j")|| key.equals("a") ||key.equals("left")) {
            moveOrPush("left");
        }
        else if (key.equals("l")|| key.equals("d") ||key.equals("right")) {
            moveOrPush("right");
        }
        else if (key.equals("u")) {
            undo();
        }
        else if (key.equals("r")) {
            redo();
        }
    }

    /** 
     *  Moves the worker in the given direction, if possible.
     *  If there is box in front of the Worker and a space in front of the box,
     *  then push the box.
     *  Otherwise, if the worker can't move, do nothing.
     */
    public void moveOrPush(String direction) {
        workerDir = direction;                       // turn worker to face in this direction

        Position nextP = workerPos.next(direction);  // where the worker would move to
        Position nextNextP = nextP.next(direction);  // where a box would be pushed to

        // is there a box in that direction which can be pushed?
        if ( cells[nextP.row][nextP.col].hasBox() &&
             cells[nextNextP.row][nextNextP.col].isFree() ) { 
            push(direction);
            if (isSolved()) { reportWin(); }
        }
        // is the next cell free for the worker to move into?
        else if ( cells[nextP.row][nextP.col].isFree() ) { 
            move(direction);
        }
    }
    public void undo(){
        if(actions.isEmpty()){
            UI.println("No more actions for undo!");
            return;
        }
        ActionRecord action = this.actions.pop();
        undoActions.push(action);
        if(action.isMove()){
            drawCell(workerPos);                   // redisplay cell under worker
            workerPos = workerPos.next(this.opposite(action.direction())); // put worker in new position
            drawWorker();                          // display worker at new position
        }else{
            this.pull(action.direction());
        }
    }
    public void redo(){
        if(undoActions.isEmpty()){
            UI.println("No more actions for redo!");
            return;
        }
        ActionRecord action = this.undoActions.pop();
        actions.push(action);
        if(action.isMove()){
            this.move(action.direction());
        }else{
            this.push(action.direction());
        }
    }
    
    /**
     * Moves the worker into the new position (guaranteed to be empty) 
     * @param direction the direction the worker is heading
     */
    public void move(String direction) {
        drawCell(workerPos);                   // redisplay cell under worker
        workerPos = workerPos.next(direction); // put worker in new position
        drawWorker();                          // display worker at new position
        /*#My code*/
        ActionRecord action = new ActionRecord("move",direction);
        actions.push(action);
        Trace.println("Move " + direction);    // for debugging
    }

    
    /**
     * Push: Moves the Worker, pushing the box one step 
     *  @param direction the direction the worker is heading
     */
    public void push(String direction) {
        Position boxPos = workerPos.next(direction);   // where box is
        Position newBoxPos = boxPos.next(direction);   // where box will go

        cells[boxPos.row][boxPos.col].removeBox();     // remove box from current cell
        cells[newBoxPos.row][newBoxPos.col].addBox();  // place box in its new position

        drawCell(workerPos);                           // redisplay cell under worker
        drawCell(boxPos);                              // redisplay cell without the box
        drawCell(newBoxPos);                           // redisplay cell with the box
     
        workerPos = boxPos;                            // put worker in new position
        drawWorker();                                  // display worker at new position
        
        /*#My code*/
        ActionRecord action = new ActionRecord("push",direction);
        actions.push(action);
        Trace.println("Push " + direction);   // for debugging
    }


    /**
     * Pull: (could be useful for undoing a push)
     *  move the Worker in the direction,
     *  pull the box into the Worker's old position
     */
    public void pull(String direction) {
        /*# YOUR CODE HERE */
        Position boxPos = workerPos.next(direction);
        Position newBoxPos = workerPos;

        cells[boxPos.row][boxPos.col].removeBox();     // remove box from current cell
        cells[newBoxPos.row][newBoxPos.col].addBox();  // place box in its new position

        drawCell(workerPos);                           // redisplay cell under worker
        drawCell(boxPos);                              // redisplay cell without the box
        drawCell(newBoxPos);                           // redisplay cell with the box
     
        workerPos = workerPos.next(this.opposite(direction));                            // put worker in new position
        drawWorker();                                  // display worker at new position
    }

    /**
     * Report a win by flickering the cells with boxes
     */
    public void reportWin(){
        for (int i=0; i<12; i++) {
            for (int row=0; row<cells.length; row++)
                for (int column=0; column<cells[row].length; column++) {
                    Cell cell=cells[row][column];

                    // toggle shelf cells
                    if (cell.hasBox()) {
                        cell.removeBox();
                        drawCell(row, column);
                    }
                    else if (cell.isEmptyShelf()) {
                        cell.addBox();
                        drawCell(row, column);
                    }
                }

            UI.sleep(100);
        }
    }
    
    /** 
     *  Returns true if the warehouse is solved, 
     *  i.e., all the shelves have boxes on them 
     */
    public boolean isSolved() {
        for(int row = 0; row<cells.length; row++) {
            for(int col = 0; col<cells[row].length; col++)
                if(cells[row][col].isEmptyShelf())
                    return  false;
        }

        return true;
    }

    /** 
     * Returns the direction that is opposite of the parameter
     * useful for undoing!
     */
    public String opposite(String direction) {
        if ( direction.equals("right")) return "left";
        if ( direction.equals("left"))  return "right";
        if ( direction.equals("up"))    return "down";
        if ( direction.equals("down"))  return "up";
        throw new RuntimeException("Invalid  direction");
    }





    // Drawing the warehouse

    private static final int LEFT_MARGIN = 40;
    private static final int TOP_MARGIN = 40;
    private static final int CELL_SIZE = 25;

    /**
     * Draw the grid of cells on the screen, and the Worker 
     */
    public void drawWarehouse() {
        UI.clearGraphics();
        // draw cells
        for(int row = 0; row<cells.length; row++)
            for(int col = 0; col<cells[row].length; col++)
                drawCell(row, col);

        drawWorker();
    }

    /**
     * Draw the cell at a given position
     */
    private void drawCell(Position pos) {
        drawCell(pos.row, pos.col);
    }

    /**
     * Draw the cell at a given row,col
     */
    private void drawCell(int row, int col) {
        double left = LEFT_MARGIN+(CELL_SIZE* col);
        double top = TOP_MARGIN+(CELL_SIZE* row);
        cells[row][col].draw(left, top, CELL_SIZE);
    }

    /**
     * Draw the worker at its current position.
     */
    private void drawWorker() {
        double left = LEFT_MARGIN+(CELL_SIZE* workerPos.col);
        double top = TOP_MARGIN+(CELL_SIZE* workerPos.row);
        UI.drawImage("worker-"+workerDir+".gif",
                     left, top, CELL_SIZE,CELL_SIZE);
    }



    /**
     * Load a grid of cells (and Worker position) for the current level from a file
     */
    public void doLoad() {
        File f = new File("warehouse" + level + ".txt");

        if (! f.exists()) {
            UI.printMessage("Run out of levels!");
            level--;
        }
        else {
            List<String> lines = new ArrayList<String>();
            try {
                Scanner sc = new Scanner(f);
                while (sc.hasNext()){
                    lines.add(sc.nextLine());
                }
                sc.close();
            } catch(IOException e) {UI.println("File error: " + e);}

            int rows = lines.size();
            cells = new Cell[rows][];

            for(int row = 0; row < rows; row++) {
                String line = lines.get(row);
                int cols = line.length();
                cells[row]= new Cell[cols];
                for(int col = 0; col < cols; col++) {
                    char ch = line.charAt(col);
                    if (ch=='w'){
                        cells[row][col] = new Cell("empty");
                        workerPos = new Position(row,col);
                    }
                    else if (ch=='.') cells[row][col] = new Cell("empty");
                    else if (ch=='#') cells[row][col] = new Cell("wall");
                    else if (ch=='s') cells[row][col] = new Cell("shelf");
                    else if (ch=='b') cells[row][col] = new Cell("box");
                    else {
                        throw new RuntimeException("Invalid char at "+row+","+col+"="+ch);
                    }
                }
            }
            drawWarehouse();
            UI.printMessage("Level "+level+": Push the boxes to their target positions. Use buttons or put mouse over warehouse and use keys (arrows, wasd, ijkl, u)");
        }
    }

    public static void main(String[] args) {
        new Sokoban();
    }
}

//This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 2
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
import javax.swing.border.EmptyBorder;
import java.awt.event.*; 
import java.awt.Color;
/** Genealogy:
 * Prints out information from a genealogical database
 */

public class Genealogy  {

    // all the people:  key is a name,  value is a Person object
    private final Map<String, Person> database = new HashMap<String, Person>();

    private String selectedName;  //currently selected name.

    private boolean databaseHasBeenFixed = false;

    /**
     * Constructor
     */
    public Genealogy() {
        loadData();
        setupGUI();
        setButton();
        FixNameList();
    }
    /*# Record all button and divided them into two group */
    private Set<JButton> Buttons = new HashSet<JButton>();
    /**
     * Buttons and text field for operations.
     */
    public void setupGUI(){
        addButton("Hits",this::hits);
        addButton("Print all names", this::printAllNames);
        addButton("Print all details", this::printAllDetails);
        UI.addTextField("Name", this::selectPerson);
        addButton("Parent details", this::printParentDetails);
        addButton("Add child", this::addChild);
        addButton("Find & print Children", this::printChildren);
        addButton("Fix database", this::fixDatabase);
        addButton("Print GrandChildren", this::printGrandChildren);
        addButton("Print Missing", this::printMissing);
        addButton("Clear Text", UI::clearText);
        addButton("Reload Database", this::loadData);
        addButton("CheekList", this::cheekList);
        UI.addTextField("Descendants & Ancestry", this::Tree);
        addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }
    public void hits(){
        JOptionPane.showMessageDialog(new JFrame().getContentPane(), "CheekList----> cheek if there were any mistake of the database" + 
        "\n Descendants & Ancestry ----> Print the Descendants & Ancestry trees of the person entered" + 
        "\n PrintMissing ----> Print the name which can be find in someone's Father||Mother||Child,but not in the database", 
        "Instructions for use", JOptionPane.INFORMATION_MESSAGE); 
        UI.clearText();
        UI.println("-----------------------------");
    }
    /*# Add the button which are used to editShow*/
    public void addButton(String name,UIButtonListener controller){
        JButton button = UI.addButton(name,controller);
        this.Buttons.add(button);
    }
    /*# Set some color for button(I think it's nice....but not meaningful)*/
    public void setButton(){
        for(JButton b:this.Buttons){
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
    }
    /** 
     * Load the information from the file "database.txt".
     *       Each line of the file has information about one person:
     *        name, year of birth, mother's name, father's name
     *       (note: a '-' instead of a name means  the mother or father are unknown)
     *        For each line,
     *         - construct a new Person with the information, and
     *   - add to the database map.
     */
    public void loadData(){
        try{
            Scanner sc = new Scanner(new File("database.txt"));
            // read the file to construct the Persons to put in the map
            /*# YOUR CODE HERE */
            while(sc.hasNext()){
                String name = sc.next();
                int dob = sc.nextInt();
                String mother = sc.next();
                String father = sc.next();
                database.put(name, new Person(name,dob,mother,father));
            }
            sc.close();
            UI.println("Loaded "+database.size()+" people into the database");
        }catch(IOException e){throw new RuntimeException("Loading database.txt failed" + e);}
    }

    /** Prints out names of all the people in the database */
    public void printAllNames(){
        for (String name : database.keySet()) {
            UI.println(name);
        }
        UI.println("-----------------");
    }

    /** Prints out details of all the people in the database */
    public void printAllDetails(){
        /*# YOUR CODE HERE */
        for (String name : database.keySet()) {
            UI.println(database.get(name));
        }
        UI.println("-----------------");
    }

    /**
     * Store value (capitalised properly) in the selectedName field.
     * If there is a person with that name currently in people,
     *  then print out the details of that person,
     * Otherwise, offer to add the person:
     * If the user wants to add the person,
     *  ask for year of birth, mother, and father
     *  create the new Person,
     *  add to the database, and
     *  print out the details of the person.
     * Hint: it may be useful to make an askPerson(String name) method
     * Hint: remember to capitalise the names that you read from the user
     */
    public void selectPerson(String value){
        selectedName = capitalise(value); 
        /*# YOUR CODE HERE */
        if(database.get(selectedName) == null){
            UI.println("no person called " + selectedName);
            boolean Add = UI.askBoolean("Do you want to add this person?");
            if(Add){
               this.ask(selectedName);
               if(database.get(selectedName)!=null){
                  UI.println(database.get(selectedName).toString());
               }
            }
        }else{
            UI.println(database.get(selectedName).toString());
        }
    }
    /*#Making a new frame to let user choosing the dob and enter the Mname and Fname*/
    boolean fine;
    public void ask(String name){
        JFrame j = new JFrame();
        j.setTitle("Person's Message");     
        j.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE ); 
        j.setBounds(100,100,250,250);
        JTextField fa = new JTextField();
        JComboBox M = new JComboBox();    
        M.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
        JComboBox year = new JComboBox();  
        JLabel d = new JLabel("Dob");
        for(int i = 0; i< 1000 ; i++){
            year.addItem(2019 - i);
        }       
        JTextField father = new JTextField(15);
        JLabel f = new JLabel("Father");
        JTextField mother = new JTextField(15);
        JLabel m = new JLabel("Mother");
        JButton ok = new JButton("Fine");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                Person p = addPerson(name,(int)year.getSelectedItem(),capitalise(mother.getText()), capitalise(father.getText()));
                j.setVisible(false);
                if(!cheek(p)){
                    j.setVisible(true);
                    fine = false;
                }else{
                    fine = true;
                }
            }
        });
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                UI.println("Adding person canceled!");
                database.remove(name);
                fine = true;
                j.setVisible(false);
            }
        });
        j.setContentPane(M);
        j.getContentPane().add(d);
        j.getContentPane().add(year);
        j.getContentPane().add(new JLabel("                                 "));
        j.getContentPane().add(f);
        j.getContentPane().add(father);
        j.getContentPane().add(m);
        j.getContentPane().add(mother);
        j.getContentPane().add(ok);
        j.getContentPane().add(cancel);
        j.setVisible(true);
        while(!fine){
            UI.sleep(10);        
        }
    }
    public Person addPerson(String name,int dob,String father,String mother){
        this.database.put(name,new Person(name,dob,father,mother));
        return new Person(name,dob,father,mother);
    }
    /*#Cheek if the message is reasonable*/
    public boolean cheek(Person p){
        for(String name:database.keySet()){
            if(database.containsKey(p.getFather())){
                if(10 > p.getDOB() - database.get(p.getFather()).getDOB() || p.getDOB() - database.get(p.getFather()).getDOB() >= 80){
                    JOptionPane.showMessageDialog(null, "Please enter the correct dob of this person!" + "\n"
                    + p.getFather() + "'s dob: "  + database.get(p.getFather()).getDOB() + "\n"+ p.getName() + "'s dob: " + p.getDOB(),"MISTAKE!", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            if(database.containsKey(p.getMother())){
                if(10 > p.getDOB() - database.get(p.getMother()).getDOB()|| p.getDOB() - database.get(p.getMother()).getDOB() >= 80){
                    JOptionPane.showMessageDialog(null,"Please enter the correct dob of this person!" + "\n"
                    + p.getMother() + "'s dob: "  + database.get(p.getMother()).getDOB() + "\n"+ p.getName() + "'s dob: " + p.getDOB() , "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        if(p.getMother().equals(p.getFather())){
            JOptionPane.showMessageDialog(null,"Parent's name can not be the same!", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(p.getName().equals(p.getFather())||p.getName().equals(p.getMother())){
            JOptionPane.showMessageDialog(null,p.getName() +" can not be his(her) parent!","MISTAKE!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Set<String> FAncestors = new HashSet<String>();
        Set<String> MAncestors = new HashSet<String>();
        if(database.containsKey(p.getMother())){
            MAncestors = this.getAncestors(p.getMother());
        }
        if(database.containsKey(p.getMother())){
            FAncestors = this.getAncestors(p.getFather());
        }
        for(String ancestor:MAncestors){
            if(p.getName().equals(ancestor)){
                JOptionPane.showMessageDialog(null,p.getName() + "has the same name with his ancestor!", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(p.getFather().equals(ancestor)){
                JOptionPane.showMessageDialog(null,p.getName() + "'s father's name is one of him(her) ancestors", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(p.getMother().equals(ancestor)){
                JOptionPane.showMessageDialog(null,p.getName() + "'s mother's name is one of him(her) ancestors", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        for(String ancestor:FAncestors){
            if(p.getName().equals(ancestor)){
                JOptionPane.showMessageDialog(null,p.getName() + "has the same name with his ancestor!", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(p.getFather().equals(ancestor)){
                JOptionPane.showMessageDialog(null,p.getName() + "'s father's name is one of him(her) ancestors", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if(p.getMother().equals(ancestor)){
                JOptionPane.showMessageDialog(null,p.getName() + "'s mother's name is one of him(her) ancestors", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Print all the details of the mother and father of the person
     * with selectedName (if there is one).
     * (If there is no person with the current name, print "no person called ...")
     * If the mother or father's names are unknown, print "unknown".
     * If the mother or father names are known but they are not in
     *  the database, print "...: No details known".
     */
    public void printParentDetails(){
        /*# YOUR CODE HERE */
        String MotherName = null, FatherName = null;
        if(database.get(selectedName) == null){
            UI.println("no person called " + selectedName);
            return;
        }else{
            MotherName = database.get(selectedName).getMother();
            FatherName = database.get(selectedName).getFather();
        }
        if(MotherName == null){
            UI.println(selectedName+"'s Mother : unknown");
        }else if(database.get(MotherName) == null){
            UI.println(selectedName+"'s Mother: " + MotherName + " No details known");
        }else{
            UI.println(selectedName+"'s Mother:" + database.get(MotherName).toString());
        }
        if(FatherName == null){
            UI.println(selectedName+"'s Father : unknown");
        }else if(database.get(FatherName) == null){
            UI.println(selectedName+"'s Father: "+ FatherName + " No details known");
        }else{
            UI.println(selectedName+"'s Father: " + database.get(FatherName).toString());
        }
        UI.println("-----------------");
    }

    /**
     * Add a child to the person with selectedName (if there is one).
     * If there is no person with the selectedName,
     *   print "no person called ..." and return
     * Ask for the name of a child of the selectedName
     *  (remember to capitalise the child's name)
     * If the child is already recorded as a child of the person,
     *  print a message
     * Otherwise, add the child's name to the current person.
     * If the child's name is not in the current database,
     *   offer to add the child's details to the current database.
     *   Check that the selectedName is either the mother or the father.
     */
    public void addChild(){
        /*# YOUR CODE HERE */
        if(database.get(selectedName) == null){
            UI.println("no person called " + selectedName);return;
        }
        String name = capitalise(UI.askString("What's the name of " + selectedName + "'s child's name?"));
        for(String n:this.database.keySet()){
            if(n.equals(name)){
                UI.println("The name had been used in the database!");
                return;
            }
        }
        if(database.get(selectedName).getChildren().contains(name)){
            UI.println("The child has been recorded!");
            return;
        }else{
            database.get(selectedName).addChild(name);
        }
        boolean Add = UI.askBoolean("Do you want to add " + name + " into the database?");
        if(Add){
            this.ask(name);
            Person child = this.database.get(name);
            if(child != null&&!selectedName.equals(child.getFather())&&!selectedName.equals(child.getMother())){
                JOptionPane.showMessageDialog(null,"One of the parents'name should be the name you selected!(" + selectedName + ")", "MISTAKE!", JOptionPane.ERROR_MESSAGE);
                this.database.remove(name);
                this.ask(name);
            }
        }
        if(database.containsKey(name)){ UI.println(database.get(name).toString());}
        UI.println("-----------------");
    }

    /**
     * Print the number of children of the selectedName and their names (if any)
     * Find the children by searching the database for people with
     * selectedName as a parent.
     * Hint: Use the findChildren method (which is needed for other methods also)
     */
    public void printChildren(){
        /*# YOUR CODE HERE */
        Set<String> Children = this.findChildren(selectedName);
        UI.println(selectedName + "'s Children: ");
        for(String name:Children){
            UI.println(name);
        }
        UI.println("-----------------");
    }

    /**
     * Find (and return) the set of all the names of the children of
     * the given person by searching the database for every person 
     * who has a mother or father equal to the person's name.
     * If there are no children, then return an empty Set
     */
    public Set<String> findChildren(String name){
        /*# YOUR CODE HERE */
        if(database.get(name) == null){
            UI.println("no person called " + name);
            return new HashSet<String>();
        }else{
            for(String n: this.database.keySet()){
                if((this.database.get(n).getFather()!=null && this.database.get(n).getFather().equals(name))
                ||(this.database.get(n).getMother()!=null && this.database.get(n).getMother().equals(name))){
                    this.database.get(name).addChild(n);
                }
            }
            return database.get(name).getChildren();
        }
    }

    /**
     * When the database is first loaded, none of the Persons will
     * have any children recorded in their children field. 
     * Fix the database so every Person's children includes all the
     * people that have that Person as a parent.
     * Hint: use the findChildren method
     */
    /*# Find th e relationship betweend all name in the database*/
    public void fixDatabase(){
        /*# YOUR CODE HERE */
        for(String name:this.database.keySet()){
            this.findChildren(name);
            UI.println(name + "'s children : " + this.database.get(name).getChildren());
        }
        databaseHasBeenFixed = true;
        UI.println("Found children of each person in database\n-----------------");
    }

    /**
     * Print out all the grandchildren of the selectedName (if any)
     * Assume that the database has been "fixed" so that every Person
     * contains a set of all their children.
     * If the selectedName is not in the database, print "... is not known"
     */
    public void printGrandChildren(){
        if (!databaseHasBeenFixed) { UI.println("Database must be fixed first!");}
        if (!database.containsKey(selectedName)){
            UI.println("That person is not known");
            return;
        }
        /*# YOUR CODE HERE */
        if(database.get(selectedName) == null){
            UI.println("no person called " + selectedName);
            return;
        }
        UI.println(this.selectedName + "'s grandChildren :");
        boolean isFind = false;
        Set<String> Children = this.findChildren(selectedName);
        for(String ChildName:Children){
            Set<String> GrandChildren = this.findChildren(ChildName);
            UI.println(GrandChildren.toString());
            isFind = true;
        }
        if(!isFind){
            UI.println(this.selectedName + " have not any grandchildren!");
        }
        UI.println("------------------");
    }

    /**
     * Print out all the names that are in the database but for which
     * there is no Person in the database. Do not print any name twice.
     * These will be names of parents or children of Persons in the Database
     * for which a Person object has not been created.
     */
    /*# Find all name (inculding the  name which is not in the database)*/
    private Set<String> nameList,missName;
    public void FixNameList(){
        nameList = new HashSet<String>();
        missName = new HashSet<String>();
        for(String name:database.keySet()){
            if(!nameList.contains(name)){   nameList.add(name); }
            String father = database.get(name).getFather();
            String mother = database.get(name).getMother();
            if(!database.containsKey(father)&&father!=null&&!nameList.contains(father)){  nameList.add(father); }
            if(!database.containsKey(mother)&&mother!=null&&!nameList.contains(mother)){  nameList.add(mother); }
            Set<String> children = database.get(name).getChildren();
            for(String child:children){
                if(!nameList.contains(child)){  nameList.add(child); }
            }
        }
        for(String name:this.nameList){
            if(!this.database.containsKey(name)){
                this.missName.add(name);
            }
        }
    }
    /*# Find  if there are any name which not in the database but can be find in namelist
       Like if adding a person and that person's parents' name are not in the database*/
    public void printMissing(){
        UI.println("Missing names:");
        /*# YOUR CODE HERE */
        this.FixNameList();
        for(String name:this.missName){
            UI.println(name);
        }
        UI.println("------------------");
    }
    /*#Using the recursion to find the tree*/
    String m,f,c;
    int deep = 0;
    public void Tree(String name){
        name = capitalise(name);
        UI.clearText();
        if(!database.containsKey(name)){
            UI.println(name + " is not in the database!");
            return;
        }
        UI.println(name + "'s ancestry tree :");
        UI.println(name + "(" +  database.get(name).getDOB()+ ")" + ":");
        this.ParentsTree(name, this.deep);
        deep =0;
        UI.println("#######################");
        UI.println(name + "'s descendants tree :");
        UI.println(name + "(" +  database.get(name).getDOB()+ ")" + ":");
        this.ChildrenTree(name,this.deep);
    }
    
    public void ParentsTree(String name,int deep){
        if(this.database.get(name) == null){return;}
        if(this.database.get(name).getMother() != null){
            f = this.database.get(name).getMother();
            String space = "    ";
            for(int i = 0;i<deep;i++){ space = space + "    ";}
            UI.println(space + "M: " + f +"(" +  database.get(f).getDOB()+ ")");
            deep++;
            ParentsTree(f,deep);
        }
        if(this.database.get(name).getFather() != null){
            m = this.database.get(name).getFather();
            String space = "    ";
            for(int i = 0;i<deep;i++){ space = space + "    ";}
            UI.println(space + "F: " + m + "(" +  database.get(m).getDOB()+ ")");
            deep++;
            ParentsTree(m,deep);
        }
    }
    
    public void ChildrenTree(String name,int deep){
        if(this.database.get(name) == null){return;}
        Set<String> Children = this.findChildren(name);
        for(String Child : Children){
            String space = "    ";
            for(int i = 0;i<deep;i++){ space = space + "    ";}
            UI.println(space + Child + "(" + this.database.get(Child).getDOB() + ")");
            c = Child;
            ChildrenTree(c,deep+1);
        }
    }
    
    public void FindAncestor(String name,Set<String> ancestor){
        if(this.database.get(name) == null){return;}
        if(this.database.get(name).getMother() != null){
            f = this.database.get(name).getMother();
            ancestor.add(f);
            FindAncestor(f,ancestor);
        }
        if(this.database.get(name).getFather() != null){
            m = this.database.get(name).getFather();
            ancestor.add(m);
            FindAncestor(m,ancestor);
        }
    }
    /*# Get one's ancestors*/
    public Set<String> getAncestors(String name){
        Set<String> ancestors = new HashSet<String>();
        this.FindAncestor(name, ancestors);
        return ancestors;
    }
    /*# Cheek this person, if his message is not reasonable, print the mistake*/
    public void cheekPerson(Person p){
        String name = p.getName();
        if(database.containsKey(p.getFather())){
            if(p.getDOB() - database.get(p.getFather()).getDOB() < 10  || p.getDOB() - database.get(p.getFather()).getDOB() >= 75){
                UI.println("Mistake: " +"\n"+ name + "'s dob: " + p.getDOB()  +"\n" + name+"'s father: " + p.getFather()+ "'s dob: " + database.get(p.getFather()).getDOB());
                UI.println("Dad is " + (p.getDOB() - database.get(p.getFather()).getDOB()) + " years older than his child.");
                UI.println("-------------------------------");
            }
        }
        if(database.containsKey(p.getMother())){
            if(p.getDOB() - database.get(p.getMother()).getDOB() < 10  || p.getDOB() - database.get(p.getMother()).getDOB() >= 75){
                UI.println("Mistake: " + name + "'s dob: " + p.getDOB() +"\n" + name+"'s mother:" + p.getMother() + "'s dob: " + database.get(p.getMother()).getDOB());
                UI.println("Mom is " + (p.getDOB() - database.get(p.getMother()).getDOB()) + " years older than his child.");
                UI.println("-------------------------------");
            }
        }
        if(database.containsKey(p.getMother())&&database.containsKey(p.getFather())){
            if(Math.abs(database.get(p.getFather()).getDOB() - database.get(p.getMother()).getDOB())>80){
                UI.println("Mistake: " + p.getName() + "'s parents are too different in age");
                UI.println(name+"'s father: " + p.getFather()+ "'s dob: " + database.get(p.getFather()).getDOB());
                UI.println(name+"'s mother:" + p.getMother() + "'s dob: " + database.get(p.getMother()).getDOB());
                UI.println("Difference: " + (Math.abs(database.get(p.getFather()).getDOB() - database.get(p.getMother()).getDOB())));
                UI.println("-------------------------------");
            }
        }
        Set<String> ancestors = this.getAncestors(name);
        for(String ancestor:ancestors){
            if(name.equals(ancestor)){
                UI.println("#########################");
                UI.println(name + "has the same name with his ancestor!");
                UI.println("#########################");
            }
        }
    }
    /*# Cheek the database,find the mistake of those person*/
    public void cheekList(){
        for(Person p:database.values()){
            this.cheekPerson(p);
        }
    }
    
    /**
     * Return a capitalised version of a string
     */
    public String capitalise(String s){
        if(s == null){ return null;}
        if(s.length()<2){
            return s.substring(0).toUpperCase();
        }
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void main(String[] args) throws IOException {
        new Genealogy();
    }
}

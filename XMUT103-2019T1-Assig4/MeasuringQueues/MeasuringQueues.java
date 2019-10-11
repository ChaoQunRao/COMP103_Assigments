// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for XMUT103 - 2019T1, Assignment 4
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;

/** 
 *  Measures the performance of different ways of doing a priority queue of Items
 *  Uses an Item class that has nothing in it but a priority (so it takes
 *   minimal time to construct a new Item).
 *  The Item constructor doesn't need any arguments
 *  Remember that small priority values are the highest priority - 1 is higher priority than 10.
 *  Algorithms to measure:
 *      Use a built-in PriorityQueue
 *      Use an ArrayList, with queue's head at 0,   sorting when you add an item.
 *      Use an ArrayList, with queue's head at end, sorting when you add an item.
 *  Each method should have an items parameter, which is a collection of Items
 *    that should be initially added to the queue (eg  new PriorityQueue<Item>(items); or
 *    new ArrayList<Item>(items))
 *    It should then repeatedly dequeue an item from the queue, and enqueue a new Item(). 
 *    It should do this 100,000 times.
 *    (the number of times can be changed using the textField)
 *  To test your methods, you should have debugging statements such as UI.println(queue)
 *   in the loop to print out the state of the queue. You could comment them out before measuring.
 */

public class MeasuringQueues{

    private static final int TIMES=100000;  //Number of times to repeatedly dequeue and enqueue item 

    /**
     * Construct a new MeasuringQueues object
     */
    public MeasuringQueues(){
        setupGUI();
    }

    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.addButton("Measure", this::measure);
        UI.addButton("Quit", UI::quit);
        UI.setDivider(1.0);
    }

    /**
     * Create a priority queue using a PriorityQueue, 
     * adding all the items in the collection to the queue.
     * (n will be the size of the the collection in the items parameter).
     * Then, dequeue and enqueue TIMES times.
     */
    public void useQueuesPQ(Collection<Item> items){
        Queue<Item> queue = new PriorityQueue<Item>(items);
        // UI.println(queue);
        for (int i=0; i<TIMES; i++){
            queue.poll();
            queue.offer(new Item());
            // UI.println(queue);
        }
    }

    /**
     * Create a queue using an ArrayList with the head at the end.
     * Make a new ArrayList using all the items in the collection,
     * and then sorting the list.
     * Then, dequeue and enqueue TIMES times.
     * (n will be the size of the the collection in the items parameter).
     * Note: Head of queue is at the end of the list, 
     * so we need to sort in the reverse order of Items (so the smallest value comes at the end)
     */
    public void useQueuesALEnd(Collection<Item> items){
        /*# YOUR CODE HERE */

    }

    
    /**
     * Create a queue using an ArrayList with the head at the start.
     * Head of queue is at the start of the list
     * Make a new ArrayList using all the items in the collection,
     * and then sorting the list.
     * Then, dequeue and enqueue TIMES times.
     * (n will be the size of the the collection in the items parameter).
     */
    public void useQueuesALStart(Collection<Item> items){
        /*# YOUR CODE HERE */

    }


    /**
     * For a sequence of values of n, from 1000 to 1024000,
     *  - Construct a collection of n Items (This step shouldn't be included in the time measurement)
     *  - call each of the methods, passing the collection, and measuring
     *    how long each method takes to dequeue and enqueue an Item 100,000 times
     */
    public void measure(){
        UI.printf("Measuring methods using %d repetitions, on queues of size 1000 up to 1,024,000\n",TIMES);
        UI.println("       n      PQ      ALEnd   ALStart");
        /*# YOUR CODE HERE */

    }


    public static void main(String[] arguments){
        new MeasuringQueues();
    }

}

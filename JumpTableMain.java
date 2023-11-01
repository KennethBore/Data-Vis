
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.io.*;

/**
 * The main class for the program.
 */
public class JumpTableMain { 
	public static void main(String[] args) { 
		Screen screen = new Screen(); 
		boolean keepRunning = true; 
		while(keepRunning) { 
			keepRunning = screen.doState(); 
		} 
	} 
}

// Interface for the Enter and Exit methods
interface StateEnterExitMeth {
    public void invoke();
}
// Interface for the stay methods
interface StateStayMeth {
    public boolean invoke();
}

// Possible states
enum State {
    NONE,
    IDLE,
    LIST,
    STACK,
    QUEUE;
}

class Screen {
    // Hashmaps with a state and a corresponding function
    private HashMap<State, StateEnterExitMeth> stateEnterMeths;
    private HashMap<State, StateStayMeth> stateStayMeths;
    private HashMap<State, StateEnterExitMeth> stateExitMeths;

    // Data Structures
    private Stack<Character> stack;
    private ArrayList<Character> list;
    private Queue<Character> queue;


    // File Paths
    private final String STACK_FILE_PATH = "stack.txt";
    private final String QUEUE_FILE_PATH = "queue.txt";
    private final String LIST_FILE_PATH = "list.txt";
    // Reset to defualt
    private final String ANSI_RESET = "\u001b[0m";
    // Yellow
    private final String ANSI_YELLOW = "\u001B[33m";
    // RED
    private final String ANSI_RED = "\u001B[31m";
    // GREEN
    private final String ANSI_GREEN = "\u001B[32m";
    // BLUE
    private final String ANSI_BLUE = "\u001B[34m";

    // Scanner for input
    Scanner scanner = new Scanner(System.in);

    // State of the program
    private State state;

    // CONST
    public Screen(){
        // Stack, queue, and list
        stack = new Stack<>();
        queue = new LinkedList<>();
        list = new ArrayList<>();

        // Jumptables
        stateEnterMeths = new HashMap<>();
        stateStayMeths = new HashMap<>();
        stateExitMeths = new HashMap<>();

        // Fill the tables
        stateEnterMeths.put(State.IDLE, () -> { StateEnterIdle(); });
        stateEnterMeths.put(State.LIST, () -> { StateEnterList(); });
        stateEnterMeths.put(State.STACK, () -> { StateEnterStack(); });
        stateEnterMeths.put(State.QUEUE, () -> { StateEnterQueue(); });

        stateStayMeths.put(State.IDLE, () -> {return StateStayIdle(); });
        stateStayMeths.put(State.LIST, () -> {return StateStayList(); });
        stateStayMeths.put(State.QUEUE, () -> {return StateStayQueue(); });
        stateStayMeths.put(State.STACK, () -> {return StateStayStack(); });

        stateExitMeths.put(State.IDLE, () -> { StateExitIdle(); });
        stateExitMeths.put(State.LIST, () -> { StateExitList(); });
        stateExitMeths.put(State.QUEUE, () -> { StateExitQueue(); });
        stateExitMeths.put(State.STACK, () -> { StateExitStack(); });

        state = State.NONE;
        changeState(State.IDLE);
    }


    /**
     * Clear the screen, exit the previous state and
     * change the state of the program
     * Goes to the Enter methods
     * @param newState The state to change to
     */
    private void changeState(State newState){
        // Check if the state is being changed
        if(state != newState){
            // Exit the old state
            if (stateExitMeths.containsKey(state)){
                stateExitMeths.get(state).invoke();
            }
            // Enter the new state
            state = newState;
            if (stateEnterMeths.containsKey(newState)){
                stateEnterMeths.get(newState).invoke();
            }
        }
    }

    /**
     * <code>doState</code>
     * <p>
     * Excecutes the current state method
     * 
     * @return determines whether or not to stay in the loop.
     * True will keep the program running, false will exit
     */
    public boolean doState(){
        clearScreen(); // Clear the screen

        // Check if the current state is in the dictionary
        if(stateStayMeths.containsKey(state)){
            return stateStayMeths.get(state).invoke();
        }
        // Return false if the state isnt found
        return false;
    }


    /**
     * Called at the end of changeState()
     * Enter Into a state.
     * Populate the data structure. 
     */
    private void StateEnterIdle(){
    }
    private void StateEnterList(){
        populateList();
    }
    private void StateEnterStack(){
        populateStack();
    }
    private void StateEnterQueue(){
        populateQueue();
    }

    
    /**
     * Stay in a state
     * Contains operations for the data structure
     * Change state to Enter when the screen needs to be redrawn
     * @return False if the user leaves
     */
    private boolean StateStayIdle(){
        // Get the user input, then do that action
        System.out.print(ANSI_YELLOW + "\nWhere next?\n1.Stack\n2.Queue\n3.List\n4.Quit\n? " + ANSI_RESET);
        String input = scanner.nextLine();
        char option = input.charAt(0);
        switch (option) {
            case '1':
                changeState(State.STACK);
                break;
            case '2':
                changeState(State.QUEUE);
                break;
            case '3':
                changeState(State.LIST);
                break;
            case '4':
                return false;
            default:
                changeState(state);
        }
        return true;
    }
    private boolean StateStayList(){
        drawList();
        System.out.print(ANSI_YELLOW+ "\nWhere next?\n1.Append\n2.Remove\n3.Save & Move to Stack\n4.Save & Move to Queue\n5.Quit\n? " + ANSI_RESET);
        
        //Bonus.check(list);

        String input = scanner.nextLine();
        // First choice
        char option = input.charAt(0);
        switch (option) {
            case '1':
                if(input.length()>2){
                    list.add(input.charAt(2));
                }
                break;
            case '2':
                if(list.size() != 0){
                    list.remove(list.size()-1);
                }
                break;
            case '3':
                changeState(State.STACK);
                break;
            case '4':
                changeState(State.QUEUE);
                break;
            case '5':
                changeState(State.NONE);
                return false;
            default:
                changeState(state);
        }
        return true;
    }
    private boolean StateStayStack(){
        drawStack();
        System.out.print(ANSI_YELLOW + "\nWhere next?\n1.Pop\n2.Push\n3.Save & Move to List\n4.Save & Move to Queue\n5.Quit\n? " + ANSI_RESET);

       //Bonus.check(stack);

        String input = scanner.nextLine();
        char option = input.charAt(0);
        // Check the user input
        switch (option) {
            // Pop from the stack if the stack is not empty
            case '1':
                if(!stack.isEmpty()){
                    stack.pop();
                }
                break;
            // Push to the stack
            case '2':
                if(input.length()>2){
                    stack.push(input.charAt(2));
                }
                break;
            // Save to the file and switch state to the list
            case '3':
                changeState(State.LIST);
                break;
            // Save to the file and switch state to the Queue
            case '4':
                changeState(State.QUEUE);
                break;
            // Quit the program
            case '5':
                changeState(State.NONE);
                return false;
            default:
                changeState(State.NONE);
        }
        return true;
    }
    private boolean StateStayQueue(){
        drawQueue();
        System.out.print(ANSI_YELLOW+ "\nWhere next?\n1.Enqueue\n2.Dequeue\n3.Save & Move to Stack\n4.Save & Move to List\n5.Quit\n? " + ANSI_RESET);

        //Bonus.check(queue);

        String input = scanner.nextLine();
        char option = input.charAt(0);
        switch (option) {
            case '1':
                if(input.length()>2){
                    queue.offer(input.charAt(2));
                }
                break;
            case '2':
                if(queue.peek() != null){ 
                    queue.poll();
                }
                break;
            case '3':
                changeState(State.STACK);
                break;
            case '4':
                changeState(State.LIST);
                break;

            case '5':
                changeState(State.NONE);
                return false;
            
            default:
                changeState(state);
        }
        return true;
    }


    /**
     * Exit the state. 
     * Save to a file.
     */
    private void StateExitIdle(){
    }
    private void StateExitList(){
        writeToList();
    }
    private void StateExitStack(){
        writeToStack();
    }
    private void StateExitQueue(){
        writeToQueue();
    }


    // ---HELPER Methods---
 
    /**
     * Gets the contents of the file 'stack.txt', then 
     * fills the stack with the values.
     * Called by the StateEnterStack method
     */
    private void populateStack(){
        // Read the stack file
        String data = readFile("stack");
        for(int i=0; i<data.length(); i++){
            // if the character is not a comma, push it to the stack
            if(data.charAt(i) != ','){
                stack.push(data.charAt(i));
            }
        }
    }

    /**
     * Gets the contents of the "list.txt" file.
     * Fills the list with the contents of the file
     * Called by StateEnterList method
     */
    private void populateList(){
        // New list to prevent duplicate data
        //list = new ArrayList<>();
        // Read file
        String data = readFile("list");
        // Fill list
        for(int i=0; i<data.length(); i++){
            if(data.charAt(i) != ','){
                list.add(data.charAt(i)); // Add the character if it isnt a ','
            }
        }
    }

    /**
     * Fill the queue with the data from the 'queue.txt' file
     */
    private void populateQueue(){
        // Read file
        String data = readFile("queue");
        // Fill Queue
        for(int i=0; i<data.length(); i++){
            if(data.charAt(i) != ','){
                queue.add(data.charAt(i)); // Add the character if it isnt a ','
            }
        }
    }


    /**
     * Draw the stack
     */
    private void drawStack(){
        // A temp stack to hold the main stack 
        Stack<Character> temp = new Stack<>();

        String stackStr = "";
        stackStr += "|   |\n|---|\n"; // Top of the stack
        while(!stack.isEmpty()){
            // Take the character off the stack
            Character c = stack.pop();
            // Represent it on the stack
            stackStr += "| " + c + " |\n|---|\n";
            // Push it to the temp stack
            temp.push(c);
        }
        while(!temp.isEmpty()){
            stack.push(temp.pop());
        }
        System.out.print(ANSI_RED + stackStr + ANSI_RESET);
    }

    /**
     * Draw the List
     */
    private void drawList(){
        // Empty string for the output
        String strList = "";
        strList = "{ ";
        for(int i=0; i<list.size(); i++){
            // Add each character seperated by a comma
            strList += list.get(i) + ", ";
        }
        strList += "}";
        System.out.println(ANSI_BLUE + strList + ANSI_RESET);
    }

    /**
     * Draw the Queue
     */
    private void drawQueue(){
        // temp queue
        Queue<Character> tempQ = new LinkedList<>();
        String tempStr = "| ";
        while(queue.peek() != null){
            Character c = queue.remove();
            tempStr += c + " | ";
            tempQ.add(c);
        }
        tempStr += "  |";
        System.out.println(ANSI_GREEN + tempStr + ANSI_RESET);
        while(tempQ.peek() != null){
            queue.add(tempQ.remove());
        }
    }


    /**
     * Takes the name of a text file and reads it. The contents of the file are
     * then returned to one of the draw functions that called it.
     * @param fileName Name of the text file
     * @return Contents of the text file.
     */
    private String readFile(String fileName){
        File file = new File(fileName + ".txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while((st = br.readLine()) != null){
                return st;
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Writes to the 'stack.txt' file.
     * Creates a temp stack so that the data is 
     * pushed in the right order.
     */
    private void writeToStack(){
        Stack<Character> temp = new Stack<>();
        while(!stack.empty()){
            temp.push(stack.pop());
        }
        try {
            FileWriter fw = new FileWriter(STACK_FILE_PATH, false);
            while(!temp.isEmpty()){
                fw.write(temp.pop().charValue() + ",");
            }
            fw.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * Writes to the 'list.txt' file in the same order as the list
     */
    private void writeToList(){
        try{
            FileWriter fw = new FileWriter(LIST_FILE_PATH, false);
            for(int i=0; i<list.size(); i++){
                fw.write(list.get(i)+ ",");
            }
            fw.close();
			list.clear();
        } catch (Exception e){
            e.getStackTrace();
        }
		
    }

    /**
     * Writes to the 'queue.txt' file.
     * Creates a temp queue to hold the data so the order
     * is preserved.
     */
    private void writeToQueue(){
        Queue<Character> temp = new LinkedList<>();
        while(queue.peek() != null){
            temp.add(queue.remove());
        }
        try {
            FileWriter fw = new FileWriter(QUEUE_FILE_PATH, false);
            while(!temp.isEmpty()){
                fw.write(temp.remove().charValue() + ",");
            }
            fw.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    /**
     * Clear the screen so that the new screen can be seen.
     */
    private void clearScreen(){
        // Get the name of the os
        String osName = System.getProperty("os.name").toLowerCase();
        try{
            if (osName.contains("win")){
                // Use the proccess builder if the system is windows
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Use the character method if the system is linux
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

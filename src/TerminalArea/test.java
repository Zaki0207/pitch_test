package TerminalArea;

import java.util.Stack;

public class test {
    public static void main(String[] args) throws InterruptedException {
        Stack<String> stack = new Stack<String>();

        // Use add() method to add elements in the Stack
        stack.push("Geeks");
        stack.push("for");
        stack.push("Geeks");
        stack.push("10");
        stack.push("20");

        // Displaying the Stack
        System.out.println("Stack: " + stack);

        // Fetching the specific element from the Stack
        System.out.println("The element is: "
                + stack.get(stack.size()-1));
        Thread.sleep(500);
    }
}

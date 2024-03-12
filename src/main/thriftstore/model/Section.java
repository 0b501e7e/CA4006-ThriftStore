package src.main.thriftstore.model;

import java.util.LinkedList;
import java.util.List;

public class Section {
    private final String name; // The name of the section (e.g., "electronics", "clothing")
    private final List<Item> items = new LinkedList<>(); // A list to hold items in this section

    private final Integer limit = 20;

    // Constructor to initialize a Section with a name
    public Section(String name) {
        this.name = name;
    }

    private int waitingCustomers = 0;

    public synchronized void addWaitingCustomer() {
        waitingCustomers++;
    }

    public synchronized void removeWaitingCustomer() {
        if (waitingCustomers > 0) {
            waitingCustomers--;
        }
    }

    public synchronized int getWaitingCustomers() {
        return waitingCustomers;
    }


    // Adds an item to the section; synchronized to manage concurrent access
    public synchronized void addItem(Item item) {
        if (!isFull()) {
            items.add(item); // Add the item to the list
            notifyAll(); // Notify all waiting threads (e.g., customers waiting for an item)
        }
        else {
            // wait?
        }
    }

    // Check if the section is full
    public synchronized boolean isFull() {
        return items.size() >= limit;
    }

    // Check if the section is empty
    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    // Removes and returns the first item from the section; waits if no items are available
    public synchronized Item removeItem() throws InterruptedException {
        while (items.isEmpty()) { // If the section is empty, wait
            wait();
        }
        return items.remove(0); // Remove and return the first item
    }

    // Returns the name of the section
    public String getName() {
        return name;
    }
}

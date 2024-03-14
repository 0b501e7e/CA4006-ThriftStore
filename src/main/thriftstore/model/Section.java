package src.main.thriftstore.model;

import java.util.LinkedList;
import java.util.List;

public class Section {
    private final String name; // The name of the section (e.g., "electronics", "clothing")
    private final List<Item> items = new LinkedList<>(); // A list to hold items in this section
    private int waitingCustomers = 0;
    private boolean isBeingStocked = false;

    // Constructor to initialize a Section with a name
    public Section(String name, int initialItems) throws InterruptedException {
        this.name = name;

        for (int i = 0; i < initialItems; i++) {
            addItem(new Item(name));
        }
    }

    public int getItems() {
        return items.size();
    }

    // Method to set the section as being stocked
    public synchronized void startStocking() {
        this.isBeingStocked = true;
    }

    // Method to set the section as not being stocked
    public synchronized void finishStocking() {
        this.isBeingStocked = false;
        notifyAll(); // Notify waiting customers
    }

    // Method to check if the section is being stocked
    public synchronized boolean isBeingStocked() {
        return this.isBeingStocked;
    }


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
    public synchronized void addItem(Item item) throws InterruptedException {
        // wait while section is full
        while (isFull()) {
            wait();
        }
        items.add(item);
        notifyAll();
    }

    // Check if the section is full
    public synchronized boolean isFull() {
        int limit = 20;
        return items.size() >= limit;
    }

    // Check if the section is empty
    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    // Removes and returns the first item from the section; waits if no items are available
    public synchronized Item removeItem() throws InterruptedException {
        while (items.isEmpty() || isBeingStocked) { // If the section is empty, wait
            wait();
        }
        return items.remove(0); // Remove and return the first item
    }

    // Returns the name of the section
    public String getName() {
        return name;
    }
}

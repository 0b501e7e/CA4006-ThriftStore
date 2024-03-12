package src.main.thriftstore.model;

import java.util.LinkedList;
import java.util.List;

public class Section {
    private final String name; // The name of the section (e.g., "electronics", "clothing")
    private final List<Item> items = new LinkedList<>(); // A list to hold items in this section

    // Constructor to initialize a Section with a name
    public Section(String name) {
        this.name = name;
    }

    // Adds an item to the section; synchronized to manage concurrent access
    public synchronized void addItem(Item item) {
        items.add(item); // Add the item to the list
        notifyAll(); // Notify all waiting threads (e.g., customers waiting for an item)
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

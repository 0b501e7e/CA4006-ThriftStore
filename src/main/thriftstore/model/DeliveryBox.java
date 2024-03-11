package main.thriftstore.model;

import java.util.ArrayList;
import java.util.List;

public class DeliveryBox {
    private final List<Item> items = new ArrayList<>(); // Holds items to be stocked by assistants

    // Adds new items to the delivery box and notifies assistants waiting for items
    public synchronized void addItems(List<Item> newItems) {
        items.addAll(newItems); // Add new items to the list
        notifyAll(); // Notify all waiting threads that items have been added
    }

    // Allows assistants to take a specified number of items from the box
    public synchronized List<Item> takeItems(int count) throws InterruptedException {
        while (items.size() < count) { // Wait if not enough items are available
            wait();
        }
        List<Item> taken = new ArrayList<>(items.subList(0, count)); // Take the specified number of items
        items.subList(0, count).clear(); // Remove the taken items from the list
        return taken; // Return the list of taken items
    }
}

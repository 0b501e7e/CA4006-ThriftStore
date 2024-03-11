package main.thriftstore.concurrent;

// Imports from the model package for item handling and the concurrent package for managing shared resources
import main.thriftstore.model.Item;
import main.thriftstore.model.Section;
import main.thriftstore.model.DeliveryBox;
import java.util.Map;
import java.util.List;

// The Assistant class implements Runnable for concurrent execution in a thread
public class Assistant implements Runnable {
    // DeliveryBox to take items from for stocking
    private final DeliveryBox deliveryBox;
    // Sections mapped by their names for efficient lookup and stocking
    private final Map<String, Section> sections;
    // Unique identifier for each assistant for tracking and logging purposes
    private final int id;

    // Constructor initializes the assistant with an ID, delivery box, and sections map
    public Assistant(int id, DeliveryBox deliveryBox, Map<String, Section> sections) {
        this.id = id;
        this.deliveryBox = deliveryBox;
        this.sections = sections;
    }

    // The core logic of stocking items executed when the assistant thread starts
    @Override
    public void run() {
        try {
            // Continuously try to stock items until the thread is interrupted
            while (!Thread.currentThread().isInterrupted()) {
                // Take up to 10 items from the delivery box
                List<Item> items = deliveryBox.takeItems(10);
                for (Item item : items) {
                    // Find the section for each item based on its category and add the item to it
                    Section section = sections.get(item.getCategory());
                    section.addItem(item);
                    // Log the action for tracking
                    System.out.println("Assistant " + id + " stocked " + item.getCategory());
                    // Simulate time taken to stock an item
                    Thread.sleep(1);
                }
            }
        } catch (InterruptedException e) {
            // Handle interruption (e.g., from stopping the simulation) gracefully
            Thread.currentThread().interrupt();
        }
    }
}

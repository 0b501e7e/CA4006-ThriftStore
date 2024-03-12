package src.main.thriftstore.concurrent;

// Necessary imports for handling items and sections from the model package, and for utility purposes
import src.main.thriftstore.model.Item;
import src.main.thriftstore.model.Section;
import java.util.Map;
import java.util.Random;

public class Customer implements Runnable {
    private final Map<String, Section> sections; // Store sections accessible to the customer
    private final int id; // Unique identifier for the customer
    private static final Random random = new Random(); // Random generator for selecting items

    public Customer(int id, Map<String, Section> sections) {
        this.id = id; // Assign the customer ID
        this.sections = sections; // Initialize sections from which the customer can buy items
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Select a random section from which to buy an item
                String[] categories = sections.keySet().toArray(new String[0]);
                String category = categories[random.nextInt(categories.length)];
                Section section = sections.get(category);
                
                synchronized (section) {
                    while (section.isEmpty()) {
                        section.wait(); // Wait for an item to become available
                    }
                    // Attempt to remove (buy) an item from the chosen section
                    Item item = section.removeItem();
                    System.out.println("Customer " + id + " bought " + item.getCategory() + " from " + category);
                }
                
                // Simulate time delay for buying an item
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // Handle possible interruption of the customer thread
            Thread.currentThread().interrupt();
        }
    }
}

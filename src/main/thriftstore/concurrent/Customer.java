package src.main.thriftstore.concurrent;

// Necessary imports for handling items and sections from the model package, and for utility purposes
import src.main.thriftstore.ThriftStore;
import src.main.thriftstore.model.Item;
import src.main.thriftstore.model.Section;
import java.util.Map;
import java.util.Random;

// a customer needs to be able to check if a section is being stocked and wait for that to be finished
// takes 1 tick to take an item
// Every 10 ticks on average a customer buys an item from a random section
// Will wait for an item to become available if empty


public class Customer implements Runnable {
    private final Map<String, Section> sections; // Store sections accessible to the customer
    private final int id; // Unique identifier for the customer
    private static final Random random = new Random(); // Random generator for selecting items

    public Customer(int id, Map<String, Section> sections) {
        this.id = id; // Assign the customer ID
        this.sections = sections; // Initialize sections from which the customer can buy items
    }

    private void log(String action, String details) {
        long threadId = Thread.currentThread().threadId();
        System.out.printf("[Thread: %d, Customer: %d] %s - %s%n", threadId, id, action, details);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                double probabilityOfPurchaseAttempt = 1.0 / 10; // On average, one purchase attempt every 10 ticks.
                String[] categories = sections.keySet().toArray(new String[0]);
                String category = categories[random.nextInt(categories.length)];
                Section section = sections.get(category);
                if (random.nextDouble() < probabilityOfPurchaseAttempt) {

                    synchronized (section) {
                        if (section.isEmpty() || section.isBeingStocked()) {
                            section.addWaitingCustomer();
                            section.wait(); // Wait for an item to become available
                            log("Waiting for item", String.format("Waiting for item in category: %s", category));
                        }
                            // Attempt to remove (buy) an item from the chosen section
                            section.removeWaitingCustomer();
                            Item item = section.removeItem();
                            // Log the purchase.
                            log("Purchased item", String.format("Bought %s", item.getCategory()));

                    }
                }

                // Sleep until the next tick.
                Thread.sleep(ThriftStore.TICK_TIME_SIZE);
            }
        } catch (InterruptedException e) {
            log("Interrupted", "Customer thread was interrupted.");
            Thread.currentThread().interrupt();
        }
    }

}

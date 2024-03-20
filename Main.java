import src.main.thriftstore.ThriftStore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import src.main.thriftstore.model.Section;
import src.main.thriftstore.model.DeliveryBox;
import src.main.thriftstore.concurrent.Assistant;
import src.main.thriftstore.concurrent.Customer;
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Initialize sections
        Map<String, Section> sections = new HashMap<>();
        String[] categories = {"electronics", "clothing", "furniture", "toys", "sporting goods", "books"};
        for (String category : categories) {
            sections.put(category, new Section(category, 5));
        }

        // Initialize the delivery box
        DeliveryBox deliveryBox = new DeliveryBox();

        // Define the number of assistants and customers
        int assistantCount = 2;
        int customerCount = 5;
        // Override with command-line arguments if provided
        if (args.length >= 2) {
            try {
                assistantCount = Integer.parseInt(args[0]);
                customerCount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid input for number of assistants or customers, using default values.");
            }
        }

        // Create the ThriftStore instance with initialized components
        ThriftStore thriftStore = new ThriftStore(sections, deliveryBox, assistantCount, customerCount);

        // Start the simulation
        thriftStore.startSimulation();

        // Set up a shutdown hook to ensure resources are cleaned up properly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            thriftStore.stopSimulation();
            System.out.println("Simulation stopped.");
        }));
    }
}

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
    public static void main(String[] args) {
        // Initialize sections
        Map<String, Section> sections = new HashMap<>();
        String[] categories = {"electronics", "clothing", "furniture", "toys", "sporting goods", "books"};
        for (String category : categories) {
            sections.put(category, new Section(category));
        }

        // Initialize the delivery box
        DeliveryBox deliveryBox = new DeliveryBox();

        // Define the number of assistants and customers
        int assistantCount = 2;
        int customerCount = 5;

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

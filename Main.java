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
        // Create sections
        Map<String, Section> sections = new HashMap<>();
        sections.put("electronics", new Section("electronics"));
        sections.put("clothing", new Section("clothing"));
        sections.put("furniture", new Section("furniture"));
        sections.put("toys", new Section("toys"));
        sections.put("sporting goods", new Section("sporting goods"));
        sections.put("books", new Section("books"));

        // Create delivery box
        DeliveryBox deliveryBox = new DeliveryBox();

        // Create thrift store simulation instance (not shown in previous steps but assumed to exist)
        ThriftStore thriftStore = new ThriftStore();

        // Start ticking mechanism of the thrift store
        thriftStore.startSimulation();

        // Create and start threads for assistants
        ExecutorService assistantService = Executors.newFixedThreadPool(2); // Assuming 2 assistants for simplicity
        for (int i = 0; i < 2; i++) {
            assistantService.submit(new Assistant(i + 1, deliveryBox, sections));
        }

        // Create and start threads for customers
        ExecutorService customerService = Executors.newFixedThreadPool(5); // Assuming 5 customers for simplicity
        for (int i = 0; i < 5; i++) {
            customerService.submit(new Customer(i + 1, sections));
        }

        // Add shutdown hook to stop the executor services on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            assistantService.shutdownNow();
            customerService.shutdownNow();
        }));
    }
}

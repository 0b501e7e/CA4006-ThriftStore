import src.main.thriftstore.ThriftStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner; // Import the Scanner class
import java.util.InputMismatchException;
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

        Scanner scanner = new Scanner(System.in); // Create a Scanner object for collecting parameters

        System.out.println("-------------------------------------------------");
        System.out.println("Welcome to Senan & Zak's Thrift Store Simulation!");
        System.out.println("-------------------------------------------------");

        int assistantCount = 0;
        int customerCount = 0;

        // Prompt for the number of assistants with error handling
        while (true) {
            try {
                System.out.print("Enter the number of assistants: ");
                assistantCount = scanner.nextInt(); // Read user input for assistants
                if (assistantCount < 0) throw new InputMismatchException("Number must be positive.");
                break; // Break the loop if input is valid
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid positive integer.");
                scanner.next(); // Consume the invalid input
            }
        }

        // Prompt for the number of customers with error handling
        while (true) {
            try {
                System.out.print("Enter the number of customers: ");
                customerCount = scanner.nextInt(); // Read user input for customers
                if (customerCount < 0) throw new InputMismatchException("Number must be positive.");
                break; // Break the loop if input is valid
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid positive integer.");
                scanner.next(); // Consume the invalid input
            }
        }

        scanner.close(); // Close the scanner

        System.out.println("-------------------------------------------------");
        System.out.println("Thriftstore Opening");
        System.out.println("-------------------------------------------------");

        // Create the ThriftStore instance with initialized components
        ThriftStore thriftStore = new ThriftStore(sections, deliveryBox, assistantCount, customerCount);

        // Start the simulation
        thriftStore.startSimulation();

        // Set up a shutdown hook to ensure resources are cleaned up properly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            thriftStore.stopSimulation();
            System.out.println("-------------------------------------------------");
            System.out.println("Thriftstore has closed");
            System.out.println("-------------------------------------------------");
        }));
    }
}

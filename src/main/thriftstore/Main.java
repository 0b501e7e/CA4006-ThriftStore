package src.main.thriftstore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Set;
import src.main.thriftstore.model.Section;
import src.main.thriftstore.model.DeliveryBox;
import src.main.thriftstore.concurrent.Assistant;
import src.main.thriftstore.concurrent.Customer;
public class Main {
    public static void main(String[] args) throws InterruptedException {

        // Initialize the delivery box
        DeliveryBox deliveryBox = new DeliveryBox();

        Scanner scanner = new Scanner(System.in); // Create a Scanner object for collecting parameters

        System.out.println("-------------------------------------------------");
        System.out.println("Welcome to Senan & Zak's Thrift Store Simulation!");
        System.out.println("-------------------------------------------------");

        // List available categories
        String[] categories = {"electronics", "clothing", "furniture", "toys", "sporting goods", "books"};
        System.out.println("Available categories are: ");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        // Ask how many categories they want to include
        int numCategories;
        while (true) {
            try {
                System.out.print("How many categories would you like to include? ");
                numCategories = scanner.nextInt();
                if (numCategories <= 0 || numCategories > categories.length) throw new InputMismatchException("Invalid number of categories.");
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number within the available range.");
                scanner.next(); // Consume the invalid input
            }
        }

        // Initialize sections based on user choice
        Map<String, Section> sections = new HashMap<>();
        Set<String> selectedCategories = new HashSet<>();
        if (numCategories == categories.length) {
            for (String category : categories) {
                selectedCategories.add(category);
                sections.put(category, new Section(category, 5));
                System.out.println("Category chosen: " + category);
            }
        } else {
            for (int i = 0; i < numCategories; i++) {
                System.out.print("Enter the number for category " + (i + 1) + ": ");
                int categoryChoice = scanner.nextInt() - 1;
                scanner.nextLine(); // consume newline
                if (categoryChoice >= 0 && categoryChoice < categories.length) {
                    String selectedCategory = categories[categoryChoice];
                    if (!selectedCategories.contains(selectedCategory)) {
                        selectedCategories.add(selectedCategory);
                        sections.put(selectedCategory, new Section(selectedCategory, 5));
                        System.out.println("Category chosen: " + selectedCategory);
                    } else {
                        System.out.println(selectedCategory + " has already been selected. Skipping.");
                    }
                } else {
                    System.out.println("Invalid selection. Please select a valid category number.");
                    i--; // prompt the user again for this selection
                }
            }
        }

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
        ThriftStore thriftStore = new ThriftStore(sections, deliveryBox, assistantCount, customerCount, selectedCategories);

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

package src.main.thriftstore;

import src.main.thriftstore.model.Item;
import src.main.thriftstore.model.Section;
import src.main.thriftstore.model.DeliveryBox;
import src.main.thriftstore.concurrent.Assistant;
import src.main.thriftstore.concurrent.Customer;


import src.main.thriftstore.model.*;
import src.main.thriftstore.concurrent.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class ThriftStore {
    private final Map<String, Section> sections = new HashMap<>();
    private final DeliveryBox deliveryBox = new DeliveryBox();
    private final ExecutorService assistantPool;
    private final ExecutorService customerPool;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicInteger tickCount = new AtomicInteger();

    private Random random = new Random();

    public ThriftStore() {
        // Initialize sections
        String[] categories = {"electronics", "clothing", "furniture", "toys", "sporting goods", "books"};
        for (String category : categories) {
            sections.put(category, new Section(category));
        }

        // Initialize thread pools for assistants and customers
        assistantPool = Executors.newFixedThreadPool(2); // Example: 2 assistants
        customerPool = Executors.newFixedThreadPool(5); // Example: 5 customers
    }

    public void startSimulation() {
        // Schedule tick incrementer
        scheduler.scheduleAtFixedRate(this::onTick, 0, 1, TimeUnit.SECONDS);

        // Start assistant threads
        for (int i = 0; i < 2; i++) {
            assistantPool.submit(new Assistant(i, deliveryBox, sections));
        }

        // Start customer threads
        for (int i = 0; i < 5; i++) {
            customerPool.submit(new Customer(i, sections));
        }
    }

    private void onTick() {
        int currentTick = tickCount.incrementAndGet();
        System.out.println("Tick: " + currentTick);

        if( random.nextDouble() < 0.01 ) {
            simulateDelivery();
        }
    }

    private void simulateDelivery() {
        // Randomly create and distribute items across categories
        // Example: Randomly create 10 items and add them to the delivery box
        List<Item> items = new ArrayList<>();
        Random random = new Random();
        String[] categories = {"electronics", "clothing", "furniture", "toys", "sporting goods", "books"};
        for (int i = 0; i < 10; i++) { // Simulate 10 items per delivery
            String category = categories[random.nextInt(categories.length)];
            items.add(new Item(category));
        }
        deliveryBox.addItems(items);
        System.out.println("Delivered items to the box.");
    }

    public void stopSimulation() {
        scheduler.shutdown();
        assistantPool.shutdown();
        customerPool.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
            if (!assistantPool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                assistantPool.shutdownNow();
            }
            if (!customerPool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                customerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            assistantPool.shutdownNow();
            customerPool.shutdownNow();
        }
    }

    public static void main(String[] args) {
        ThriftStore store = new ThriftStore();
        store.startSimulation();

        // Example: Stop simulation after some time or based on some condition
        // This could be replaced with a more sophisticated control mechanism
        try {
            Thread.sleep(10 * 1000); // Run for 10 seconds for demonstration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        store.stopSimulation();
    }
}

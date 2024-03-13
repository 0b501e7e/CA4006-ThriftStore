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
    private final Map<String, Section> sections;
    private final DeliveryBox deliveryBox;
    private final ExecutorService assistantPool;
    private final ExecutorService customerPool;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicInteger tickCount = new AtomicInteger();
    private final Random random = new Random();
    public static long TICK_TIME_SIZE = 100; // Example: Define the tick time size, e.g., 100 milliseconds per tick

    // Constructor accepting initialized components
    public ThriftStore(Map<String, Section> sections, DeliveryBox deliveryBox, int assistantCount, int customerCount) {
        this.sections = sections;
        this.deliveryBox = deliveryBox;
        this.assistantPool = Executors.newFixedThreadPool(assistantCount);
        this.customerPool = Executors.newFixedThreadPool(customerCount);
    }

    public void startSimulation() {
        // Schedule tick incrementer
        scheduler.scheduleAtFixedRate(this::onTick, 0, 100, TimeUnit.MILLISECONDS);

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

        // Attempt to simulate a delivery with a probability of 0.01 every tick.
        if( random.nextDouble() < 0.01 ) {
            simulateDelivery();
        }
    }

    private void simulateDelivery() {
        // Simulates creating and distributing 10 items across categories randomly.
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String category = getRandomCategory();
            items.add(new Item(category));
        }
        deliveryBox.addItems(items);
        System.out.println("Delivered items to the box.");
    }

    private String getRandomCategory() {
        String[] categories = {"electronics", "clothing", "furniture", "toys", "sporting goods", "books"};
        return categories[random.nextInt(categories.length)];
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
}

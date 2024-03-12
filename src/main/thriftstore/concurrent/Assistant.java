package src.main.thriftstore.concurrent;

// Imports from the model package for item handling and the concurrent package for managing shared resources
import src.main.thriftstore.model.Item;
import src.main.thriftstore.model.Section;
import src.main.thriftstore.model.DeliveryBox;

import java.util.*;

// The Assistant class implements Runnable for concurrent execution in a thread
public class Assistant implements Runnable {
    // DeliveryBox to take items from for stocking
    private final DeliveryBox deliveryBox;
    // Sections mapped by their names for efficient lookup and stocking
    private final Map<String, Section> sections;
    // Unique identifier for each assistant for tracking and logging purposes
    private final int id;

    private enum State {
        MOVING_TO_SECTION,
        STOCKING,
        RETURNING_TO_DELIVERY_AREA,
        INIT
    }
    private State currentState = State.INIT;

    private List<Item> carriedItems = new ArrayList<>();
    private Section currentSection;



    // Constructor initializes the assistant with an ID, delivery box, and sections map
    public Assistant(int id, DeliveryBox deliveryBox, Map<String, Section> sections) {
        this.id = id;
        this.deliveryBox = deliveryBox;
        this.sections = sections;
    }

    // The core logic of stocking items executed when the assistant thread starts
    // Needs to be aware of which items it has, so it can determine whether to move to another section
    // will need to check how many items to stock in a section, then move to the next one if there is after
    // An assistant takes 10 ticks to move from the delivery area to any section and vice versa.
    // Additionally, moving between sections also takes 10 ticks, plus 1 tick for every item they are currently carrying.
    // Stocking each item in a section takes 1 tick.
    // An assistant can carry up to 10 items at once, and the time it takes to move depends on the number of items they're carrying.
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                switch (currentState) {
                    case MOVING_TO_SECTION -> {
                        // Calculate and simulate movement time
                        Thread.sleep(calculateMovementTime());
                        currentState = State.STOCKING;
                    }
                    case STOCKING -> {
                        stockItems();
                        decideNextAction();
                    }
                    case RETURNING_TO_DELIVERY_AREA -> {
                        Thread.sleep(calculateMovementTime());
                        pickUpItems();
                    }
                    case INIT -> {
                        pickUpItems();
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Assistant " + id + " was interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    private long calculateMovementTime() {
        // 10 ticks for movement, plus 1 for each item carried
        return (10 + carriedItems.size()) * 100L; // Assuming 1 tick = 100 milliseconds
    }

    // might need to check the categories here, as we could have different items
    private void stockItems() throws InterruptedException {
        synchronized (currentSection) { // Ensure exclusive access to the section
            Iterator<Item> iterator = carriedItems.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (Objects.equals(item.getCategory(), currentSection.getName())) {
                    currentSection.addItem(item);
                    Thread.sleep(calculateTickTime()); // Simulating stocking time with variability
                    iterator.remove(); // Remove after stocking
                    System.out.println("[" + System.currentTimeMillis() + "] Assistant " + id + " stocked " + item.getCategory() + " in " + currentSection.getName());
                }
            }
        }
    }


    private void decideNextAction() {
        // Implement logic to decide whether to move to another section or return to delivery area
        if (!carriedItems.isEmpty()) {
            String nextCategory = carriedItems.get(0).getCategory();
            currentSection = sections.get(nextCategory);
            currentState = State.MOVING_TO_SECTION;
        }
        else {
            currentState = State.RETURNING_TO_DELIVERY_AREA;
        }
    }

    private void pickUpItems() {
        try {
            carriedItems = deliveryBox.takeItems(10);
            if (!carriedItems.isEmpty()) {
                String nextCategory = carriedItems.get(0).getCategory();
                currentSection = sections.get(nextCategory);
                currentState = State.MOVING_TO_SECTION;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

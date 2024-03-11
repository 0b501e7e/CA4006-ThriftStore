package main.thriftstore.model;

public class Item {
    private final String category; // Stores the category to which this item belongs

    // Constructor: Initializes a new Item instance with a specified category
    public Item(String category) {
        this.category = category; // Set the category of this item
    }

    // Getter for the category of this item
    public String getCategory() {
        return category; // Return the category
    }
}

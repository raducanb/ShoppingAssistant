package com.example.raducanbogdan.shoppingassistant;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class ShoppingItem implements Serializable {
    public final String id;
    public final String name;
    public final Category category;

    public ShoppingItem(String name, Category category) {
        this.name = name;
        this.category = category;
        this.id = UUID.randomUUID().toString();
    }
}

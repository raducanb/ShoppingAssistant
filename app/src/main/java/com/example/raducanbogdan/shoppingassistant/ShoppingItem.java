package com.example.raducanbogdan.shoppingassistant;

import java.io.Serializable;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class ShoppingItem implements Serializable {
    public final String name;
    public final Category category;

    public ShoppingItem(String name, Category category) {
        this.name = name;
        this.category = category;
    }
}

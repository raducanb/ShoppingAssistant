package com.example.raducanbogdan.shoppingassistant;

import java.io.Serializable;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class Category implements Serializable {
    public final String name;
    public final String id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }
}

package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by raducanbogdan on 1/16/17.
 */

public class Shop implements Serializable {
    public final String name;
    public final ArrayList<Category> categories;
    public final HashMap<String, Double> coordinates;

    public Shop(String name, ArrayList<Category> categories, HashMap<String, Double> coordinates) {
        this.name = name;
        this.categories = categories;
        this.coordinates = coordinates;
    }

    public String categoriesNamesStiched() {
        StringBuilder categoriesNames = new StringBuilder();
        for (Category category : this.categories) {
            categoriesNames.append(category.name);
        }
        return categoriesNames.toString();
    }
}

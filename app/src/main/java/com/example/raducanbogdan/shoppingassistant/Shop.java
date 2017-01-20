package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by raducanbogdan on 1/16/17.
 */

public class Shop implements Serializable {
    public final String id;
    public final String name;
    public final ArrayList<Category> categories;
    public final HashMap<String, Double> coordinates;

    public Shop(String id, String name, ArrayList<Category> categories, HashMap<String, Double> coordinates) {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.coordinates = coordinates;
    }

    public String categoriesNamesStiched() {
        StringBuilder namesBuilder = new StringBuilder();
        for (int i = 0; i < this.categories.size(); i++) {
            Category category = this.categories.get(i);
            namesBuilder.append(category.name);
            if (i < this.categories.size() - 1) {
                namesBuilder.append(", ");
            }
        }
        return namesBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!Shop.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Shop other = (Shop)obj;

        return this.id.equals(other.id);
    }
}

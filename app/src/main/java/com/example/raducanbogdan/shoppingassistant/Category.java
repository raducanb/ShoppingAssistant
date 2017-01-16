package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

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

public class CategoriesList {
    private static ArrayList<Category> categories;

    public static ArrayList<Category> all(Context context) {
        if (categories) {
            return categories;
        }

        categories = JSONArrayLoader.<Category>load(context, "categories.json");
        return categories;
    }
}

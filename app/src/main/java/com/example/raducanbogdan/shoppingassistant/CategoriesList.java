package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class CategoriesList {
    private static ArrayList<Category> categories;
    private static final String fileName = "categories.json";

    public static ArrayList<Category> all(Context context) {
        if (categories != null) {
            return categories;
        }

        categories = new JSONArrayLoader<Category>().load(context, fileName);
        return categories;
    }
}
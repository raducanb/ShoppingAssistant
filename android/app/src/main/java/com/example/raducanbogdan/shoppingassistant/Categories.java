package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class Categories {
    private static ArrayList<Category> savedCategories;
    private ArrayList<Category> categories;
    private static final String fileName = "categories.json";

    public static ArrayList<Category> all(Context context) {
        if (savedCategories != null) {
            return savedCategories;
        }

        Categories list = (Categories)JSONLoader.load(context, fileName, Categories.class);
        savedCategories = list.categories();
        return savedCategories;
    }

    public ArrayList<Category>categories() {
        return this.categories;
    }
}
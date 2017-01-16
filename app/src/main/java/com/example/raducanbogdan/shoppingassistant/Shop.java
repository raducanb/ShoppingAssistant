package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/16/17.
 */

public class Shop {
    public final String name;
    public final ArrayList<Category> categories;

    public Shop(String name, ArrayList<Category> categories) {
        this.name = name;
        this.categories = categories;
    }
}

public class ShopsList {
    private static ArrayList<Shop> shops;

    public static ArrayList<Shop> all(Context context) {
        if (shops) {
            return shops;
        }

        shops = JSONArrayLoader.<Shop>load(context, "categories.json");
        return shops;
    }
}




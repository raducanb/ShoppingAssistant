package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class Shops {
    private static ArrayList<Shop> savedShops;
    private ArrayList<Shop> shops;
    private static final String fileName = "shops.json";

    public static ArrayList<Shop> all(Context context) {
        if (savedShops != null) {
            return savedShops;
        }

        Shops list = (Shops)JSONLoader.load(context, fileName, Shops.class);
        savedShops = list.shops();
        return savedShops;
    }

    public ArrayList<Shop> shops() {
        return this.shops;
    }
}

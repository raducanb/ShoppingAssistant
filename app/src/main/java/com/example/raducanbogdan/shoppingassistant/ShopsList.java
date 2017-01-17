package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class ShopsList {
    private static ArrayList<Shop> shops;
    private static final String fileName = "shops.json";

    public static ArrayList<Shop> all(Context context) {
        if (shops != null) {
            return shops;
        }

        shops = new JSONArrayLoader<Shop>().load(context, fileName);
        return shops;
    }
}

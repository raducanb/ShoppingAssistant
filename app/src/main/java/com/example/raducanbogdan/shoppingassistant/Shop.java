package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.io.Serializable;
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

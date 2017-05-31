package com.example.raducanbogdan.shoppingassistant;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raducanb on 29/05/2017.
 */

public class ShoppingLists {
    @SerializedName("lists")
    public List<ShoppingList> lists;

    public ShoppingLists() {
        lists = new ArrayList<ShoppingList>();
    }
}

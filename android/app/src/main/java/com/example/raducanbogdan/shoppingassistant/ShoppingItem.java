package com.example.raducanbogdan.shoppingassistant;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class ShoppingItem implements Serializable {
    @SerializedName("ID")
    public Number id;
    @SerializedName("NAME")
    public String name;
    @SerializedName("CATEGORY_ID")
    public Number categoryId;
    @SerializedName("CATEGORY_NAME")
    public String categoryName;

    public Category category() {
        return new Category(categoryName, categoryId.toString());
    }

    public ShoppingItem() {

    }

    public ShoppingItem(String name, Category category) {
        this.name = name;
        this.categoryId = Integer.parseInt(category.id);
        this.categoryName = category.name;
//        this.name = name;
//        this.category = category;
//        this.id = UUID.randomUUID().toS;
    }
}

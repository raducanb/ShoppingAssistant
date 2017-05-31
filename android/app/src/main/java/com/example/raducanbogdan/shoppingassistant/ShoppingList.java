package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class ShoppingList implements Serializable {
    static String fileName = "shoppinglist.ser";
    public ArrayList<ShoppingItem> items;
    private Context context;

    @SerializedName("ID")
    public Number id;
    @SerializedName("NAME")
    public String name;
    @SerializedName("USER_ID")
    public Number userId;

    @Override
    public String toString() {
        return name;
    }

    private static ShoppingList instance = null;
    public ShoppingList() {

    }

    public void addItem(ShoppingItem item) {
        this.items.add(item);
        this.saveItems(this.context);
    }

    public void removeItem(ShoppingItem item) {
        this.items.remove(item);
        this.saveItems(this.context);
    }

    public ArrayList<ShoppingItem> items() {
        return this.items;
    }

    public ArrayList<Category> categories() {
        HashSet<Category> categories = new HashSet<>();
        for (ShoppingItem item : this.items) {
            categories.add(item.category());
        }
        return new ArrayList<Category>(categories);
    }

    private void saveItems(Context context) {
//        try {
//            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            ObjectOutputStream os = new ObjectOutputStream(fos);
//            os.writeObject(this.items);
//            os.close();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private ArrayList<ShoppingItem> getSavedItems(Context context) {
//        try {
//            FileInputStream fis = context.openFileInput(fileName);
//            ObjectInputStream is = new ObjectInputStream(fis);
//            ArrayList<ShoppingItem> items = (ArrayList<ShoppingItem>)is.readObject();
//            is.close();
//            fis.close();
//            return items;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        return new ArrayList<ShoppingItem>();
    }
}

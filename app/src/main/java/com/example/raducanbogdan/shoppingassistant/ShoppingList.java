package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class ShoppingList {
    static String fileName = "shoppinglist.ser";
    private ArrayList<ShoppingItem> items;
    private Context context;

    private static ShoppingList instance = null;
    public ShoppingList(Context context) {
        this.context = context;
        this.items = this.getSavedItems(context);
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

    private void saveItems(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this.items);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<ShoppingItem> getSavedItems(Context context) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            ArrayList<ShoppingItem> items = (ArrayList<ShoppingItem>)is.readObject();
            is.close();
            fis.close();
            return items;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<ShoppingItem>();
    }
}

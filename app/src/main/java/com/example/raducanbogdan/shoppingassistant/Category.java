package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by raducanbogdan on 1/15/17.
 */

public class Category implements Serializable {
    public final String name;
    public final String id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }
}

public class CategoriesList {
    private static ArrayList<Category> categories;

    public static ArrayList<Category> all(Context context) {
        if (categories) {
            return categories;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>(){}.getType();

        ArrayList<Category> a = new ArrayList<>();
        try {
            a = new Gson().fromJson(loadJSONFromAsset(context), listType);
        } catch (Exception e) {
            System.out.println(e);
        }
        categories = a;
        return categories;
    }

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("categories.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            return null;
        }
        return json;
    }
}

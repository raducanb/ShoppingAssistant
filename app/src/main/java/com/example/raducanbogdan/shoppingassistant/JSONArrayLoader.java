package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/16/17.
 */

public class JSONArrayLoader<T> {
    public ArrayList<T> load(Context context, String fileName) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();

        ArrayList<T> a = new ArrayList<>();
        try {
            a = new Gson().fromJson(loadJSONFromAsset(context, fileName), listType);
        } catch (Exception e) {
            System.out.println(e);
        }

        return a;
    }

    static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
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

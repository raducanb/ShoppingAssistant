package com.example.raducanbogdan.shoppingassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingListActivity
        extends AppCompatActivity
        implements ShoppingListAdapterProtocol {
    private int userId;
    private ShoppingList shoppingList;
    private ShoppingListAdapter shoppingListAdapter;
    private GeofencingManager geofencingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userId = getIntent().getIntExtra("userId", 0);
        shoppingList = (ShoppingList)getIntent().getSerializableExtra("shoppingList");

        setTitle(shoppingList.name);

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        this.geofencingManager = new GeofencingManager();

        getShoppingListItems();
    }

    private void addShoppingItem(ShoppingItem item) {
        this.shoppingList.addItem(item);
        this.shoppingListAdapter.notifyDataSetChanged();
        addGeofenceForShoppingItem(item);
    }

    private void removeShoppingItem(ShoppingItem item) {
        this.shoppingList.removeItem(item);
        this.shoppingListAdapter.notifyDataSetChanged();

        AndroidNetworking.get("http://10.0.2.2:3000/deleteItem")
                .addQueryParameter("item_id", item.id.toString())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<List<ShoppingItem>>(){}.getType();
                        shoppingList.items = gson.fromJson(response.toString(), collectionType);
                        setupShoppingListView((ListView)findViewById(R.id.shopping_list_view));
                        setupAddShoppingItemFAB();
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("t", "onError: " + error);
                    }
                });

        ArrayList<Category> remainingCategories = this.shoppingList.categories();
        boolean didRemoveLastItemWithThisCategory = !remainingCategories.contains(item.category());
        if (!didRemoveLastItemWithThisCategory) { return; }

        removeGeofencesForNeededShopsAfterItemDeleted(item, remainingCategories);
    }

    private void removeGeofencesForNeededShopsAfterItemDeleted(ShoppingItem item,
                                                               ArrayList<Category> remainingCategories) {
        ArrayList<String> shopIdsToRemoveGeofence = new ArrayList<>();
        for (Shop shop : Shops.all(this)) {
            boolean shopDoesntHaveDeletedItemCategory = !shop.categories.contains(item.category());
            if (shopDoesntHaveDeletedItemCategory) { continue; }
            boolean shopHasOtherItemsCategories =
                    !Collections.disjoint(shop.categories, remainingCategories);
            if (shopHasOtherItemsCategories) { continue; }

            shopIdsToRemoveGeofence.add(shop.id);
        }

        if (shopIdsToRemoveGeofence.size() == 0) { return; }
        this.geofencingManager.removeGeofenceForShopIds(shopIdsToRemoveGeofence);
    }

    private void addGeofenceForShoppingItem(ShoppingItem item) {
        this.geofencingManager.addGeofencesForShopsThatHaveCategory(this, Shops.all(this), item.category());
    }

    private void setupAddShoppingItemFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ShoppingListActivity.this, AddShoppingItemActivity.class);
                myIntent.putExtra("list_id", shoppingList.id.toString());
                ShoppingListActivity.this.startActivityForResult(myIntent, 0);
            }
        });
    }

    private void getShoppingListItems() {
        AndroidNetworking.get("http://10.0.2.2:3000/shoppingListProducts")
                .addQueryParameter("shopping_list_id", shoppingList.id.toString())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<List<ShoppingItem>>(){}.getType();
                        shoppingList.items = gson.fromJson(response.toString(), collectionType);
                        setupShoppingListView((ListView)findViewById(R.id.shopping_list_view));
                        setupAddShoppingItemFAB();
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("t", "onError: " + error);
                    }
                });
    }

    private void setupShoppingListView(ListView shoppingListView) {
        this.shoppingListAdapter = new ShoppingListAdapter(this, shoppingList.items);
        this.shoppingListAdapter.delegate = this;
        shoppingListView.setAdapter(this.shoppingListAdapter);
    }

    private void showShopsMapActivity() {
        Intent myIntent = new Intent(ShoppingListActivity.this, ShopsMapActivity.class);
        ShoppingListActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) { return; }
        if (resultCode == Activity.RESULT_OK) {
            ShoppingItem item = (ShoppingItem)data.getSerializableExtra("item");
            addShoppingItem(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_see_all_shops:
                showShopsMapActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void didCheckItem(ShoppingItem item) {
        removeShoppingItem(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            this.geofencingManager.connectGoogleApi(this);
        }
    }
}

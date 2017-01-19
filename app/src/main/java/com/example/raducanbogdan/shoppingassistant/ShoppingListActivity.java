package com.example.raducanbogdan.shoppingassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class ShoppingListActivity
        extends AppCompatActivity
        implements ShoppingListAdapterProtocol {
    private ShoppingList shoppingList;
    private ShoppingListAdapter shoppingListAdapter;
    private GeofencingManager geofencingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        this.geofencingManager = new GeofencingManager();

        setupShoppingListView((ListView)findViewById(R.id.shopping_list_view));
        setupAddShoppingItemFAB();
    }

    private void addShoppingItem(ShoppingItem item) {
        this.shoppingList.addItem(item);
        this.shoppingListAdapter.notifyDataSetChanged();
        addGeofenceForShoppingItem(item);
    }

    private void removeShoppingItem(ShoppingItem item) {
        this.shoppingList.removeItem(item);
        this.shoppingListAdapter.notifyDataSetChanged();

        ArrayList<Category> remainingCategories = this.shoppingList.categories();
        boolean didRemoveLastItemWithThisCategory = !remainingCategories.contains(item.category);
        if (!didRemoveLastItemWithThisCategory) { return; }

        removeGeofencesForNeededShopsAfterItemDeleted(item, remainingCategories);
    }

    private void removeGeofencesForNeededShopsAfterItemDeleted(ShoppingItem item,
                                                               ArrayList<Category> remainingCategories) {
        ArrayList<String> shopIdsToRemoveGeofence = new ArrayList<>();
        for (Shop shop : Shops.all(this)) {
            boolean shopDoesntHaveDeletedItemCategory = !shop.categories.contains(item.category);
            if (shopDoesntHaveDeletedItemCategory) { continue; }
            boolean shopHasOtherItemsCategories =
                    !Collections.disjoint(shop.categories, remainingCategories);
            if (shopHasOtherItemsCategories) { continue; }

            shopIdsToRemoveGeofence.add(shop.id);
        }

        this.geofencingManager.removeGeofenceForShopIds(shopIdsToRemoveGeofence);
    }

    private void addGeofenceForShoppingItem(ShoppingItem item) {
        this.geofencingManager.addGeofencesForShopsThatHaveCategory(this, Shops.all(this), item.category);
    }

    private void setupAddShoppingItemFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ShoppingListActivity.this, AddShoppingItemActivity.class);
                ShoppingListActivity.this.startActivityForResult(myIntent, 0);
            }
        });
    }

    private void setupShoppingListView(ListView shoppingListView) {
        this.shoppingList = new ShoppingList(this);
        ArrayList shoppingItems = this.shoppingList.items();
        this.shoppingListAdapter = new ShoppingListAdapter(this, shoppingItems);
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

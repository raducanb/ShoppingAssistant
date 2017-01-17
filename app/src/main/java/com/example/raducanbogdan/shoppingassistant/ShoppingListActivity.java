package com.example.raducanbogdan.shoppingassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity implements ShoppingListAdapterProtocol {
    private ShoppingList shoppingList;
    private ShoppingListAdapter shoppingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupShoppingListView((ListView)findViewById(R.id.shopping_list_view));
        setupAddShoppingItemFAB();
    }

    private void saveShoppingItem(ShoppingItem item) {
        this.shoppingList.addItem(item);
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
            saveShoppingItem(item);
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
        this.shoppingList.removeItem(item);
        this.shoppingListAdapter.notifyDataSetChanged();
    }
}

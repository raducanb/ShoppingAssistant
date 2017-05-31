package com.example.raducanbogdan.shoppingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListsActivity extends AppCompatActivity {
    private int userId;
    private int currentPage = 1;
    private int preLast = 0;
    private ShoppingLists shoppingLists = new ShoppingLists();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        this.userId = getIntent().getIntExtra("userId", 0);

        getListsForPage(this.currentPage);

        ListView listView = (ListView) findViewById(R.id.shopping_lists_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(ShoppingListsActivity.this, ShoppingListActivity.class);
                myIntent.putExtra("userId", userId);
                myIntent.putExtra("shoppingList", shoppingLists.lists.get(i));
                ShoppingListsActivity.this.startActivity(myIntent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, final int firstVisibleItem,
                                 final int visibleItemCount, final int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if (lastItem == totalItemCount) {
                    if(preLast != lastItem) {
                        currentPage += 1;
                        getListsForPage(currentPage);
                        preLast = lastItem;
                    }
                }
            }
        });

        SearchView searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AndroidNetworking.get("http://10.0.2.2:3000/searchShoppingLists")
                        .addQueryParameter("user_id", Integer.toString(userId))
                        .addQueryParameter("search_text", query)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                shoppingLists = gson.fromJson(response.toString(), ShoppingLists.class);
                                ListView listView = (ListView) findViewById(R.id.shopping_lists_view);
                                ArrayAdapter<ShoppingList> adapter = new ArrayAdapter<ShoppingList>(getApplicationContext(), android.R.layout.simple_list_item_1, shoppingLists.lists);
                                listView.setAdapter(adapter);
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    currentPage = 1;
                    shoppingLists.lists = new ArrayList<ShoppingList>();
                    getListsForPage(currentPage);
                }
                return false;
            }
        });
    }

    private void getListsForPage(int page) {
        AndroidNetworking.get("http://10.0.2.2:3000/shoppingLists")
                .addQueryParameter("user_id", Integer.toString(this.userId))
                .addQueryParameter("page", Integer.toString(page))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ShoppingLists sl = gson.fromJson(response.toString(), ShoppingLists.class);
                        shoppingLists.lists.addAll(sl.lists);
                        ListView listView = (ListView) findViewById(R.id.shopping_lists_view);
                        ArrayAdapter<ShoppingList> adapter = new ArrayAdapter<ShoppingList>(getApplicationContext(), android.R.layout.simple_list_item_1, shoppingLists.lists);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
}

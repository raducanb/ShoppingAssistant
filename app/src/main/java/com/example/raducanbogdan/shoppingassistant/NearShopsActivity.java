package com.example.raducanbogdan.shoppingassistant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NearShopsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_shops);

        Intent intent = getIntent();
        ArrayList<String> shopsIds = (ArrayList<String>)intent.getSerializableExtra("shops_ids");

        boolean isOnlyOneShop = shopsIds.size() == 1;
        if (isOnlyOneShop) {
            showDetailsForShop(shopsForIds(shopsIds).get(0));
            this.finish();
        } else {
            setupWithShops(shopsForIds(shopsIds));
        }
    }

    public static Intent makeNotificationIntent(Context context) {
        Intent intent = new Intent(context, NearShopsActivity.class);
        return intent;
    }

    private void setupWithShops(final ArrayList<Shop> shops) {
        ArrayList<String> titles = titlesWithNrOfProductsForShops(shops);
        ArrayAdapter<String> titlesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        ListView listView = (ListView)findViewById(R.id.shops_with_nr_of_products_list);
        listView.setAdapter(titlesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailsForShop(shops.get(position));
            }
        });
    }

    private void showDetailsForShop(Shop shop) {
        Intent i = new Intent(NearShopsActivity.this, ShopDetailsActivity.class);
        i.putExtra("shop", shop);
        startActivity(i);
    }

    private ArrayList<Shop> shopsForIds(ArrayList<String> ids) {
        ArrayList<Shop> shops = new ArrayList<>();
        for (Shop shop : Shops.all(this)) {
            if (!ids.contains(shop.id)) { continue; }
            shops.add(shop);
        }
        return shops;
    }

    private ArrayList<String> titlesWithNrOfProductsForShops(ArrayList<Shop> shops) {
        ArrayList<String> titles = new ArrayList<>();
        ShoppingList list = new ShoppingList(this);
        for (Shop shop : shops) {
            titles.add(titleWithNrOfProductsForShop(shop, list));
        }
        return titles;
    }

    private String titleWithNrOfProductsForShop(Shop shop, ShoppingList list) {
        StringBuilder title = new StringBuilder();
        title.append(shop.name);
        int nrOfProducts = nrOfProductsForShopAndShoppingList(shop, list);
        title.append(" - " + nrOfProducts);
        title.append(nrOfProducts == 1 ? "produs." : " produse.");
        return title.toString();
    }

    private int nrOfProductsForShopAndShoppingList(Shop shop, ShoppingList list) {
        int nrOfProducts = 0;
        for (ShoppingItem item : list.items()) {
            if (!shop.categories.contains(item.category)) { continue; }
            nrOfProducts++;
        }
        return nrOfProducts;
    }
}

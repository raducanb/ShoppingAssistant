package com.example.raducanbogdan.shoppingassistant;

import android.app.Activity;
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
    ArrayList<Shop> shops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_shops);

        setTitle("Magazine langa tine");

        Intent intent = getIntent();
        ArrayList<String> shopsIds = (ArrayList<String>)intent.getSerializableExtra("shops_ids");

        boolean isOnlyOneShop = shopsIds.size() == 1;
        if (isOnlyOneShop) {
            setupWithShop(shopsForIds(shopsIds).get(0));
        } else {
            this.shops = shopsForIds(shopsIds);
            setupWithShops(this.shops);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupWithShops(this.shops);
    }

    public static Intent makeNotificationIntent(Context context) {
        Intent intent = new Intent(context, NearShopsActivity.class);
        return intent;
    }

    private void setupWithShop(Shop shop) {
        showDetailsForShop(shop, true);
        this.finish();
    }

    private void setupWithShops(final ArrayList<Shop> shops) {
        ArrayList<String> titles = titlesWithNrOfProductsForShops(shops);
        ArrayAdapter<String> titlesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        ListView listView = (ListView)findViewById(R.id.shops_with_nr_of_products_list);
        listView.setAdapter(titlesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailsForShop(shops.get(position), false);
            }
        });
    }

    private void showDetailsForShop(Shop shop, boolean isOnlyShop) {
        Intent i = new Intent(NearShopsActivity.this, ShopDetailsActivity.class);
        i.putExtra("shop", shop);
        if (isOnlyShop) {
            startActivity(i);
        } else {
            startActivityForResult(i, 0);
        }
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
        title.append(nrOfProducts == 1 ? " produs." : " produse.");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) { return; }
        if (resultCode == Activity.RESULT_OK) {
            Shop removedShop = (Shop)data.getSerializableExtra("removedShop");
            if (removedShop == null) {
                setupWithShops(this.shops);
                return;
            }

            this.shops.remove(removedShop);
            if (this.shops.size() == 0) {
                showShoppingList();
                this.finish();
            } else {
                setupWithShops(this.shops);
            }
        }
    }

    private void showShoppingList() {
        Intent i = new Intent(NearShopsActivity.this, ShoppingListActivity.class);
        startActivity(i);
    }
}

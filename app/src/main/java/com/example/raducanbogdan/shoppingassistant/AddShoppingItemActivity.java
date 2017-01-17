package com.example.raducanbogdan.shoppingassistant;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;

public class AddShoppingItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_item);

        setupCategoriesSpinner();
        setupClickListeners();
    }

    private void setupCategoriesSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.categories_spinner);
        ArrayList<Category> categories = Categories.all(this);
        ArrayList<String> categoriesNames = new ArrayList<>();
        for (Category category : categories) {
            categoriesNames.add(category.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        Button saveButton = (Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                didSelectSave();
            }
        });
        Button cancelButton = (Button)findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                didSelectCancel();
            }
        });
    }

    private void didSelectSave() {
        ShoppingItem item = createShoppingItem();
        if (item == null) { return; }

        Intent intent = new Intent();
        intent.putExtra("item", (Serializable)item);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void didSelectCancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private ShoppingItem createShoppingItem() {
        EditText nameEditText =  (EditText)findViewById(R.id.editText_name);
        String nameText = nameEditText.getText().toString();
        if (nameText.length() == 0) {
            showNeedToFillNameError(nameEditText);
            return null;
        }
        Category category = selectedCategory();

        return new ShoppingItem(nameText, category);
    }

    private void showNeedToFillNameError(View view) {
        Snackbar.make(view, "You need to fill in the name", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private Category selectedCategory() {
        Spinner spinner = (Spinner)findViewById(R.id.categories_spinner);
        int selectedPos = spinner.getSelectedItemPosition();
        return Categories.all(this).get(selectedPos);
    }
}

package com.example.raducanbogdan.shoppingassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = ((EditText)findViewById(R.id.usernameEditText)).getText().toString();
                String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
                AndroidNetworking.post("http://10.0.2.2:3000/login")
                        .addBodyParameter("username", username)
                        .addBodyParameter("password", password)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Boolean isSuccess = null;
                                try {
                                    isSuccess = response.getBoolean("success");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (isSuccess) {
                                    try {
                                        showShoppingListsForUserId(response.getInt("user_id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("error_message"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }
        });
    }

    private void showShoppingListsForUserId(int userId) {
        Intent myIntent = new Intent(LoginActivity.this, ShoppingListsActivity.class);
        myIntent.putExtra("userId", userId);
        LoginActivity.this.startActivity(myIntent);
    }
}

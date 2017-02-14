package com.example.user.kchat01;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by user on 10/02/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText inputUsername, inputPassword;
    private Button btnGoRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputUsername = (TextInputEditText) findViewById(R.id.input_username);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);
        btnGoRegister = (Button) findViewById(R.id.btn_goRegister);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to RegisterActivity;
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login process
                String username = inputUsername.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                //empty field is not permitted
                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("All fields are required.")
                            .setNegativeButton("Back", null)
                            .create()
                            .show();
                } else {
                //in actual application, these variables are sent to the server
                    Log.i("username", username);
                    Log.i("password", password);
                }
            }
        });
    }
}

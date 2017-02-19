package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import IMPL.DatabaseAdaptor;
import IMPL.RESTApi;

/**
 * Created by user on 10/02/2017.
 */

public class LoginActivity extends CustomActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextInputEditText inputUsername, inputPassword;
    private Button btnGoRegister, btnLogin;
    BottomNavigationView bottomNavigationView;
    CustomActivity customActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DatabaseAdaptor myAdaptor = new DatabaseAdaptor(this);
        myAdaptor.checkTable();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        inputUsername = (TextInputEditText) findViewById(R.id.input_username);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);
        btnGoRegister = (Button) findViewById(R.id.btn_goRegister);

        // apply toolbar title
        toolbarTitle.setText(R.string.toolbar_title);
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        // apply the Register button to Georgia font
        btnGoRegister.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        btnLogin = (Button) findViewById(R.id.btn_login);
        // apply the Login button to Georgia font
        btnLogin.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        // When pushing "Enter key" after inputting password field, Login button works.
        inputPassword.setNextFocusDownId(R.id.btn_login);

        //Listener for Register button
        btnGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to RegisterActivity;
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        //Listener for Login button
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
                    Intent loginIntent = new Intent(LoginActivity.this, ContactsListActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        //bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //customActivity = new CustomActivity();
        //customActivity.startActivity();

        //Intent bottomIntent = new Intent(this, CustomActivity.class);
        //this.startActivity(bottomIntent);
    }

    public void OnLogin(View view) {
      //  String username = UsernameEt.getText().toString();
       // String password = PasswordEt.getText().toString();
        String type = "login";
         String login_url = "http://192.168.1.6/login.php";

        RESTApi backgroundasync = new RESTApi(LoginActivity.this,login_url);
      // backgroundasync.execute(type, username, password);
    }


}

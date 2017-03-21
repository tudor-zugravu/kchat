package com.example.user.kchat01;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import IMPL.Contacts;
import IMPL.RESTApi;

/**
 * Created by user on 10/02/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextInputEditText inputUsername, inputPassword;
    private Button btnGoRegister, btnLogin;
    BottomNavigationView bottomNavigationView;
    CustomActivity customActivity;
    private DataManager dm;

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dm = new DataManager(LoginActivity.this);
        dm.flushAllData();
        dm.flushAllMessageData();
        Contacts.activeChat.clear();
        Contacts.activeChat.clear();
        //before any work is done on creating the activity i will check to see if the user is still in session
        //if the user is in session then send him to the chat menu but first check with the server
        //if not then design the layout
         this.pref = getApplicationContext().getSharedPreferences("LoginPreference", 0); // 0 - for private mode;
         this.editor = pref.edit();
        if(pref.getAll()!=null) {
            String username = pref.getString("usernamelogin", null);
            String password = pref.getString("usernamepassword", null);
            if (username != null && password != null) {

                Intent loginIntent = new Intent(LoginActivity.this, ContactsActivity.class);
                startActivity(loginIntent);
                finish();
                //editor.clear();
                //editor.commit(); // commit changes
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        inputUsername = (TextInputEditText) findViewById(R.id.input_username);
        inputPassword = (TextInputEditText) findViewById(R.id.input_password);
        btnGoRegister = (Button) findViewById(R.id.btn_goRegister);

        inputUsername.setText("tudor");
        inputPassword.setText("tudor");
        // apply toolbar title
        toolbarTitle.setText("Login");
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
                 String user;
                 String pass;
                //login process
                user = inputUsername.getText().toString().trim();
                pass = inputPassword.getText().toString().trim();
                //empty field is not permitted
                if (user.isEmpty() || pass.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("All fields are required.")
                            .setNegativeButton("Back", null)
                            .create()
                            .show();
                } else {
                //in actual application, these variables are sent to the server
                    Log.i("username", user);
                    Log.i("password", pass);
                    onLogin(user,pass);

                }
            }
        });

    }

    public void onLogin(String usr, String pass) {
        if(InternetHandler.hasInternetConnection(LoginActivity.this)==false){

        }else {
            String type = "login";
            String login_url = "http://188.166.157.62:3000/login";
            ArrayList<String> paramList = new ArrayList<>();
            paramList.add("username");
            paramList.add("password");
            RESTApi backgroundasync = new RESTApi(LoginActivity.this, login_url, paramList);
            backgroundasync.execute(type, usr, pass);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pref.getAll()!=null) {
            this.editor.clear();
            this.editor.commit(); // commit changes
        }
    }
}

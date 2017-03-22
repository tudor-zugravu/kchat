package com.example.user.kchat01;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import IMPL.RESTApi;

import static com.example.user.kchat01.R.id.confirm;
import static com.example.user.kchat01.R.id.etConfirmPassword;
import static com.example.user.kchat01.R.id.newPassword;


/**
 * Created by user on 17/02/2017.
 */

public class CustomActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private View alertView;
    private EditText etCurrentPassword, etNewPassword, etConfirm, etDeleteAccConfirmPassword;
    private String newValue, strPassword, currentPassword;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,}$");
    private DataManager dm;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        dm = new DataManager(CustomActivity.this);
        MenuItem itemDeleteAccount = menu.findItem(R.id.deleteAccount);
        MenuItem itemPasswordChange = menu.findItem(R.id.changePassword);
        if (ProfileActivity.self==false) {
            itemDeleteAccount.setVisible(false);
            itemPasswordChange.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    // behaviour by pushing each menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAccount:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CustomActivity.this);
                builder1.setTitle("Delete Account Confirmation");
                builder1.setMessage("Do you want to delete your account: "+ProfileActivity.users_username+" ?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomActivity.this);
                                LayoutInflater inflater = LayoutInflater.from(CustomActivity.this);
                                alertView = inflater.inflate(R.layout.dialog_delete_account, null);
                                builder.setView(alertView);
                                builder.setTitle("Delete Account: "+ProfileActivity.users_username);
                                etCurrentPassword = (EditText) alertView.findViewById(R.id.etPassword);
                                etDeleteAccConfirmPassword = (EditText) alertView.findViewById(etConfirmPassword);
                                etDeleteAccConfirmPassword.addTextChangedListener(new MyTextWatcher(etDeleteAccConfirmPassword));
                                builder.setPositiveButton("Delete",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(CustomActivity.this, "Delete!\nPassword:"+currentPassword, Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });
                                builder.setNegativeButton("Cancel",null);
                                alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            }
                        });
                builder1.setNegativeButton("Cancel", null);
                AlertDialog alert11 = builder1.create();
                alert11.show();
                return true;
            case R.id.changePassword:
                newValue = null;
                //create alert dialog to change password
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomActivity.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                alertView = inflater.inflate(R.layout.dialog_change_password, null);
                builder.setView(alertView);
                builder.setTitle("Change Password");

                etCurrentPassword = (EditText) alertView.findViewById(R.id.currentPassword);
                etNewPassword = (EditText) alertView.findViewById(newPassword);
                etConfirm = (EditText) alertView.findViewById(confirm);

                // input check
                etCurrentPassword.addTextChangedListener(new MyTextWatcher(etCurrentPassword));
                etNewPassword.addTextChangedListener(new MyTextWatcher(etNewPassword));
                etConfirm.addTextChangedListener(new MyTextWatcher(etConfirm));

                builder.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if(InternetHandler.hasInternetConnection(CustomActivity.this,0)==false){
                                }else {
                                    String newPassword = etCurrentPassword.getText().toString();
                                   // String oldPassword = etOldPassword.getText().toString();

                                    String type = "profileUpdate";
                                    String login_url = "http://188.166.157.62:3000/changePass";
                                    ArrayList<String> paramList = new ArrayList<>();
                                    paramList.add("newPassword");
                                    paramList.add("username");
                                    paramList.add("password"); // to get the old password

                                    RESTApi backgroundasync = new RESTApi(CustomActivity.this, login_url, paramList);
                                    backgroundasync.execute(type, newPassword); // myusername and old password
                                }
                                //Send request for changing password to the server
                                // New password is stored in variable of "newValue"
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                LoginActivity.editor.clear();
                                LoginActivity.editor.commit(); // commit changes
                                startActivity(intent);

                               // Toast.makeText(getApplicationContext(),"Password was changed to "+newValue, Toast.LENGTH_SHORT).show();
                            }
                        });

                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                alertDialog = builder.create();
                alertDialog.show();
                //Initially the positive button is disable
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                return true;
            case R.id.logout:
                /*
                Logout Request to server
                 */
                Intent logoutIntent = new Intent(CustomActivity.this, LoginActivity.class);
                LoginActivity.editor.clear();
                LoginActivity.editor.commit(); // commit changes
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
                dm.flushAllData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
/*
Please set on this method to check current password validity
Compare the hash value of input strings with that of current actual password
*/
    private boolean currentPasswordCheck(){
        currentPassword = etCurrentPassword.getText().toString().trim();
//        if (!currentPassword.equals("aaaaa")){
//        etCurrentPassword.setError("Current Password is wrong");
//        return false;
//        } else {
        Log.d("DELETE_PASSCHK", currentPassword);
        etCurrentPassword.setError(null);
//        }
        return true;
    }

    private boolean newPasswordCheck() {
        newValue = etNewPassword.getText().toString().trim();
        if (newValue.isEmpty() || !PASSWORD_PATTERN.matcher(newValue).matches()) {
            etNewPassword.setError("Minimum 6 characters including alphabet, number and special character");
            return false;
        } else {
            etNewPassword.setError(null);
        }
        return true;
    }

    private boolean confirmCheck(){
        String Confirm = etConfirm.getText().toString().trim();
        if (!Confirm.equals(etNewPassword.getText().toString().trim())) {
            etConfirm.setError("New password doesn't match");
            return false;
        } else {
            etConfirm.setError(null);
        }
        return true;
    }

    private boolean deleteAccConfirmCheck(){
        String Confirm = etDeleteAccConfirmPassword.getText().toString().trim();
        if (!Confirm.equals(etCurrentPassword.getText().toString().trim())) {
            etDeleteAccConfirmPassword.setError("password doesn't match");
            return false;
        } else {
            etDeleteAccConfirmPassword.setError(null);
        }
        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
              //  case currentPassword:  //current pass check doesn't need realtime check
              //      currentPasswordCheck();
              //      break;
                case newPassword:
                    newPasswordCheck();
                    break;
                case confirm:
                    confirmCheck();
                    break;
                case etConfirmPassword:
                    deleteAccConfirmCheck();
                    break;
            }
            if (view.getId()==newPassword||view.getId()==R.id.confirm){
                enableButton("changePassword");
            }else if (view.getId()== etConfirmPassword){
                enableButton("deleteAccount");
            }
        }
    }


    private void enableButton(String type){
        if (type.equals("changePassword")){
            if (!currentPasswordCheck() || !newPasswordCheck() || !confirmCheck()) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        }else if(type.equals("deleteAccount")){
            if (!currentPasswordCheck() || !deleteAccConfirmCheck()){
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        }
    }
}
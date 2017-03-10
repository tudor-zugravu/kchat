package com.example.user.kchat01;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import static com.example.user.kchat01.R.id.currentPassword;
import static com.example.user.kchat01.R.id.newPassword;


/**
 * Created by user on 17/02/2017.
 */

public class CustomActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private View alertView;
    private EditText etCurrentPassword, etNewPassword, etConfirm;
    private String newValue;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,}$");

    /*
  Top menu
   */
    // deploy menu button

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // behaviour by pushing each menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                Toast.makeText(this, "Edit Profile", Toast.LENGTH_LONG).show();
                return true;
            case R.id.changePassword:
                newValue = null;
                //create alert dialog to change password
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomActivity.this);
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                alertView = inflater.inflate(R.layout.dialog_change_password, null);
                builder.setView(alertView);
                builder.setTitle("Change Password");

                etCurrentPassword = (EditText) alertView.findViewById(currentPassword);
                etNewPassword = (EditText) alertView.findViewById(newPassword);
                etConfirm = (EditText) alertView.findViewById(R.id.confirm);

                // input check
                etCurrentPassword.addTextChangedListener(new MyTextWatcher(etCurrentPassword));
                etNewPassword.addTextChangedListener(new MyTextWatcher(etNewPassword));
                etConfirm.addTextChangedListener(new MyTextWatcher(etConfirm));

                builder.setPositiveButton("Change",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Send request for changing password to the server
                                // New password is stored in variable of "newValue"
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                LoginActivity.editor.clear();
                                LoginActivity.editor.commit(); // commit changes
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"Password was changed to "+newValue, Toast.LENGTH_SHORT).show();
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
            case R.id.changeImage:
                Toast.makeText(this, "to change profile image", Toast.LENGTH_SHORT).show();
                Intent imageintent = new Intent(CustomActivity.this, ImageUpload.class);
                startActivity(imageintent);
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
        String currentPassword = etCurrentPassword.getText().toString().trim();
//        if (!currentPassword.equals("aaaaa")){
//        etCurrentPassword.setError("Current Password is wrong");
//        return false;
//        } else {
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
                case currentPassword:
                    currentPasswordCheck();
                    break;
                case newPassword:
                    newPasswordCheck();
                    break;
                case R.id.confirm:
                    confirmCheck();
                    break;
            }
            enableButton();
            }
        }

    private void enableButton(){
      if (!currentPasswordCheck() || !newPasswordCheck() || !confirmCheck()) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        } else {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }
    }
}


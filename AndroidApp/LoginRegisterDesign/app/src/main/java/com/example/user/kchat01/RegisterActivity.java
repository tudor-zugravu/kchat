package com.example.user.kchat01;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * Created by user on 09/02/2017.
 */

public class RegisterActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private TextInputEditText inputUsername, inputEmail, inputPhone, inputPassword, inputConfirm;
    private TextInputLayout inputLayoutUsername, inputLayoutEmail, inputLayoutPhone, inputLayoutPassword, inputLayoutConfirm;
    private Button btnRegister;

    // for java regular expression
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");
    // start with 020 or 07 and 11 digits
    private static final Pattern PHONE_PATTERN = Pattern.compile("^020[0-9]{8}$||^07[0-9]{9}$");
    //include at least one digit and one character excluding alphabets, and more than 6
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*[0-9])(?=.*[^a-zA-Z0-9])[^a-zA-Z]{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputLayoutUsername = (TextInputLayout)findViewById(R.id.input_layout_username);
        inputLayoutEmail= (TextInputLayout)findViewById(R.id.input_layout_email);
        inputLayoutPhone = (TextInputLayout)findViewById(R.id.input_layout_phone);
        inputLayoutPassword = (TextInputLayout)findViewById(R.id.input_layout_password);
        inputLayoutConfirm = (TextInputLayout)findViewById(R.id.input_layout_confirm);
        inputUsername = (TextInputEditText)findViewById(R.id.input_username);
        inputEmail = (TextInputEditText)findViewById(R.id.input_email);
        inputPhone = (TextInputEditText)findViewById(R.id.input_phone);
        inputPassword = (TextInputEditText)findViewById(R.id.input_password);
        inputConfirm = (TextInputEditText)findViewById(R.id.input_confirm);
        btnRegister = (Button)findViewById(R.id.btn_register);

        //for check during inputting characters in each field
        inputUsername.addTextChangedListener(new MyTextWatcher(inputUsername));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputConfirm.addTextChangedListener(new MyTextWatcher(inputConfirm));

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                validateCheck();
            }
        });
        btnRegister.setEnabled(false);
    }

    /* Form validation check*/
    private void validateCheck() {
        if (!validateUsername()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePhone()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validateConfirm()) {
            return;
        }

// Now, result is displayed by Toast and output each input in the Log.
// For actual communication, required to store each variable in parameters
        Toast.makeText(this, "Registered", Toast.LENGTH_LONG).show();
        Log.i("username", inputUsername.getText().toString());
        Log.i("email", inputEmail.getText().toString());
        Log.i("phone", inputPhone.getText().toString());
        Log.i("password", inputPassword.getText().toString());
    }

    //get the text in username edittext
    // if empty -> show error in layout field
    private boolean validateUsername(){
        if (inputUsername.getText().toString().trim().isEmpty()){
            inputLayoutUsername.setError(getString(R.string.err_msg_username));
            return false;
        } else {
            inputLayoutUsername.setErrorEnabled(false);
        }
        return true;
    }
    //get the text in email edittext
    // if empty or invalid email add. -> show error in layout field
    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }
        return true;
    }

    //get the text in phone number edittext
    // if empty or invalid phone number -> show error in layout field
    private boolean validatePhone() {
        String phone = inputPhone.getText().toString().trim();
        if (phone.isEmpty() || !PHONE_PATTERN.matcher(phone).matches()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    //get the text in password edittext
    // if empty or invalid password -> show error in layout field
    private boolean validatePassword(){
        String password = inputPassword.getText().toString().trim();
        if (password.isEmpty() || !PASSWORD_PATTERN.matcher(password).matches()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    //get the text in Confirm Password edittext
    // if empty or not match password -> show error in layout field
    private boolean validateConfirm() {
        if (!inputConfirm.getText().toString().trim().equals(inputPassword.getText().toString().trim())) {
            inputLayoutConfirm.setError(getString(R.string.err_msg_confirm));
            return false;
        } else {
            inputLayoutConfirm.setErrorEnabled(false);
        }
        return true;
    }

    // Button Update
    // when all fields are OK, Register button is enabled.
    private void enableButton(){
        if (!validateUsername() || !validateEmail() || !validatePhone() || !validatePassword() || !validateConfirm()) {
            btnRegister.setEnabled(false);
        } else {
            btnRegister.setEnabled(true);
        }
    }

    //watching the text in each field during inputs
    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view){
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        //after inputting text, each character is checked
        @Override
        public void afterTextChanged(Editable s){
            switch (view.getId()){
                case R.id.input_username:
                    validateUsername();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_phone:
                    validatePhone();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_confirm:
                    validateConfirm();
                    break;
            }
             enableButton();
        }
    }

}

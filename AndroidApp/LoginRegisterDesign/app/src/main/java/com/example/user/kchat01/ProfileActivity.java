package com.example.user.kchat01;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import java.util.regex.Pattern;

import IMPL.MasterUser;

import static android.view.View.GONE;
import static com.example.user.kchat01.R.id.editTextNewProfile;
import static com.example.user.kchat01.R.id.textViewCurrentProfile;

/**
 * Created by user on 15/02/2017.
 */
/* This class has following functions:
1 shows profile image, username, email, phone and text biography.
2 modify those contents by clicking each element or by using top menu button
 */

public class ProfileActivity extends CustomActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle, tvUsername, tvEmail, tvPhone, tvBio, textViewField;
    private ImageView imageProfile;
    private Button btnDelete;
    private AlertDialog alertDialog;
    private View alertView;
    private EditText editTextField;
    private String fieldName, contacts_username, contacts_contactname, contacts_userid, contacts_email, contacts_phonenumber; // his params
    public static String users_username;
    private String users_email, users_phonenumber, users_biography;// self params
    private String newValue=null; //store new username, email or phone
    public static boolean self=false; //Profile data is her/himself or not
    private int contacts_contactid, contacts_position;


    //check new Email and Phone
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^020[0-9]{8}$||^07[0-9]{9}$");

    @Override
    protected void onResume() {
        super.onResume();
            toolbarTitle.setText("Profile");
            toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
            MasterUser man = new MasterUser();
            if(man.getProfileLocation()!=null&&!man.getProfileLocation().equals("null")) {
              //  imageProfile.setImageBitmap(man.getUsersprofile());
                Log.d("MYTAG", "location is" + man.getUsersprofile());
            }else if(man.getProfileLocation().equals("null")){
                imageProfile.setImageResource(R.drawable.human);
            }else {
                imageProfile.setImageResource(R.drawable.human);
            }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(InternetHandler.hasInternetConnection(ProfileActivity.this,1)==false){
           // ContactsActivity.mSocket.disconnect();
        }else {
            //ContactsActivity.mSocket.connect();
            ContactsActivity.mSocket.on("fullname_changed", fullnameChanged);
            ContactsActivity.mSocket.on("username_changed", usernameChanged); //-----------refresh
            ContactsActivity.mSocket.on("email_changed", emailChanged);
            ContactsActivity.mSocket.on("phone_number_changed", phoneNumberChanged);////
            ContactsActivity.mSocket.on("about_changed", aboutChanged);
        }
        setContentView(R.layout.activity_profile);
        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        tvUsername = (TextView) findViewById(R.id.textViewUsername);
        tvEmail = (TextView) findViewById(R.id.textViewEmail);
        tvPhone = (TextView) findViewById(R.id.textViewPhone);
        tvBio = (TextView) findViewById(R.id.textViewBio);
        btnDelete = (Button)findViewById(R.id.btn_delete);

        //set username to textview
        tvUsername.setTextSize(18);
        //set email to txtview
        tvEmail.setTextSize(18);
        //set phone to txtview
        tvPhone.setTextSize(18);
        //set biography to txtview
        tvBio.setTextSize(18);

        if(bundle!=null) {
            String type =(String) bundle.get("type");
            if (type.equals("contactsprofile")){
                contacts_username = (String) bundle.get("contact_username");
                contacts_userid = (String)bundle.get("contact_userid");
                contacts_contactname = (String) bundle.get("contact_contactname");
                contacts_contactid = intent.getIntExtra("contact_contactid", 0);
                contacts_position = intent.getIntExtra("position", 0);
                contacts_email = (String) bundle.get("contact_email");
                contacts_phonenumber = (String) bundle.get("contact_phonenumber");
                String contacts_biography = (String) bundle.get("contact_biography");
                Bitmap contacts_bitmap =(Bitmap) bundle.get("contacts_bitmap");
                if(contacts_bitmap!=null) {
                    imageProfile.setImageBitmap(contacts_bitmap);
                }else {
                    imageProfile.setImageResource(R.drawable.human);
                }
                if (contacts_username!=null) {
                    toolbarTitle.setText(contacts_username + "'s Profile");
                }else{
                    toolbarTitle.setText("Profile");
                }
                toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
                tvUsername.setText(contacts_username);
                tvEmail.setText(contacts_email);
                tvPhone.setText(contacts_phonenumber);
                tvBio.setText(contacts_biography);
                self = false;
                //set listener on DELETE Button
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
                        builder1.setTitle("Delete Contact Confirmation");
                        builder1.setMessage("Do you delete "+contacts_contactname+" from your Contact list?");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.d("REMOVE_CONTACT ", contacts_username);
                                        Log.d("REMOVE_CONTACTNAME ", contacts_contactname);
                                        Log.d("REMOVE_USERID", contacts_userid);
                                        Log.d("REMOVE_MASTERID", String.valueOf(MasterUser.usersId));
                                        Log.d("REMOVE_CONTACT_POSI", String.valueOf(contacts_position));
                                        ContactsActivity.mSocket.emit("delete_contact", MasterUser.usersId,contacts_userid);
                                        dialog.cancel();
                                            Toast.makeText(ProfileActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                            btnDelete.setVisibility(GONE);
                                        onBackPressed();

                                    }
                                });
                        builder1.setNegativeButton("No", null);
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });

            }else if (type.equals("usersprofile")) {
                toolbarTitle.setText("My Profile");
                toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
                MasterUser man = new MasterUser();
                if(man.getProfileLocation()!=null&&!man.getProfileLocation().equals("null")) {
                    imageProfile.setImageBitmap(man.getUsersprofile());
                    Log.d("MYTAG", "location is" + man.getUsersprofile());
                }else if(man.getProfileLocation().equals("null")){
                    imageProfile.setImageResource(R.drawable.human);
                }else {
                    imageProfile.setImageResource(R.drawable.human);
                }
                btnDelete.setVisibility(GONE);
                users_username = (String) bundle.get("users_username");
                users_email = (String) bundle.get("users_email");
                users_phonenumber = (String) bundle.get("users_phonenumber");
                users_biography = (String) bundle.get("users_biography");
                // String j =(String) bundle.get("name");
                tvUsername.setText(users_username);
                tvEmail.setText(users_email);
                tvPhone.setText(users_phonenumber);
                tvBio.setText(users_biography);
                self = true;
            }
        }
        /*
        modify each field by long click
         */
        // Long Click event for ImageView
        imageProfile.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        fieldName = "Image";
                        if (!self){
                            createErrorDialog(fieldName);
                        } else if (self){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
                            builder1.setTitle("Change Image Confirmation");
                            builder1.setMessage("Do you want to change your profile image?");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent imageuploaderIntent = new Intent(ProfileActivity.this, ImageUpload.class);
                                            startActivity(imageuploaderIntent);
                                        }
                                    });
                            builder1.setNegativeButton("No", null);
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                        return true;
                    }
                }
        );

        // Long Click event for Username
        tvUsername.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        fieldName = "Username";
                        if (!self){
                            createErrorDialog(fieldName);
                        } else if (self){
                            createDialog(fieldName);
                        }
                        return true;
                    }
                }
        );

        // Long Click event for Email
        tvEmail.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        fieldName = "Email";
                        if (!self){
                            createErrorDialog(fieldName);
                        } else if (self){
                            createDialog(fieldName);
                        }
                        return true;
                    }
                }
        );

        // Long Click event for Phone
        tvPhone.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        fieldName = "Phone";
                        if (!self){
                            createErrorDialog(fieldName);
                        } else if (self){
                            createDialog(fieldName);
                        }
                        return true;
                    }
                }
        );

        // Long Click event for Biography
        tvBio.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        fieldName = "Biography";
                        if (!self){
                            createErrorDialog(fieldName);
                        } else if (self){
                            createDialog(fieldName);
                        }
                        return true;
                    }
                }
        );
    }

    private boolean createDialog(final String fieldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
        alertView = inflater.inflate(R.layout.dialog_change_profile, null);
        builder.setView(alertView);
        builder.setTitle("Change "+fieldName);//Username----Email---Phone----Biography
        textViewField = (TextView) alertView.findViewById(textViewCurrentProfile);
        editTextField = (EditText) alertView.findViewById(editTextNewProfile);
        // input check
        editTextField.addTextChangedListener(new MyTextWatcher(editTextField));
        builder.setPositiveButton("Change",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //To add request server for changing field.
                        // String fieldname has "Username", Email" or "Phone"
                        // new data is stored in "newValue"
                        // If server error, return false
                        if(InternetHandler.hasInternetConnection(ProfileActivity.this,0)==false){
                        }else {

                            switch (fieldName) {
                                case "Username":
                                    Log.d("CHANGE","Changed username");
                                    ContactsActivity.mSocket.emit("change_username",MasterUser.usersId,newValue);
                                    break;
                                case "Email":
                                    Log.d("CHANGE","Changed email");
                                    ContactsActivity.mSocket.emit("change_email",MasterUser.usersId,newValue);
                                    break;
                                case "Phone":
                                    Log.d("CHANGE","Changed phone");
                                    ContactsActivity.mSocket.emit("change_phone_number",MasterUser.usersId,newValue);
                                    break;
                                case "Biography":
                                    Log.d("CHANGE","Changed biography");
                                    ContactsActivity.mSocket.emit("change_about",MasterUser.usersId,newValue);
                                    Log.d("CHANGE","Changed biography value is " + newValue);
                                    break;
                            }

                        }
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newValue = null;
                        dialog.cancel();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
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
            inputCheck(fieldName);
            enableButton();
        }
    }

    private boolean inputCheck(String fieldName) {
        editTextField.setFilters(new InputFilter[] { new InputFilter.LengthFilter(300) }); // less than 300 characters
        newValue = editTextField.getText().toString().trim();
        if (fieldName.equals("Username")){
            if(newValue.isEmpty()) { // Duplicate check is needed on Server
                editTextField.setError(getString(R.string.err_msg_username));
                return false;
            } else {
                editTextField.setError(null);
            }
        } else if (fieldName.equals("Email")) {
            if (newValue.isEmpty() || !EMAIL_PATTERN.matcher(newValue).matches()) {
                editTextField.setError(getString(R.string.err_msg_email));
                return false;
            } else {
                editTextField.setError(null);
            }
        }else if(fieldName.equals("Phone")){
            if (newValue.isEmpty() || !PHONE_PATTERN.matcher(newValue).matches()) {
                editTextField.setError(getString(R.string.err_msg_phone));
                return false;
            } else {
                editTextField.setError(null);
            }
        }else if (fieldName.equals("Biography")) {
            if (newValue.isEmpty()) {
                editTextField.setError(getString(R.string.err_msg_bio));
                return false;
            } else {
                editTextField.setError(null);
            }
        }
        return true;
    }

    private void enableButton(){
        if (!inputCheck(fieldName)) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        } else {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    private void createErrorDialog(String fieldName){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Not Allowed");
        builder.setMessage("This is not your profile.\n You can't edit this "+fieldName+".");
        builder.setNegativeButton("Cancel", null);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private Emitter.Listener fullnameChanged = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    if((receivedMessages!=null&&receivedMessages.equals(""))||receivedMessages.equals("fail")){
                        Toast toast = Toast.makeText(ProfileActivity.this, "could not change Full Name server offline or value exists", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(ProfileActivity.this, "Full Name changed", Toast.LENGTH_LONG);

                    }
                }
            });
        }
    };

    private Emitter.Listener usernameChanged = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    if((receivedMessages!=null&&receivedMessages.equals(""))||receivedMessages.equals("fail")) {
                        Toast toast = Toast.makeText(ProfileActivity.this, "could not change username, server offline or value already exists", Toast.LENGTH_LONG);
                        toast.show();

                    }else if((receivedMessages!=null&&receivedMessages.equals(""))||receivedMessages.equals("duplicate")){
                            Toast toast = Toast.makeText(ProfileActivity.this, "could not change username, username already exists", Toast.LENGTH_LONG);
                            toast.show();
                    }else{
                        Toast toast = Toast.makeText(ProfileActivity.this, "Username changed", Toast.LENGTH_LONG);
                        toast.show();
                        MasterUser.setUsername(receivedMessages);
                        tvUsername.setText(receivedMessages);
                    }
                }
            });
        }
    };

    private Emitter.Listener emailChanged = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    if((receivedMessages!=null&&receivedMessages.equals(""))||receivedMessages.equals("fail")){
                        Toast toast = Toast.makeText(ProfileActivity.this, "could not change email, server offline or value already exists", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(ProfileActivity.this, "Email changed", Toast.LENGTH_LONG);
                        toast.show();
                        MasterUser.setEmail(receivedMessages);
                        tvEmail.setText(receivedMessages);
                    }
                }
            });
        }
    };

    private Emitter.Listener phoneNumberChanged = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    if((receivedMessages!=null&&receivedMessages.equals(""))||receivedMessages.equals("fail")){
                        Toast toast = Toast.makeText(ProfileActivity.this, "could not change phone number, server offline or value already exists", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(ProfileActivity.this, "Phone number changed", Toast.LENGTH_LONG);
                        toast.show();
                        MasterUser.setTelephonenumber(receivedMessages);
                        tvPhone.setText(receivedMessages);
                    }
                }
            });
        }
    };

    private Emitter.Listener aboutChanged = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ProfileActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String receivedMessages = (String) args [0];
                    Log.d("CHANGE","Reached here");

                    if((receivedMessages!=null&&receivedMessages.equals(""))||receivedMessages.equals("fail")){
                        Toast toast = Toast.makeText(ProfileActivity.this, "could not change biography, server offline or value already exists", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(ProfileActivity.this, "Biography changed", Toast.LENGTH_LONG);
                        toast.show();
                        MasterUser man = new MasterUser();
                        man.setBiography(receivedMessages);
                        tvBio.setText(receivedMessages);
                    }
                }
            });
        }
    };

}


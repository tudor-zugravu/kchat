package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by user on 15/02/2017.
 */
/* This class has following functions:
1 shows profile image, username, email, phone and text biography.
2 modify those contents by clicking each element or by using top menu button
 */

public class ProfileActivity extends CustomActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle, tvUsername, tvEmail, tvPhone, tvBio;
    private ImageView imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // apply toolbar title
        toolbarTitle.setText("Profile");
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

/*
        REST get profile image, username, email, phone and biography
*/

        //set profile image to imageview (temporarily, set the image in drawable forlder)
        imageProfile.setImageResource(R.drawable.human);

        //set username to textview
        tvUsername.setTextSize(18);
        //set email to txtview
        tvEmail.setTextSize(18);
        //set phone to txtview
        tvPhone.setTextSize(18);
        //set biography to txtview
        tvBio.setTextSize(18);

        if(bundle!=null) {
            String users_username =(String) bundle.get("users_username");
            String users_email =(String) bundle.get("users_email");
            String users_phonenumber =(String) bundle.get("users_phonenumber");
            String users_biography =(String) bundle.get("users_biography");
           // String j =(String) bundle.get("name");
            tvUsername.setText(users_username);
            tvEmail.setText(users_email);
            tvPhone.setText(users_phonenumber);
            tvBio.setText(users_biography);
        }
        /*
        modify each field by long click
         */
        // Long Click event for ImageView
        imageProfile.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(ProfileActivity.this, "profile image was clicked", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        // Long Click event for Username
        tvUsername.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(ProfileActivity.this, "username was clicked", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        // Long Click event for Email
        tvEmail.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(ProfileActivity.this, "email was clicked", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        // Long Click event for Phone
        tvPhone.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(ProfileActivity.this, "phone was clicked", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        // Long Click event for Phone
        tvBio.setOnLongClickListener(
                new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        Toast.makeText(ProfileActivity.this, "bio was clicked", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
        );

        /*
    From here, Bottom navigation settings
    Future work: implement as another class
    item0: Chats, item1: Groups, item2: Contacts, item3: Profile
    */
//recognise the bottom navi.
        /*
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(1).setChecked(false);
        bottomNavigationView.getMenu().getItem(2).setChecked(false);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.chats:
                                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Intent chatIntent = new Intent(getApplicationContext(), old_ChatActivity.class);
                                startActivity(chatIntent);
                                break;
                            case R.id.groups:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Toast.makeText(getApplicationContext(), "groups is clicked.", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.contacts:
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                                bottomNavigationView.getMenu().getItem(3).setChecked(false);
                                Intent contactsIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                                startActivity(contactsIntent);
                                break;
                            case profile:
                                //finish();
                                bottomNavigationView.getMenu().getItem(0).setChecked(false);
                                bottomNavigationView.getMenu().getItem(1).setChecked(false);
                                bottomNavigationView.getMenu().getItem(2).setChecked(false);
                                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                                Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(profileIntent);
                                break;
                        }
                        return true;
                    }

                });
*/
    }

}


package com.example.user.kchat01;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.user.kchat01.R.id.profile;

/**
 * Created by user on 15/02/2017.
 */
/* This class has following functions:
1 shows profile image, username, email and phone.
2 modify those contents to click each element or menu button
3 change password button
4 logout button
5 bottom navigation
 */

public class ProfileActivity extends CustomActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle, tvUsername, tvEmail, tvPhone;
    private ImageView imageProfile;
    private Button btnChangePassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        tvUsername = (TextView) findViewById(R.id.textViewUsername);
        tvEmail = (TextView) findViewById(R.id.textViewEmail);
        tvPhone = (TextView) findViewById(R.id.textViewPhone);

        // apply toolbar title
        toolbarTitle.setText(R.string.toolbar_title);
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        // apply the Change Password button to Georgia font
        btnChangePassword.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // apply the Change Password button to Georgia font
        btnLogout.setTypeface(Typeface.createFromAsset(getAssets(), "Georgia.ttf"));

        // when pushing ChangePassword button, intent to change password activity
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
/*
create change password activity, then change following Toast to Intent
 */
            public void onClick(View view) {
                // Intent to Change password Activity;
              //  Intent changePasswordIntent = new Intent(ProfileActivity.this, ChangePassword.class);
              //  ProfileActivity.this.startActivity(changePasswordIntent);
                Toast.makeText(ProfileActivity.this, "move to change password activity", Toast.LENGTH_LONG).show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
/*
send logout request to server
*/
                Intent logoutIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                Toast.makeText(ProfileActivity.this, "send logout request to server", Toast.LENGTH_LONG).show();
            }

        });

/*
        REST get profile image, username, email and phone

*/

        //set profile image to imageview (temporarily, set the image in drawable forlder)
        imageProfile.setImageResource(R.drawable.human);

        //set username to textview
        tvUsername.setText("hardcopy_username");

        //set email to txtview
        tvEmail.setText("hardcopy_email");

        //set phone to txtview
        tvPhone.setText("hardcopy_02012345678");


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

        /*
    From here, Bottom navigation settings
    Future work: implement as another class
    item0: Chats, item1: Groups, item2: Contacts, item3: Profile
    */
//recognise the bottom navi.
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
                                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
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
                                Intent contactsIntent = new Intent(getApplicationContext(), ContactsListActivity.class);
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

    }

}


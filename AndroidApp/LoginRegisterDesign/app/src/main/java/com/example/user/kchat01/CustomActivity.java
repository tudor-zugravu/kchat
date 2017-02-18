package com.example.user.kchat01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by user on 17/02/2017.
 */

public class CustomActivity extends AppCompatActivity {

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
            case R.id.setting01:
                Toast.makeText(this, "move to setting01", Toast.LENGTH_LONG).show();
                return true;
            case R.id.profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                this.startActivity(profileIntent);
                return true;
            case R.id.language:
                Toast.makeText(this, "setting language", Toast.LENGTH_LONG).show();
                return true;
            case R.id.logout:
                Toast.makeText(this, "go to logout", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

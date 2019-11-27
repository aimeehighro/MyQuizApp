package com.example.myquizapplication;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

public class SettingsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

//        if (getSupportActionBar() != null) {
//            if(savedInstanceState != null) {
//                return;
//            }
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.settings_activity, new SettingsFragment())
//                    .commit();
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}

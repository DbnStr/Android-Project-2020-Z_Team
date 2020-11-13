package ru.mail.z_team;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MenuActivity", "onCreate: ");
        setContentView(R.layout.activity_main_menu);
        if (getSupportFragmentManager().findFragmentById(R.id.main_fragment) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fragment, new NewsFragment(), "NEWS FRAGMENT")
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}

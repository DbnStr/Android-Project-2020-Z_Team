package ru.mail.z_team;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MenuActivity", "onCreate: ");
        setContentView(R.layout.activity_main_menu);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final int container = R.id.current_menu_container;
        final TextView news = findViewById(R.id.news_feed_icon);
        final TextView walks = findViewById(R.id.walk_icon);
        final TextView friends = findViewById(R.id.friends_icon);
        final TextView profile = findViewById(R.id.profile_icon);

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsFragment fragment = (NewsFragment) fragmentManager.findFragmentByTag("NEWS FRAGMENT");
                if (fragment != null) {
                    if (fragmentManager.findFragmentById(container) != fragment) {
                        fragmentManager
                                .beginTransaction()
                                .replace(container, fragment, "NEWS FRAGMENT")
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    }
                }
                else {
                    fragmentManager
                            .beginTransaction()
                            .replace(container, new NewsFragment(), "NEWS FRAGMENT")
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });

        walks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalksFragment fragment = (WalksFragment) fragmentManager.findFragmentByTag("WALKS FRAGMENT");
                if (fragment != null) {
                    if (fragmentManager.findFragmentById(container) != fragment) {
                        fragmentManager
                                .beginTransaction()
                                .replace(container, fragment, "WALKS FRAGMENT")
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    }
                }
                else {
                    fragmentManager
                            .beginTransaction()
                            .replace(container, new WalksFragment(), "WALKS FRAGMENT")
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });

        if (getSupportFragmentManager().findFragmentById(container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(container, new NewsFragment(), "NEWS FRAGMENT")
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}

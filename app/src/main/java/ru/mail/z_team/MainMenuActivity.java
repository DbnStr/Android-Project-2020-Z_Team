package ru.mail.z_team;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mail.z_team.icon_fragments.friends.FriendsFragment;
import ru.mail.z_team.icon_fragments.GoOutFragment;
import ru.mail.z_team.icon_fragments.NewsFragment;
import ru.mail.z_team.icon_fragments.profile.ProfileFragment;
import ru.mail.z_team.icon_fragments.WalksFragment;

public class MainMenuActivity extends AppCompatActivity {

    static private final String NEWS_TAG = "NEWS FRAGMENT";
    static private final String WALKS_TAG = "WALKS FRAGMENT";
    static private final String FRIENDS_TAG = "FRIENDS FRAGMENT";
    static private final String PROFILE_TAG = "PROFILE FRAGMENT";
    static private final String GO_OUT_TAG = "GO_OUT FRAGMENT";

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MenuActivity", "onCreate: ");
        setContentView(R.layout.activity_main_menu);

        firebaseAuth = FirebaseAuth.getInstance();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final int container = R.id.current_menu_container;
        final TextView news = findViewById(R.id.news_feed_icon);
        final TextView walks = findViewById(R.id.walks_icon);
        final TextView friends = findViewById(R.id.friends_icon);
        final TextView profile = findViewById(R.id.profile_icon);
        final TextView goOut = findViewById(R.id.go_out_icon);


        news.setOnClickListener(new ClickOnMainIconHandler<>(new NewsFragment()).getListener(NEWS_TAG, fragmentManager, container));
        walks.setOnClickListener(new ClickOnMainIconHandler<>(new WalksFragment()).getListener(WALKS_TAG, fragmentManager, container));
        friends.setOnClickListener(new ClickOnMainIconHandler<>(new FriendsFragment()).getListener(FRIENDS_TAG, fragmentManager, container));
        profile.setOnClickListener(new ClickOnMainIconHandler<>(new ProfileFragment()).getListener(PROFILE_TAG, fragmentManager, container));
        goOut.setOnClickListener(new ClickOnMainIconHandler<>(new GoOutFragment()).getListener(GO_OUT_TAG, fragmentManager, container));

        if (getSupportFragmentManager().findFragmentById(container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(container, new NewsFragment(), NEWS_TAG)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private class ClickOnMainIconHandler<T extends Fragment> {

        final private T newFragment;

        ClickOnMainIconHandler(T newFragment) {
            this.newFragment = newFragment;
        }

        public View.OnClickListener getListener(final String tag, final FragmentManager fragmentManager, final int container) {
            return v -> {
                T fragment = (T) fragmentManager.findFragmentByTag(tag);
                if (fragment == null) {
                    replaceFragment(tag, fragmentManager, container, newFragment);
                    return;
                }
                if (fragmentManager.findFragmentById(container) != fragment) {
                    replaceFragment(tag, fragmentManager, container, fragment);
                }
            };
        }

        private void replaceFragment(final String tag, final FragmentManager fragmentManager, final int container, final T fragment) {
            fragmentManager
                    .beginTransaction()
                    .replace(container, fragment, tag)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }
}

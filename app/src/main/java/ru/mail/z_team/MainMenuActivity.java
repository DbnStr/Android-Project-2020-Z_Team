package ru.mail.z_team;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mail.z_team.icon_fragments.friends.FriendsFragment;
import ru.mail.z_team.icon_fragments.go_out.GoOutFragment;
import ru.mail.z_team.icon_fragments.news.NewsFragment;
import ru.mail.z_team.icon_fragments.walks.WalksFragment;
import ru.mail.z_team.icon_fragments.profile.ProfileFragment;

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


        news.setOnClickListener(new ClickOnIconHandler<>(new NewsFragment()).getListener(NEWS_TAG, fragmentManager, container));
        walks.setOnClickListener(new ClickOnIconHandler<>(new WalksFragment()).getListener(WALKS_TAG, fragmentManager, container));
        friends.setOnClickListener(new ClickOnIconHandler<>(new FriendsFragment()).getListener(FRIENDS_TAG, fragmentManager, container));
        profile.setOnClickListener(new ClickOnIconHandler<>(new ProfileFragment()).getListener(PROFILE_TAG, fragmentManager, container));
        goOut.setOnClickListener(new ClickOnIconHandler<>(new GoOutFragment()).getListener(GO_OUT_TAG, fragmentManager, container));

        if (getSupportFragmentManager().findFragmentById(container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(container, new NewsFragment(), NEWS_TAG)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isUserAuth(firebaseAuth.getCurrentUser())) {
            startMainActivityAndFinishThis();
        }
    }

    private class ClickOnIconHandler<T extends Fragment> {

        final private T newFragment;

        ClickOnIconHandler(T newFragment) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_action){
            firebaseAuth.signOut();
            startMainActivityAndFinishThis();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isUserAuth(FirebaseUser user) {
        return user != null;
    }

    private void startMainActivityAndFinishThis() {
        startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
        finish();
    }
}

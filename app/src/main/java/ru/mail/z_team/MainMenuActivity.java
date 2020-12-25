package ru.mail.z_team;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.mail.z_team.icon_fragments.friends.FriendsFragment;
import ru.mail.z_team.icon_fragments.news.NewsFragment;
import ru.mail.z_team.icon_fragments.profile.ProfileFragment;
import ru.mail.z_team.icon_fragments.walks.WalkAnnotation;
import ru.mail.z_team.icon_fragments.walks.WalkFragment;
import ru.mail.z_team.icon_fragments.walks.WalksFragment;
import ru.mail.z_team.user.User;
import ru.mail.z_team.user.UserFragment;

public class MainMenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    static private final String NEWS_TAG = "NEWS FRAGMENT";
    static private final String WALKS_TAG = "WALKS FRAGMENT";
    static private final String FRIENDS_TAG = "FRIENDS FRAGMENT";
    static private final String PROFILE_TAG = "PROFILE FRAGMENT";
    static private final String USER_TAG = "USER FRAGMENT";
    private static final String WALK_TAG = "WALK FRAGMENT";

    private static final String LOG_TAG = "MainMenuActivity";
    private Logger logger;

    FirebaseAuth firebaseAuth;
    FragmentManager fragmentManager;
    int container;
    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Main", "onCreate");
        setContentView(R.layout.activity_main_menu);
        logger = new Logger(LOG_TAG, true);

        firebaseAuth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();
        container = R.id.current_menu_container;
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (getSupportFragmentManager().findFragmentById(container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(container, new NewsFragment(), NEWS_TAG)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }

        actionBar = getSupportActionBar();
        actionBar.setTitle("GO OUT!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isUserAuth(firebaseAuth.getCurrentUser())) {
            startMainActivityAndFinishThis();
        }
    }

    public void openUserProfile(User user){
        fragmentManager
                .beginTransaction()
                .replace(container, new UserFragment(user), USER_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void openWalkProfile(WalkAnnotation walk) {
        logger.log("Open walk profile");
        fragmentManager
                .beginTransaction()
                .replace(container, new WalkFragment(walk), WALK_TAG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final Fragment fragment;
        final String tag;
        final Fragment nFragment;

        switch (item.getItemId()) {
            case R.id.news_feed_icon:
                fragment = new NewsFragment();
                nFragment = (NewsFragment) fragmentManager.findFragmentByTag(NEWS_TAG);
                tag = NEWS_TAG;
                break;
            case R.id.walks_icon:
                fragment = new WalksFragment();
                nFragment = (WalksFragment) fragmentManager.findFragmentByTag(WALKS_TAG);
                tag = WALKS_TAG;
                break;
            case R.id.friends_icon:
                fragment = new FriendsFragment();
                nFragment = (FriendsFragment) fragmentManager.findFragmentByTag(FRIENDS_TAG);
                tag = FRIENDS_TAG;
                break;
            case R.id.profile_icon:
                fragment = new ProfileFragment();
                nFragment = (ProfileFragment) fragmentManager.findFragmentByTag(PROFILE_TAG);
                tag = PROFILE_TAG;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (nFragment == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(container, fragment, tag)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        } else if (fragmentManager.findFragmentById(container) != nFragment) {
            fragmentManager
                    .beginTransaction()
                    .replace(container, nFragment, tag)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
        return true;
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

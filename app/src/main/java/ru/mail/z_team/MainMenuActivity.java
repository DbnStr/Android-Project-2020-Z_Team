package ru.mail.z_team;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ru.mail.z_team.icon_fragments.FriendsFragment;
import ru.mail.z_team.icon_fragments.NewsFragment;
import ru.mail.z_team.icon_fragments.ProfileFragment;
import ru.mail.z_team.icon_fragments.WalksFragment;

public class MainMenuActivity extends AppCompatActivity {

    static private final String NEWS_TAG = "NEWS FRAGMENT";
    static private final String WALKS_TAG = "WALKS FRAGMENT";
    static private final String FRIENDS_TAG = "FRIENDS FRAGMENT";
    static private final String PROFILE_TAG = "PROFILE FRAGMENT";

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

        news.setOnClickListener(new ClickOnMainIconHandler<>(new NewsFragment()).getListener(NEWS_TAG, fragmentManager, container));
        walks.setOnClickListener(new ClickOnMainIconHandler<>(new WalksFragment()).getListener(WALKS_TAG, fragmentManager, container));
        friends.setOnClickListener(new ClickOnMainIconHandler<>(new FriendsFragment()).getListener(FRIENDS_TAG, fragmentManager, container));
        profile.setOnClickListener(new ClickOnMainIconHandler<>(new ProfileFragment()).getListener(PROFILE_TAG, fragmentManager, container));

        if (getSupportFragmentManager().findFragmentById(container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(container, new NewsFragment(), NEWS_TAG)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        }
    }

    private class ClickOnMainIconHandler<T extends Fragment> {

        final private T newFr;

        ClickOnMainIconHandler(T newFr) {
            this.newFr = newFr;
        }

        public View.OnClickListener getListener(final String tag, final FragmentManager fragmentManager,final int container) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T fragment = (T) fragmentManager.findFragmentByTag(tag);
                    if (fragment != null) {
                        if (fragmentManager.findFragmentById(container) != fragment) {
                            fragmentManager
                                    .beginTransaction()
                                    .replace(container, fragment, tag)
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss();
                        }
                    }
                    else {
                        fragmentManager
                                .beginTransaction()
                                .replace(container, newFr, tag)
                                .addToBackStack(null)
                                .commitAllowingStateLoss();
                    }
                }
            };
        }
    }
}

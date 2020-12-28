package ru.mail.z_team.icon_fragments.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class FriendsFragment extends Fragment {

    private static final String LOG_TAG = "FriendsFragment";
    private Logger logger;

    private static final String PAGE_ARG = "current-page-number";

    private FriendsViewModel viewModel;

    private ViewPager viewPager;

    private Button addFriendBtn;
    private TextInputLayout fieldAddFriend;
    int container;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");
        viewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        logger.log("onCreateView");

        addFriendBtn = view.findViewById(R.id.add_friend_by_id_btn);
        fieldAddFriend = view.findViewById(R.id.add_friend_by_id_et);
        this.container = R.id.current_menu_container;

        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new FriendsPagerAdapter(getChildFragmentManager(), 0));

        if (savedInstanceState != null
                && savedInstanceState.containsKey(PAGE_ARG)) {
            logger.log("restore instance: " + savedInstanceState.getInt(PAGE_ARG));
            viewPager.setCurrentItem(savedInstanceState.getInt(PAGE_ARG));
        }

        TabLayout tabLayout = view.findViewById(R.id.tabs_friends);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addFriendBtn.setOnClickListener(v -> {
            String id = fieldAddFriend.getEditText().getText().toString().trim();
            if (id.equals("")) {
                fieldAddFriend.setError("Id can't be empty");
                fieldAddFriend.setFocusable(true);
            } else {
                logger.log("addFriendBtn");
                viewModel.checkUserExistence(id);

                ExistenceObserver<Boolean> observer = new ExistenceObserver<>(id);
                viewModel.userExists().observe(getActivity(), observer);
            }
        });
    }

    class ExistenceObserver<T extends Boolean> implements Observer<T> {

        private final String id;

        ExistenceObserver(String id) {
            logger.log("ExistenceObserver");
            this.id = id;
        }

        @Override
        public void onChanged(T t) {
            if (t.booleanValue()) {
                logger.log("FriendExisted");
                viewModel.addFriendToCurrentUser(id);
            } else {
                fieldAddFriend.setError("User with entered ID doesn't exist");
                fieldAddFriend.setFocusable(true);
            }
            viewModel.userExists().removeObservers(getActivity());
        }
    }

    @Override
    public void onResume() {
        logger.log("onResume");
        viewPager.setCurrentItem(0);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (viewPager != null) {
            logger.log("save instance: " + viewPager.getCurrentItem());
            outState.putInt(PAGE_ARG, viewPager.getCurrentItem());
        }
        super.onSaveInstanceState(outState);
    }
}

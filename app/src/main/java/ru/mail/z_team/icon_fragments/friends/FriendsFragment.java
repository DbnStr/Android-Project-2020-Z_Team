package ru.mail.z_team.icon_fragments.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class FriendsFragment extends Fragment {

    private static final String LOG_TAG = "FriendsFragment";
    private static final String FRIEND_REQUEST_FRAGMENT_TAG = "FRIENDS REQUESTS FRAGMENT";
    private Logger logger;

    private FriendAdapter adapter;

    private FriendsViewModel viewModel;

    private Button addFriendBtn;
    private TextInputLayout fieldAddFriend;
    private TextView noFriends;

    int container;

    public FriendsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        addFriendBtn = view.findViewById(R.id.add_friend_by_id_btn);
        fieldAddFriend = view.findViewById(R.id.add_friend_by_id_et);
        noFriends = view.findViewById(R.id.no_friend_tv);
        this.container = R.id.current_menu_container;

        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new FriendsPagerAdapter(getActivity().getSupportFragmentManager(), 0));

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
                noFriends.setVisibility(View.INVISIBLE);
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
}

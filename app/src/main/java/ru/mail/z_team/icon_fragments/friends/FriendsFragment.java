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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.friends.friend_request.FriendRequestFragment;

public class FriendsFragment extends Fragment {

    private static final String LOG_TAG = "FriendsFragment";
    private static final String FRIEND_REQUEST_FRAGMENT_TAG = "FRIENDS REQUESTS FRAGMENT";
    private Logger logger;

    private FriendAdapter adapter;

    private FriendsViewModel viewModel;

    private Button addFriendBtn;
    private Button friendRequestBtn;
    private TextInputLayout fieldAddFriend;
    private TextView noFriends;

    int container;

    public FriendsFragment() {

    }

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
        friendRequestBtn =view.findViewById(R.id.friend_request);
        fieldAddFriend = view.findViewById(R.id.add_friend_by_id_et);
        noFriends = view.findViewById(R.id.no_friend_tv);
        this.container = R.id.current_menu_container;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new FriendAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        viewModel.updateCurrentUserFriends();
        viewModel.updateCurrentUserFriendRequestList();
        viewModel.getCurrentUserFriends()
                .observe(getActivity(), friends -> {
                    logger.log("get user friends... " + friends.toString());
                    if (friends.isEmpty()) {
                        noFriends.setVisibility(View.VISIBLE);
                    } else {
                        noFriends.setVisibility(View.INVISIBLE);
                        adapter.setFriends(friends);
                    }
                });

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

        friendRequestBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FriendRequestFragment friendRequestFragment = (FriendRequestFragment)fragmentManager.findFragmentByTag(FRIEND_REQUEST_FRAGMENT_TAG);
            if (friendRequestFragment == null) {
                replaceFragment(FRIEND_REQUEST_FRAGMENT_TAG, fragmentManager, container, new FriendRequestFragment());
            } else {
                replaceFragment(FRIEND_REQUEST_FRAGMENT_TAG, fragmentManager, container, friendRequestFragment);
            }
        });
    }

    private void replaceFragment(final String tag, final FragmentManager fragmentManager, final int container, final FriendRequestFragment fragment) {
        fragmentManager
                .beginTransaction()
                .replace(container, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
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

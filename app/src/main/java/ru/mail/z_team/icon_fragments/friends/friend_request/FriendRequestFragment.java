package ru.mail.z_team.icon_fragments.friends.friend_request;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.friends.FriendsFragment;
import ru.mail.z_team.icon_fragments.friends.FriendsViewModel;

public class FriendRequestFragment extends Fragment {

    private static final String LOG_TAG = "FriendRequestFragment";
    static private final String FRIENDS_TAG = "FRIENDS FRAGMENT";
    private Logger logger;

    private Button backToFriendsListBtn;
    private TextView noFriendRequests;
    private SwipeRefreshLayout friendRequestRefreshLayout;

    private FriendRequestAdapter adapter;

    private FriendsViewModel friendsViewModel;

    int container;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);

        backToFriendsListBtn = view.findViewById(R.id.back_to_friends_list_button);
        noFriendRequests = view.findViewById(R.id.no_friend_requests_tv);
        friendRequestRefreshLayout = view.findViewById(R.id.swipe_to_refresh_friend_request);
        this.container = R.id.current_menu_container;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_friend_request);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendsViewModel = new ViewModelProvider(this).get(FriendsViewModel.class);

        adapter = new FriendRequestAdapter(getActivity(), friendsViewModel);
        recyclerView.setAdapter(adapter);

        friendsViewModel.updateCurrentUserFriendRequestList();
        friendsViewModel.getCurrentUserFriendRequestList()
                .observe(getActivity(), friends -> {
                    logger.log("get user friend requests... " + friends.toString());
                    if (friends.isEmpty()) {
                        noFriendRequests.setVisibility(View.VISIBLE);
                    } else {
                        noFriendRequests.setVisibility(View.INVISIBLE);
                    }
                    adapter.setFriends(friends);
                });

        backToFriendsListBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FriendsFragment friendsFragment = (FriendsFragment) fragmentManager.findFragmentByTag(FRIENDS_TAG);
            if (friendsFragment == null) {
                replaceFragment(FRIENDS_TAG, fragmentManager, container, new FriendsFragment());
            } else {
                replaceFragment(FRIENDS_TAG, fragmentManager, container, friendsFragment);
            }
        });

        friendRequestRefreshLayout.setOnRefreshListener(() -> {
            friendsViewModel.updateCurrentUserFriendRequestList();
            //Todo : как-то проверять процесс обновления друзей(полученя даннъы из дб), и только при успехе убирать значок обновления
            friendRequestRefreshLayout.setRefreshing(false);
        });
    }

    private void replaceFragment(final String tag, final FragmentManager fragmentManager, final int container, final FriendsFragment fragment) {
        fragmentManager
                .beginTransaction()
                .replace(container, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}

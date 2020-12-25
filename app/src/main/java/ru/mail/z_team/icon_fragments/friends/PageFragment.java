package ru.mail.z_team.icon_fragments.friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.friends.friend_request.FriendRequestAdapter;

public class PageFragment extends Fragment {
    private static final String LOG_TAG = "PageFragment";
    private Logger logger;

    private FriendAdapter adapter;

    private FriendsViewModel viewModel;
    private TextView noFriends, noFriendRequests;
    private SwipeRefreshLayout friendsRefreshLayout;

    int container;

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public PageFragment() {

    }

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("OnCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPage = getArguments().getInt(ARG_PAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_friends, container, false);

        friendsRefreshLayout = view.findViewById(R.id.swipe_to_refresh_friends);
        noFriends = view.findViewById(R.id.no_friend);
        noFriendRequests = view.findViewById(R.id.no_friend_request);
        this.container = R.id.current_menu_container;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        logger.log("onViewCreated");

        viewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        viewModel.updateCurrentUserFriends();
        viewModel.updateCurrentUserFriendRequestList();
        if (mPage == 0) {
            logger.log("friendFragment");
            adapter = new FriendAdapter(getActivity());
            recyclerView.setAdapter(adapter);
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

            friendsRefreshLayout.setOnRefreshListener(() -> {
                viewModel.updateCurrentUserFriends();
                //Todo : как-то проверять процесс обновления друзей(полученя даннъы из дб), и только при успехе убирать значок обновления
                friendsRefreshLayout.setRefreshing(false);
            });
        }
        else if (mPage == 1) {
            logger.log("friendRequestFragment");
            FriendRequestAdapter friendRequestAdapter = new FriendRequestAdapter(getActivity(), viewModel);
            recyclerView.setAdapter(friendRequestAdapter);

            viewModel.getCurrentUserFriendRequestList()
                    .observe(getActivity(), friends -> {
                        logger.log("get user friend requests... " + friends.toString());
                        if (friends.isEmpty()) {
                            noFriendRequests.setVisibility(View.VISIBLE);
                            logger.log("no requests");
                        } else {
                            noFriendRequests.setVisibility(View.INVISIBLE);
                            friendRequestAdapter.setFriends(friends);
                        }
                    });

            friendsRefreshLayout.setOnRefreshListener(() -> {
                viewModel.updateCurrentUserFriendRequestList();
                //Todo : как-то проверять процесс обновления друзей(полученя даннъы из дб), и только при успехе убирать значок обновления
                friendsRefreshLayout.setRefreshing(false);
            });
        }



    }
}

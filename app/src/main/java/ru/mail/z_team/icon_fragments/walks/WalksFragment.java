package ru.mail.z_team.icon_fragments.walks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.friends.FriendsFragment;
import ru.mail.z_team.icon_fragments.go_out.WalkViewModel;
import ru.mail.z_team.user.Friend;

public class WalksFragment extends Fragment {

    WalkAdapter adapter;
    WalkViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("WalksFragment", "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walks, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_walks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new WalkAdapter(getActivity());

        String userId = FirebaseAuth.getInstance().getUid();
        viewModel = new ViewModelProvider(this).get(WalkViewModel.class);
        viewModel.update();
        viewModel.getCurrentUser()
                .observe(getActivity(), user -> {
                    ArrayList<Friend> friends = user.getFriends();
                    Log.d(LOG_TAG, "get user friends... " + friends.toString());
                    if (friends.isEmpty()) {
                        noFriends.setVisibility(View.VISIBLE);
                    } else {
                        noFriends.setVisibility(View.INVISIBLE);
                        adapter.setFriends(friends);
                    }
                });

        addFriendBtn = view.findViewById(R.id.add_friend_by_id_btn);
        fieldAddFriend = view.findViewById(R.id.add_friend_by_id_et);
        noFriends = view.findViewById(R.id.no_friend_tv);

        addFriendBtn.setOnClickListener(v -> {
            String id = fieldAddFriend.getText().toString().trim();
            if (id.equals("")) {
                fieldAddFriend.setError("Id can't be empty");
                fieldAddFriend.setFocusable(true);
            } else {
                Log.d(LOG_TAG, "addFriendBtn");
                noFriends.setVisibility(View.INVISIBLE);
                viewModel.checkUserExistence(id);

                FriendsFragment.ExistenceObserver<Boolean> observer = new FriendsFragment.ExistenceObserver<>(id);
                viewModel.userExists().observe(getActivity(), observer);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}

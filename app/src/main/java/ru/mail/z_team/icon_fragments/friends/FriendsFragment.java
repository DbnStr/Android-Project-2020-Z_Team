package ru.mail.z_team.icon_fragments.friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ru.mail.z_team.R;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.UserViewModel;

public class FriendsFragment extends Fragment {

    private static final String LOG_TAG = "FriendsFragment";


    private FriendAdapter adapter;
    UserViewModel viewModel;
    Button addFriendBtn;
    EditText fieldAddFriend;
    TextView noFriends;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log("OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        addFriendBtn = view.findViewById(R.id.add_friend_by_id_btn);
        fieldAddFriend = view.findViewById(R.id.add_friend_by_id_et);
        noFriends = view.findViewById(R.id.no_friend_tv);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new FriendAdapter(getActivity());

        String userId = FirebaseAuth.getInstance().getUid();
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.updateCurrentUser();
        viewModel.getCurrentUser()
                .observe(getActivity(), user -> {
                    ArrayList<Friend> friends = user.getFriends();
                    log("get user friends... " + friends.toString());
                    if (friends.isEmpty()) {
                        noFriends.setVisibility(View.VISIBLE);
                    } else {
                        noFriends.setVisibility(View.INVISIBLE);
                        adapter.setFriends(friends);
                    }
                });

        addFriendBtn.setOnClickListener(v -> {
            String id = fieldAddFriend.getText().toString().trim();
            if (id.equals("")) {
                fieldAddFriend.setError("Id can't be empty");
                fieldAddFriend.setFocusable(true);
            } else {
                log("addFriendBtn");
                noFriends.setVisibility(View.INVISIBLE);
                viewModel.checkUserExistence(id);

                ExistenceObserver<Boolean> observer = new ExistenceObserver<>(id);
                viewModel.userExists().observe(getActivity(), observer);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    class ExistenceObserver<T extends Boolean> implements Observer<T> {

        private final String id;

        ExistenceObserver(String id) {
            log("ExistenceObserver");
            this.id = id;
        }

        @Override
        public void onChanged(T t) {
            if (t.booleanValue()) {
                log("FriendExisted");
                viewModel.addFriend(id, adapter.getItemCount());
            } else {
                fieldAddFriend.setError("User with entered ID doesn't exist");
                fieldAddFriend.setFocusable(true);
            }
            viewModel.userExists().removeObservers(getActivity());
        }
    }
}

package ru.mail.z_team.icon_fragments.friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import ru.mail.z_team.R;

public class FriendsFragment extends Fragment {

    private FriendAdapter adapter;
    FriendsViewModel viewModel;
    Button addFriendBtn;
    EditText friendIdEt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("FriendsFragment", "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new FriendAdapter();
        recyclerView.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getUid();
        viewModel = new ViewModelProvider(this).get(FriendsViewModel.class);
        viewModel.update(userId);
        viewModel.getUserFriendsById(userId)
                .observe(getActivity(), (Observer<List<String>>) ids -> {
                    adapter.setFriends(ids);
                });

        addFriendBtn = view.findViewById(R.id.add_friend_by_id_btn);
        friendIdEt = view.findViewById(R.id.add_friend_by_id_et);

        addFriendBtn.setOnClickListener(v -> {
            String id = friendIdEt.getText().toString().trim();
            if (id == ""){
                friendIdEt.setError("Id can't be empty");
                friendIdEt.setFocusable(true);
            }
            else{
                adapter.addFriend(id);
            }
        });


        return view;
    }
}

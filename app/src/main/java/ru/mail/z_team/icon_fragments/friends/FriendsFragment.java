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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ru.mail.z_team.R;
import ru.mail.z_team.user.UserViewModel;

public class FriendsFragment extends Fragment {

    private static final String LOG_TAG = "FriendsFragment";
    private FriendAdapter adapter;
    UserViewModel viewModel;
    Button addFriendBtn;
    EditText friendIdEt;
    TextView noFriends;

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

        String userId = FirebaseAuth.getInstance().getUid();
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        viewModel.updateFriends(userId);
        viewModel.getUserFriendsById(userId)
                .observe(getActivity(), ids -> {
                    if (ids.isEmpty()){
                        noFriends.setVisibility(View.VISIBLE);
                    }
                    else{
                        List<String> names = getFriendsNames(ids);
                        noFriends.setVisibility(View.INVISIBLE);
                        adapter.setFriends(ids);
                    }
                });

        addFriendBtn = view.findViewById(R.id.add_friend_by_id_btn);
        friendIdEt = view.findViewById(R.id.add_friend_by_id_et);
        noFriends = view.findViewById(R.id.no_friend_tv);

        addFriendBtn.setOnClickListener(v -> {
            String id = friendIdEt.getText().toString().trim();
            if (id == ""){
                friendIdEt.setError("Id can't be empty");
                friendIdEt.setFocusable(true);
            }
            else{
                Log.d(LOG_TAG, "addFriendBtn");
                noFriends.setVisibility(View.INVISIBLE);
                //adapter.addFriend(id);
                viewModel.userExists(id).observe(getActivity(), existence -> {
                    if (existence){
                        viewModel.addFriend(id, adapter.getItemCount());
                    }
                    else{
                        friendIdEt.setError("User with entered ID doesn't exist");
                        friendIdEt.setFocusable(true);
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> getFriendsNames(List<String> ids) {
        List<String> names = new ArrayList<>();
        for (String id: ids){
            //
        }
        return names;
    }
}

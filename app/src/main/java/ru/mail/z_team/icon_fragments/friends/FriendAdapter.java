package ru.mail.z_team.icon_fragments.friends;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ru.mail.z_team.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder>{

    private final List<String> friends;
    FriendsViewModel viewModel;

    public FriendAdapter(Activity context){
        String userId = FirebaseAuth.getInstance().getUid();
        friends = new ArrayList<>();
        viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(FriendsViewModel.class);
        viewModel.update(userId);
        viewModel.getUserFriendsById(userId)
                .observe((LifecycleOwner) context, (Observer<List<String>>) ids -> {
                    //get user by id
                    friends.addAll(ids);
                });

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.friend.setText(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}

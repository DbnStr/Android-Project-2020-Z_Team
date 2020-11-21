package ru.mail.z_team.icon_fragments.friends;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mail.z_team.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder>{

    private static final String LOG_TAG = "FriendAdapter";
    private final List<String> friends;

    public FriendAdapter(){
        friends = new ArrayList<>();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Log.d(LOG_TAG, "OnBindViewHolderFriend " + position);
        holder.friend.setText(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void addFriend(String id) {
        friends.add(id);
        Log.d(LOG_TAG, "addFriend");
        this.notifyItemInserted(friends.size() - 1);
    }

    public void setFriends(List<String> ids) {
        friends.addAll(ids);
        Log.d(LOG_TAG, "addAllFriends");
        Log.d(LOG_TAG, friends.toString());
        this.notifyItemInserted(friends.size() - 1);
    }
}

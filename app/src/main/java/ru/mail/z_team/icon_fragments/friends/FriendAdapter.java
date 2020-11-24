package ru.mail.z_team.icon_fragments.friends;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mail.z_team.MainMenuActivity;
import ru.mail.z_team.R;
import ru.mail.z_team.user.User;

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder>{

    private static final String LOG_TAG = "FriendAdapter";
    private List<User> friends;
    private final Context context;

    public FriendAdapter(Context context){
        this.context = context;
        this.friends = new ArrayList<>();
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
        String name = friends.get(position).getName();
        if (name.equals("")) {
            name = "No Name";
        }
        holder.friend.setText(name);
        holder.friend.setOnClickListener(v -> {
            if (context instanceof MainMenuActivity){
                ((MainMenuActivity) context).openUserProfile(friends.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setFriends(List<User> users) {
        friends = users;
        this.notifyItemRangeChanged(0, friends.size());
    }
}

package ru.mail.z_team.icon_fragments.friends;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.MainMenuActivity;
import ru.mail.z_team.R;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private static final String LOG_TAG = "FriendAdapter";
    private final Logger logger;
    private ArrayList<Friend> friends;
    private final Context context;

    public FriendAdapter(Context context) {
        this.context = context;
        this.friends = new ArrayList<>();
        logger = new Logger(LOG_TAG, true);
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        logger.log("onBindViewHolder " + position);
        String name = friends.get(position).getName();
        if (name.equals("")) {
            name = "No Name";
        }
        holder.friend.setText(name);
        holder.friend.setOnClickListener(v -> {
            Friend friend = friends.get(position);
            if (context instanceof MainMenuActivity){

                User user = new User(
                        friend.name,
                        14,
                        new ArrayList<>(),
                        friend.id
                        );
                ((MainMenuActivity) context).openUserProfile(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setFriends(ArrayList<Friend> users) {
        friends = users;
        this.notifyDataSetChanged();
    }
}

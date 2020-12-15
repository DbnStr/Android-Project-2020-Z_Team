package ru.mail.z_team.icon_fragments.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.user.Friend;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {

    private static final String LOG_TAG = "FriendAdapter";
    private final Logger logger;
    private ArrayList<Friend> friends;
    private final Context context;

    private final FriendsViewModel viewModel;

    public FriendRequestAdapter(Context context, FriendsViewModel viewModel) {
        this.context = context;
        this.friends = new ArrayList<>();
        logger = new Logger(LOG_TAG, true);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        logger.log("onBindViewHolder " + position);
        String name = friends.get(position).getName();
        if (name.equals("")) {
            name = "No Name";
        }
        holder.friendRequest.setText(name);
        holder.acceptFriendRequestBtn.setOnClickListener(v -> {
            viewModel.acceptFriendRequest(position);
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void setFriends(ArrayList<Friend> users) {
        friends = users;
        this.notifyItemRangeChanged(0, friends.size());
    }
}

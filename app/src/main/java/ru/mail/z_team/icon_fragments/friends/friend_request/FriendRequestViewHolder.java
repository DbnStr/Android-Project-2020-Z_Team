package ru.mail.z_team.icon_fragments.friends.friend_request;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mail.z_team.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    public final TextView friendRequest;
    public final Button acceptFriendRequestBtn;

    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);

        friendRequest = itemView.findViewById(R.id.friend_request_item);
        acceptFriendRequestBtn = itemView.findViewById(R.id.accept_friend_request_btn);
    }
}

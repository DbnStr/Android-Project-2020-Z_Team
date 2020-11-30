package ru.mail.z_team.icon_fragments.friends;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mail.z_team.R;

public class FriendViewHolder extends RecyclerView.ViewHolder {

    public final TextView friend;

    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        friend = itemView.findViewById(R.id.friend_item);
    }
}

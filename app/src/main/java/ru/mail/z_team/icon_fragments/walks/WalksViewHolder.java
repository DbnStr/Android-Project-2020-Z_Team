package ru.mail.z_team.icon_fragments.walks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mail.z_team.R;

public class WalksViewHolder extends RecyclerView.ViewHolder {
    public final TextView walkTitle;
    public final TextView walkDate;
    public final View walkBody;

    public WalksViewHolder(@NonNull View itemView) {
        super(itemView);
        walkTitle = itemView.findViewById(R.id.walk_item_title);
        walkDate = itemView.findViewById(R.id.walk_item_date);
        walkBody = itemView.findViewById(R.id.walk_item);
    }
}

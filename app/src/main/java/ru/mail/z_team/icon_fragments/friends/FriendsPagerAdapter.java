package ru.mail.z_team.icon_fragments.friends;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.mail.z_team.Logger;

public class FriendsPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] tabTitles = new String[] { "My Friends", "Friend Requests" };

    private final Logger logger = new Logger("FriendsPagerAdapter", true);

    public FriendsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        logger.log("FriendsPagerAdapter");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

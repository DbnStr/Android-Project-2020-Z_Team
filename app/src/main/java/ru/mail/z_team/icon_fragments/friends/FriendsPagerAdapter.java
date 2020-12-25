package ru.mail.z_team.icon_fragments.friends;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FriendsPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles = new String[] { "My Friends", "Friend Requests" };

    public FriendsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
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

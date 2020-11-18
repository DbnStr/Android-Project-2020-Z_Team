package ru.mail.z_team.icon_fragments.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

public class FriendsRepo {
    private final static MutableLiveData<List<Friend>> FRIENDS = new MutableLiveData<>();

    static {
        FRIENDS.setValue(Collections.<Friend>emptyList());
    }

    public LiveData<List<Friend>> getFriends(){
        return FRIENDS;
    }

}

package ru.mail.z_team.icon_fragments.friends;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

public class FriendsRepo {
    private final static MutableLiveData<List<Friend>> friends = new MutableLiveData<>();

    static {
        friends.setValue(Collections.<Friend>emptyList());
    }

    public LiveData<List<Friend>> getFriends(){
        return friends;
    }

}

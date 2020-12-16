package ru.mail.z_team.icon_fragments;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.local_storage.UserFriend;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class Transformer {
    /*
        function order
        for result : User -> Friend -> Walk
        for type of result: default type -> userApi type -> local_storage type
        for type of arguments : default type -> userApi type -> local_storage type
     */

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    /* default User result */

    public static User transformToUser(UserApi.User user) {
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        ArrayList<Friend> userFriends = new ArrayList<>();
        if (user.friends != null) {
            userFriends.addAll(Transformer.transformToFriendAll(user.friends));
        }
        return new User(
                name,
                user.age,
                user.id,
                userFriends
        );
    }

    public static User transformToUser(ru.mail.z_team.local_storage.User user) {
        return new User(
                user.name,
                user.age,
                user.id,
                new ArrayList<>()
        );
    }

    /* default Friend result */

    public static ArrayList<Friend> transformToFriendAll(ArrayList<UserApi.Friend> friends) {
        ArrayList<Friend> result = new ArrayList<>();
        for(UserApi.Friend friend : friends) {
            result.add(transformToFriend(friend));
        }
        return result;
    }

    public static Friend transformToFriend(UserApi.Friend friend) {
        return new Friend(friend.name, friend.id);
    }

    public static ArrayList<Friend> transformToFriendAll(List<UserFriend> friends) {
        ArrayList<Friend> result = new ArrayList<>();
        for (UserFriend friend : friends) {
            result.add(transformToFriend(friend));
        }
        return result;
    }

    public static Friend transformToFriend(ru.mail.z_team.local_storage.UserFriend friend) {
        return new Friend(
                friend.name,
                friend.id
        );
    }

    /* default Walk result */

    public static ArrayList<Walk> transformToWalkAll(ArrayList<UserApi.Walk> walks) {
        ArrayList<Walk> result = new ArrayList<>();
        for (UserApi.Walk walk : walks) {
            result.add(transformToWalk(walk));
        }
        return result;
    }

    public static Walk transformToWalk(UserApi.Walk walk) {
        Walk transformed = new Walk();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        transformed.setDate(getDate(walk.date));
        return transformed;
    }

    public static ArrayList<Walk> transformToWalkAll(List<ru.mail.z_team.local_storage.UserWalk> walks) {
        ArrayList<Walk> result = new ArrayList<>();
        for (ru.mail.z_team.local_storage.UserWalk walk : walks) {
            result.add(Transformer.transformToWalk(walk));
        }
        return result;
    }

    public static Walk transformToWalk(ru.mail.z_team.local_storage.UserWalk walk) {
        Walk result = new Walk();
        result.setAuthor(walk.author);
        result.setTitle(walk.title);
        result.setDate(getDate(walk.date));
        return result;
    }

    /* UserApi User result */

    public static UserApi.User transformToUserApiUser(User user) {
        UserApi.User result = new UserApi.User();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        return result;
    }

    /* UserApi Friend result */

    public static UserApi.Friend transformToUserApiFriend(UserApi.User user) {
        UserApi.Friend result = new UserApi.Friend();
        result.id = user.id;
        result.name = user.name;
        return result;
    }

    /* LocalDB User result */

    public static ru.mail.z_team.local_storage.User transformToLocalDBUser(User user) {
        ru.mail.z_team.local_storage.User result = new ru.mail.z_team.local_storage.User();
        result.name = user.name;
        result.id = user.id;
        result.age = user.age;

        return result;
    }

    public static ru.mail.z_team.local_storage.User transformToLocalDBUser(UserApi.User user) {
        ru.mail.z_team.local_storage.User result = new ru.mail.z_team.local_storage.User();
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        result.name = name;
        result.id = user.id;
        result.age = user.age;

        return result;
    }

    /* LocalDB Friend result */

    public static List<ru.mail.z_team.local_storage.Friend> transformToLocalDBFriendALl(List<Friend> friends, String currentUserId) {
        List<ru.mail.z_team.local_storage.Friend> result = new ArrayList<>();
        for (Friend friend : friends) {
            result.add(transformToLocalDBFriend(friend, currentUserId));
        }
        return result;
    }

    public static ru.mail.z_team.local_storage.Friend transformToLocalDBFriend(Friend friend, String currentUserId) {
        return new ru.mail.z_team.local_storage.Friend(
                friend.name,
                friend.id,
                currentUserId
        );
    }

    /* LocalDB Walk result */

    public static List<ru.mail.z_team.local_storage.Walk> transformToLocalDBWalkAll(List<Walk> walks) {
        List<ru.mail.z_team.local_storage.Walk> result = new ArrayList<>();
        for(Walk walk : walks) {
            result.add(transformToLocalDBWalk(walk));
        }
        return result;
    }

    public static ru.mail.z_team.local_storage.Walk transformToLocalDBWalk(Walk walk) {
        return new ru.mail.z_team.local_storage.Walk(
                walk.getTitle(),
                walk.getAuthor(),
                walk.getDate().toString()
        );
    }

    private static Date getDate(String date) {
        Date result;
        try {
            result = sdf.parse(date);
        } catch (ParseException e) {
            result = new Date();
            e.printStackTrace();
        }
        return result;
    }
}

package ru.mail.z_team.icon_fragments;

import android.annotation.SuppressLint;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.icon_fragments.walks.WalkAnnotation;
import ru.mail.z_team.local_storage.UserFriend;
import ru.mail.z_team.map.Story;
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

    public static Walk transformToWalk(UserApi.Walk walk) {
        Walk transformed = new Walk();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        FeatureCollection map = FeatureCollection.fromJson(walk.walk);
        transformed.setMap(map);
        transformed.setStories(transformToStoryAll(walk.stories));
        transformed.setDate(getDate(walk.date));
        return transformed;
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

    /* default Story */

    public static ArrayList<Story> transformToStoryAll(ArrayList<UserApi.Story> stories) {
        ArrayList<Story> transformed = new ArrayList<>();
        if (stories != null) {
            for (UserApi.Story apiStory : stories) {
                transformed.add(transformToStory(apiStory));
            }
        }
        return transformed;
    }

    public static Story transformToStory(UserApi.Story apiStory) {
        Story story = new Story();
        story.setDescription(apiStory.description);
        story.setPlace(apiStory.place);
        story.setId(apiStory.id);
        story.setUrlImages(apiStory.images);
        story.setPoint(Feature.fromJson(apiStory.point));
        return story;
    }

    /* Walk Annotation */

    public static ArrayList<WalkAnnotation> transformToWalkAnnotationAll(ArrayList<UserApi.WalkInfo> walks) {
        ArrayList<WalkAnnotation> result = new ArrayList<>();
        for (UserApi.WalkInfo walk : walks) {
            result.add(transformToWalkAnnotation(walk));
        }
        return result;
    }

    public static WalkAnnotation transformToWalkAnnotation(UserApi.WalkInfo walk) {
        WalkAnnotation transformed = new WalkAnnotation();
        transformed.setTitle(walk.title);
        transformed.setAuthor(walk.author);
        transformed.setAuthorId(walk.id);
        try {
            transformed.setDate(sdf.parse(walk.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transformed;
    }
}

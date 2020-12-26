package ru.mail.z_team.icon_fragments;

import android.annotation.SuppressLint;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.DatabaseStory;
import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseWalk;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;
import ru.mail.z_team.icon_fragments.go_out.UIWalk;
import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.icon_fragments.walks.WalkAnnotation;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class Transformer {
    /*
        function order
        for result : User -> Friend -> WalkAnnotation -> Walk -> Story
        for type of result: default type -> database type
        for type of arguments : default type -> database type
     */

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

    /* default User result */

    public static User transformToUser(DatabaseUser user) {
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        ArrayList<Friend> userFriends = new ArrayList<>();
        if (user.friends != null) {
            userFriends.addAll(Transformer.transformToFriendAll(user.friends));
        }
        User u = new User(
                name,
                user.age,
                userFriends,
                user.id
        );

        //возможно тут null
        u.setImageUrl(user.imageUrl);

        return u;
    }

    /* default Friend result */

    public static ArrayList<Friend> transformToFriendAll(List<DatabaseFriend> friends) {
        ArrayList<Friend> result = new ArrayList<>();
        for (DatabaseFriend friend : friends) {
            result.add(transformToFriend(friend));
        }
        return result;
    }

    public static Friend transformToFriend(DatabaseFriend friend) {
        return new Friend(friend.name, friend.id);
    }

    /* default WalkAnnotation result */

    public static ArrayList<WalkAnnotation> transformToWalkAnnotationAll(List<DatabaseWalkAnnotation> walksAnnotations) {
        ArrayList<WalkAnnotation> result = new ArrayList<>();
        for (DatabaseWalkAnnotation walksAnnotation : walksAnnotations) {
            result.add(transformToWalkAnnotation(walksAnnotation));
        }
        return result;
    }

    public static WalkAnnotation transformToWalkAnnotation(DatabaseWalkAnnotation walkAnnotation) {
        WalkAnnotation result = new WalkAnnotation();
        result.setTitle(walkAnnotation.title);
        result.setAuthorName(walkAnnotation.authorName);
        result.setAuthorId(walkAnnotation.authorId);
        result.setDate(getDate(walkAnnotation.date));
        return result;
    }

    /* default Walk result */

    public static Walk transformToWalk(DatabaseWalk databaseWalk) {
        Walk result = new Walk();
        result.setTitle(databaseWalk.title);
        result.setAuthorName(databaseWalk.authorName);
        FeatureCollection map = FeatureCollection.fromJson(databaseWalk.walkInfo);
        result.setMap(map);
        result.setStories(transformToStoryAll(databaseWalk.stories));
        result.setDate(getDate(databaseWalk.date));
        return result;
    }

    public static DatabaseWalk transformToWalk(UIWalk uiWalk, String authorName, String authorId, String date) {
        DatabaseWalk result = new DatabaseWalk();
        result.title = uiWalk.getTitle();
        result.walkInfo = uiWalk.getWalkInfo().toJson();
        //должнгы быть истории, но проблема с goOutRepository(непонятно как перенести трансформ)
        result.authorName = authorName;
        result.authorId = authorId;
        result.date = date;

        return result;
    }

    /* default Story result */

    public static ArrayList<Story> transformToStoryAll(List<DatabaseStory> stories) {
        ArrayList<Story> result = new ArrayList<>();
        if (stories != null) {
            for (DatabaseStory story : stories) {
                result.add(transformToStory(story));
            }
        }
        return result;
    }

    public static Story transformToStory(DatabaseStory localStory) {
        Story story = new Story();
        story.setDescription(localStory.description);
        story.setPlace(localStory.place);
        story.setId(localStory.id);
        story.setUrlImages(localStory.images);
        story.setPoint(Feature.fromJson(localStory.point));
        return story;
    }

    /* DatabaseUser result */

    public static DatabaseUser transformToDatabaseUser(User user) {
        DatabaseUser result = new DatabaseUser();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        result.imageUrl = user.getImageUrl();
        return result;
    }

    /* DatabaseFriend result */

    public static DatabaseFriend transformToDatabaseFriend(DatabaseUser user) {
        DatabaseFriend result = new DatabaseFriend();
        result.id = user.id;
        result.name = user.name;
        return result;
    }

    public static List<DatabaseFriend> transformToDatabaseFriendAll(List<Friend> friends, String currentUserId) {
        List<DatabaseFriend> result = new ArrayList<>();
        for (Friend friend : friends) {
            result.add(transformToDatabaseFriend(friend, currentUserId));
        }
        return result;
    }

    public static DatabaseFriend transformToDatabaseFriend(Friend friend, String currentUserId) {
        return new DatabaseFriend(
                friend.name,
                friend.id,
                currentUserId
        );
    }

    /* DatabaseStory result */

    public static List<DatabaseStory> addWalkDateToAllStory(List<DatabaseStory> stories, String walkDate) {
        List<DatabaseStory> result = new ArrayList<>();
        if (stories != null) {
            for (DatabaseStory story : stories) {
                story.walkDate = walkDate;
                result.add(story);
            }
        }
        return result;
    }

    /* helper methods */

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

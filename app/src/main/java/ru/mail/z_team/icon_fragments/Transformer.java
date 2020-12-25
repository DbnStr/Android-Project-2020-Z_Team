package ru.mail.z_team.icon_fragments;

import android.annotation.SuppressLint;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;
import ru.mail.z_team.databases.local_storage.LocalDbFriend;
import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.local_storage.story.UserStory;
import ru.mail.z_team.databases.local_storage.walk.UserWalk;
import ru.mail.z_team.databases.local_storage.walk_annotation.WalkAnnotation;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.databases.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class Transformer {
    /*
        function order
        for result : User -> LocalDbFriend -> WalkAnnotation -> Walk -> Story
        for type of result: default type -> userApi type -> local_storage type
        for type of arguments : default type -> userApi type -> local_storage type
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
        User u =  new User(
                name,
                user.age,
                userFriends,
                user.id
        );

        //возможно тут null
        u.setImageUrl(user.imageUrl);

        return u;
    }

    /* default LocalDbFriend result */

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

    public static ArrayList<ru.mail.z_team.icon_fragments.walks.WalkAnnotation> transformToWalkAnnotationAll(List<DatabaseWalkAnnotation> walksAnnotations) {
        ArrayList<ru.mail.z_team.icon_fragments.walks.WalkAnnotation> result = new ArrayList<>();
        for (DatabaseWalkAnnotation walksAnnotation : walksAnnotations) {
            result.add(transformToWalkAnnotation(walksAnnotation));
        }
        return result;
    }

    public static ru.mail.z_team.icon_fragments.walks.WalkAnnotation transformToWalkAnnotation(DatabaseWalkAnnotation walkAnnotation) {
        ru.mail.z_team.icon_fragments.walks.WalkAnnotation result = new ru.mail.z_team.icon_fragments.walks.WalkAnnotation();
        result.setTitle(walkAnnotation.title);
        result.setAuthorName(walkAnnotation.authorName);
        result.setAuthorId(walkAnnotation.authorId);
        result.setDate(getDate(walkAnnotation.date));
        return result;
    }

    /* default Walk result */

    public static Walk transformToWalk(UserApi.Walk walk) {
        Walk transformed = new Walk();
        transformed.setTitle(walk.title);
        transformed.setAuthorName(walk.authorName);
        FeatureCollection map = FeatureCollection.fromJson(walk.walk);
        transformed.setMap(map);
        transformed.setStories(transformToStoryAll(walk.stories));
        transformed.setDate(getDate(walk.date));
        return transformed;
    }

    public static Walk transformToWalk(UserWalk walk) {
        Walk result = new Walk();
        result.setTitle(walk.title);
        result.setAuthorName(walk.authorName);
        FeatureCollection map = FeatureCollection.fromJson(walk.walk);
        result.setMap(map);
        result.setStories(transformToStoryAll(walk.stories));
        result.setDate(getDate(walk.date));
        return result;
    }

    /* default Story result */

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

    public static ArrayList<Story> transformToStoryAll(List<UserStory> stories) {
        ArrayList<Story> result = new ArrayList<>();
        if (stories != null) {
            for (UserStory story : stories) {
                if (story == null) {
                    Log.e("DSD", "DSD");
                }
                result.add(transformToStory(story));
            }
        }
        return result;
    }

    public static Story transformToStory(UserStory localStory) {
        Log.d("Transformer", localStory.walkDate);
        Story story = new Story();
        story.setDescription(localStory.description);
        story.setPlace(localStory.place);
        story.setId(localStory.id);
        story.setUrlImages(new ArrayList<>());
        story.setPoint(Feature.fromJson(localStory.point));
        return story;
    }

    /* UserApi User result */

    public static DatabaseUser transformToUserApiUser(User user) {
        DatabaseUser result = new DatabaseUser();
        result.id = user.getId();
        result.name = user.getName();
        result.age = user.getAge();
        result.imageUrl = user.getImageUrl();
        return result;
    }

    /* UserApi LocalDbFriend result */

    public static DatabaseFriend transformToUserApiFriend(DatabaseUser user) {
        DatabaseFriend result = new DatabaseFriend();
        result.id = user.id;
        result.name = user.name;
        return result;
    }

    /* LocalDB User result */

    public static DatabaseUser transformToLocalDBUser(User user) {
        DatabaseUser result = new DatabaseUser();
        result.name = user.name;
        result.id = user.id;
        result.age = user.age;

        return result;
    }

    public static DatabaseUser transformToLocalDBUser(DatabaseUser user) {
        DatabaseUser result = new DatabaseUser();
        String name = user.name;
        if (name == null) {
            name = "Anonymous";
        }
        result.name = name;
        result.id = user.id;
        result.age = user.age;

        return result;
    }

    /* LocalDB LocalDbFriend result */

    public static List<LocalDbFriend> transformToLocalDBFriendALl(List<Friend> friends, String currentUserId) {
        List<LocalDbFriend> result = new ArrayList<>();
        for (Friend friend : friends) {
            result.add(transformToLocalDBFriend(friend, currentUserId));
        }
        return result;
    }

    public static LocalDbFriend transformToLocalDBFriend(Friend friend, String currentUserId) {
        return new LocalDbFriend(
                friend.name,
                friend.id,
                currentUserId
        );
    }

    /* LocalDB WalkAnnotation result */

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

    /* LocalDB Story result */

    public static List<ru.mail.z_team.databases.local_storage.story.Story> transformToLocalDBStoryAll(ArrayList<UserApi.Story> stories, String walkDate) {
        List<ru.mail.z_team.databases.local_storage.story.Story> result = new ArrayList<>();
        if (stories != null) {
            for (UserApi.Story story : stories) {
                result.add(transformToLocalDBStory(story, walkDate));
            }
        }
        return result;
    }

    public static ru.mail.z_team.databases.local_storage.story.Story transformToLocalDBStory(UserApi.Story story, String walkDate) {
        ru.mail.z_team.databases.local_storage.story.Story result = new ru.mail.z_team.databases.local_storage.story.Story();
        result.description = story.description;
        result.place = story.place;
        result.point = story.point;
        result.id = story.id;
        result.walkDate = walkDate;
        return result;
    }

    /* WalkAnnotation LocalDB result*/

    public static List<WalkAnnotation> transformToLocalDBWalkAnnotationAll(List<DatabaseWalkAnnotation> walksAnnotations) {
        List<WalkAnnotation> result = new ArrayList<>();
        for (DatabaseWalkAnnotation walkAnnotation : walksAnnotations) {
            result.add(transformToLocalDBWalkAnnotation(walkAnnotation));
        }
        return result;
    }

    public static WalkAnnotation transformToLocalDBWalkAnnotation(DatabaseWalkAnnotation walkAnnotation) {
        return new WalkAnnotation(
                walkAnnotation.title,
                walkAnnotation.authorName,
                walkAnnotation.authorId,
                walkAnnotation.date);
    }

    /* Walk LocalDB result */

    public static List<ru.mail.z_team.databases.local_storage.walk.Walk> transformToLocalDBWalkAll(Map<String, UserApi.Walk> walks, String userId) {
        List<ru.mail.z_team.databases.local_storage.walk.Walk> result = new ArrayList<>();
        walks.forEach((k, walk) -> result.add(transformToLocalDBWalk(walk, userId)));
        return result;
    }

    public static ru.mail.z_team.databases.local_storage.walk.Walk transformToLocalDBWalk(UserApi.Walk walk, String userId) {
        return new ru.mail.z_team.databases.local_storage.walk.Walk(
                walk.title,
                walk.authorName,
                userId,
                walk.date,
                walk.walk
        );
    }
}

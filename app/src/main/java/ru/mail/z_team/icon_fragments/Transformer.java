package ru.mail.z_team.icon_fragments;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.Pair;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import java.text.AttributedCharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.mail.z_team.icon_fragments.walks.Walk;
import ru.mail.z_team.local_storage.UserFriend;
import ru.mail.z_team.local_storage.UserStory;
import ru.mail.z_team.local_storage.UserWalk;
import ru.mail.z_team.local_storage.UserWalkAnnotation;
import ru.mail.z_team.map.Story;
import ru.mail.z_team.network.UserApi;
import ru.mail.z_team.user.Friend;
import ru.mail.z_team.user.User;

public class Transformer {
    /*
        function order
        for result : User -> Friend -> WalkAnnotation
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
        for (UserApi.Friend friend : friends) {
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

    /* default WalkAnnotation result */

    public static ru.mail.z_team.icon_fragments.walks.WalkAnnotation transformToWalk(UserWalkAnnotation walk) {
        Walk result = new Walk();
        result.setTitle(walk.title);
        result.setAuthorName(walk.authorName);
        result.setAuthorId(walk.authorId);
        result.setDate(getDate(walk.date));
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

    /* LocalDB WalkAnnotation result */

    public static List<ru.mail.z_team.local_storage.WalkAnnotation> transformToLocalDBWalkAll(List<Walk> walks) {
        List<ru.mail.z_team.local_storage.WalkAnnotation> result = new ArrayList<>();
        for (Walk walk : walks) {
            result.add(transformToLocalDBWalk(walk));
        }
        return result;
    }

    public static ru.mail.z_team.local_storage.WalkAnnotation transformToLocalDBWalk(Walk walk) {
        return new ru.mail.z_team.local_storage.WalkAnnotation(
                walk.getTitle(),
                walk.getAuthorName(),
                walk.getAuthorId(),
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

    /* LocalDB Story result */

    public static List<ru.mail.z_team.local_storage.Story> transformToLocalDBStoryAll(ArrayList<UserApi.Story> stories, String walkDate) {
        List<ru.mail.z_team.local_storage.Story> result = new ArrayList<>();
        if (stories != null) {
            for (UserApi.Story story : stories) {
                result.add(transformToLocalDBStory(story, walkDate));
            }
        }
        return result;
    }

    public static ru.mail.z_team.local_storage.Story transformToLocalDBStory(UserApi.Story story, String walkDate) {
        ru.mail.z_team.local_storage.Story result = new ru.mail.z_team.local_storage.Story();
        result.description = story.description;
        result.place = story.place;
        result.point = story.point;
        result.id = story.id;
        result.walkDate = walkDate;
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

    /* WalkAnnotation result */

    public static ArrayList<ru.mail.z_team.icon_fragments.walks.WalkAnnotation> transformToWalkAnnotationAll(ArrayList<UserApi.WalkAnnotation> walks) {
        ArrayList<ru.mail.z_team.icon_fragments.walks.WalkAnnotation> result = new ArrayList<>();
        for (UserApi.WalkAnnotation walk : walks) {
            result.add(transformToWalkAnnotation(walk));
        }
        return result;
    }

    public static ru.mail.z_team.icon_fragments.walks.WalkAnnotation transformToWalkAnnotation(UserApi.WalkAnnotation walk) {
        ru.mail.z_team.icon_fragments.walks.WalkAnnotation transformed = new ru.mail.z_team.icon_fragments.walks.WalkAnnotation();
        transformed.setTitle(walk.title);
        transformed.setAuthorName(walk.authorName);
        transformed.setAuthorId(walk.authorId);
        try {
            transformed.setDate(sdf.parse(walk.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transformed;
    }

    public static ArrayList<ru.mail.z_team.icon_fragments.walks.WalkAnnotation> transformToWalkAnnotationAll(List<UserWalkAnnotation> walksAnnotations) {
        ArrayList<ru.mail.z_team.icon_fragments.walks.WalkAnnotation> result = new ArrayList<>();
        for (UserWalkAnnotation walksAnnotation : walksAnnotations) {
            result.add(transformToWalkAnnotation(walksAnnotation));
        }
        return result;
    }

    public static ru.mail.z_team.icon_fragments.walks.WalkAnnotation transformToWalkAnnotation(UserWalkAnnotation walkAnnotation) {
        ru.mail.z_team.icon_fragments.walks.WalkAnnotation result = new ru.mail.z_team.icon_fragments.walks.WalkAnnotation();
        result.setTitle(walkAnnotation.title);
        result.setAuthorName(walkAnnotation.authorName);
        result.setAuthorId(walkAnnotation.authorId);
        result.setDate(getDate(walkAnnotation.date));
        return result;
    }

    /* WalkAnnotation LocalDB result*/

    public static List<ru.mail.z_team.local_storage.WalkAnnotation> transformToLocalDBWalkAnnotationAll(List<UserApi.WalkAnnotation> walksAnnotations) {
        List<ru.mail.z_team.local_storage.WalkAnnotation> result = new ArrayList<>();
        for (UserApi.WalkAnnotation walkAnnotation : walksAnnotations) {
            result.add(transformToLocalDBWalkAnnotation(walkAnnotation));
        }
        return result;
    }

    public static ru.mail.z_team.local_storage.WalkAnnotation transformToLocalDBWalkAnnotation(UserApi.WalkAnnotation walkAnnotation) {
        return new ru.mail.z_team.local_storage.WalkAnnotation(
                walkAnnotation.title,
                walkAnnotation.authorName,
                walkAnnotation.authorId,
                walkAnnotation.date);
    }

    /* Walk LocalDB result */

    public static List<ru.mail.z_team.local_storage.Walk> transformToLocalDBWalkAll(Map<String, UserApi.Walk> walks, String userId) {
        List<ru.mail.z_team.local_storage.Walk> result = new ArrayList<>();
        walks.forEach((k,walk) -> {
            result.add(transformToLocalDBWalk(walk, userId));
        });
        return result;
    }
    public static ru.mail.z_team.local_storage.Walk transformToLocalDBWalk(UserApi.Walk walk, String userId) {
        return new ru.mail.z_team.local_storage.Walk(
                walk.title,
                walk.authorName,
                userId,
                walk.date,
                walk.walk
        );
    }
}

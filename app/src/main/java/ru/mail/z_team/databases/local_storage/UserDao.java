package ru.mail.z_team.databases.local_storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.local_storage.friend.Friend;
import ru.mail.z_team.databases.local_storage.story.Story;
import ru.mail.z_team.databases.local_storage.story.UserStory;
import ru.mail.z_team.databases.local_storage.walk.UserWalk;
import ru.mail.z_team.databases.local_storage.walk.Walk;
import ru.mail.z_team.databases.local_storage.walk_annotation.UserWalkAnnotation;
import ru.mail.z_team.databases.local_storage.walk_annotation.WalkAnnotation;

@Dao
public abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DatabaseUser user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Friend friend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(WalkAnnotation walkAnnotation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Walk walk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Story story);

    @Query("DELETE FROM Friend")
    public abstract void deleteAllFriends();

    @Query("DELETE FROM WalkAnnotation")
    public abstract void deleteAllWalksAnnotations();

    @Query("DELETE FROM Walk")
    public abstract void deleteAllWalks();

    @Query("DELETE FROM story WHERE walkDate == :walkDate")
    public abstract void deleteAllStoryByIdWalk(String walkDate);

    @Transaction
    public void deleteAllWalkAnnotationAndAddNew(List<WalkAnnotation> walkAnnotations) {
        deleteAllWalksAnnotations();
        for (WalkAnnotation walkAnnotation : walkAnnotations) {
            insert(walkAnnotation);
        }
    }

    @Transaction
    public void deleteAllFriendsAndAddNew(List<Friend> friends) {
        deleteAllFriends();
        for (Friend friend : friends) {
            insert(friend);
        }
    }

    @Transaction
    public void deleteAllWalkAndAddNew(List<Walk> walks) {
        deleteAllWalks();
        for (Walk walk : walks) {
            insert(walk);
        }
    }

    @Transaction
    public void deleteAllWalkStoryAndAddNew(List<Story> stories, String walkDate) {
        deleteAllStoryByIdWalk(walkDate);
        for (Story story : stories) {
            insert(story);
        }
    }

    @Transaction
    public DatabaseUser getUserById(String id) {
        DatabaseUser result = getUserWithoutFriendsById(id);
        result.friends = getUserFriends(id);
        return result;
    }

    @Transaction
    public UserWalk getUserWalkWithStories(String walkId, String walkDate) {
        UserWalk userWalk = getUserWalk(walkId, walkDate);
        userWalk.stories = getWalkStoriesByDate(walkDate);
        return userWalk;
    }

    @Query("SELECT * FROM DatabaseUser WHERE id = :id")
    public abstract DatabaseUser getUserWithoutFriendsById(String id);

    @Query("SELECT friend.id, friend.friend_name AS fr_name " + "FROM friend " + "WHERE friend.current_user_id == :id")
    public abstract List<DatabaseFriend> getUserFriends(String id);

    @Query("SELECT WalkAnnotation.title, WalkAnnotation.authorName, WalkAnnotation.authorId, WalkAnnotation.date " + "FROM WalkAnnotation, DatabaseUser " + "WHERE WalkAnnotation.authorId == DatabaseUser.id")
    public abstract List<UserWalkAnnotation> getUserWalksAnnotations();

    @Query("SELECT * FROM walk WHERE authorId == :id AND date == :date")
    public abstract UserWalk getUserWalk(String id, String date);

    @Query("SELECT Story.description, Story.place, Story.point, Story.id, Story.walkDate " + "FROM Story " + "WHERE story.walkDate = :walkDate")
    public abstract List<UserStory> getWalkStoriesByDate(String walkDate);
}

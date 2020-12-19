package ru.mail.z_team.local_storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(User user);

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
    public UserWalk getUserWalkWithStories(String walkId, String walkDate) {
        UserWalk userWalk = getUserWalk(walkId, walkDate);
        userWalk.stories = getWalkStoriesByDate(walkDate);
        return userWalk;
    }

    @Query("SELECT * FROM user WHERE id = :id")
    public abstract User getById(String id);

    @Query("SELECT friend.id, friend.friend_name AS fr_name " + "FROM friend, user " + "WHERE friend.current_user_id == user.id")
    public abstract List<UserFriend> getUserFriends();

    @Query("SELECT WalkAnnotation.title, WalkAnnotation.authorName, WalkAnnotation.authorId, WalkAnnotation.date " + "FROM WalkAnnotation, user " + "WHERE WalkAnnotation.authorId == user.id")
    public abstract List<UserWalkAnnotation> getUserWalksAnnotations();

    @Query("SELECT Walk.title, Walk.authorName, Walk.authorId, Walk.date, Walk.walk " + "FROM Walk, User " + "WHERE Walk.authorId == user.id")
    public abstract List<UserWalk> getUserWalks();

    @Query("SELECT * FROM walk WHERE authorId == :id AND date == :date")
    public abstract UserWalk getUserWalk(String id, String date);

    @Query("SELECT Story.description, Story.place, Story.point, Story.id, Story.walkDate " + "FROM Story " + "WHERE story.walkDate = :walkDate")
    public abstract List<UserStory> getWalkStoriesByDate(String walkDate);
}

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

    @Query("DELETE FROM Friend")
    public abstract void deleteAllFriends();

    @Query("DELETE FROM WalkAnnotation")
    public abstract void deleteAllWalksAnnotations();

    @Transaction
    public void deleteAllWalkAnnotationAndAddNew(List<WalkAnnotation> walkAnnotations) {
        deleteAllWalksAnnotations();
        for(WalkAnnotation walkAnnotation : walkAnnotations) {
            insert(walkAnnotation);
        }
    }

    @Transaction
    public void deleteAllFriendsAndAddNew(List<Friend> friends) {
        deleteAllFriends();
        for(Friend friend : friends) {
            insert(friend);
        }
    }

    @Query("SELECT * FROM user WHERE id = :id")
    public abstract User getById(String id);

    @Query("SELECT friend.id, friend.friend_name AS fr_name " + "FROM friend, user " + "WHERE friend.current_user_id == user.id")
    public abstract List<UserFriend> getUserFriends();

    @Query("SELECT WalkAnnotation.title, WalkAnnotation.authorName, WalkAnnotation.authorId, WalkAnnotation.date " + "FROM WalkAnnotation, user " + "WHERE WalkAnnotation.authorId == user.id")
    public abstract List<UserWalkAnnotation> getUserWalksAnnotations();
}

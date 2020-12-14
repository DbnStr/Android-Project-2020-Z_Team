package ru.mail.z_team.local_storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Friend friend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Walk walk);

    @Query("SELECT * FROM user WHERE id = :id")
    User getById(String id);

    @Query("SELECT friend.id, friend.friend_name AS fr_name " + "FROM friend, user " + "WHERE friend.current_user_id == user.id")
    public List<UserFriend> getUserFriends();

    @Query("SELECT walk.title, walk.date, walk.author " + "FROM walk, user " + "WHERE walk.author == user.id")
    public List<UserWalk> getUserWalks();
}

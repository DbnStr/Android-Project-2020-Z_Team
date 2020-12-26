package ru.mail.z_team.databases.local_storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import ru.mail.z_team.databases.DatabaseStory;
import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.DatabaseWalk;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;

@Dao
public abstract class UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DatabaseUser user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DatabaseFriend databaseFriend);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DatabaseWalkAnnotation walkAnnotation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DatabaseWalk databaseWalk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(DatabaseStory story);

    @Query("DELETE FROM DatabaseFriend")
    public abstract void deleteAllFriends();

    @Query("DELETE FROM DatabaseWalkAnnotation")
    public abstract void deleteAllWalksAnnotations();

    @Query("DELETE FROM DatabaseWalk")
    public abstract void deleteAllWalks();

    @Query("DELETE FROM DatabaseStory WHERE walkDate == :walkDate")
    public abstract void deleteAllStoryByIdWalk(String walkDate);

    @Transaction
    public void deleteAllWalkAnnotationAndAddNew(List<DatabaseWalkAnnotation> walkAnnotations) {
        deleteAllWalksAnnotations();
        for (DatabaseWalkAnnotation walkAnnotation : walkAnnotations) {
            insert(walkAnnotation);
        }
    }

    @Transaction
    public void deleteAllFriendsAndAddNew(List<DatabaseFriend> localDbFriends) {
        deleteAllFriends();
        for (DatabaseFriend localDbFriend : localDbFriends) {
            insert(localDbFriend);
        }
    }

    @Transaction
    public void deleteAllWalkAndAddNew(List<DatabaseWalk> databaseWalks) {
        deleteAllWalks();
        for (DatabaseWalk databaseWalk : databaseWalks) {
            insert(databaseWalk);
        }
    }

    @Transaction
    public void deleteAllWalkStoryAndAddNew(List<DatabaseStory> stories, String walkDate) {
        deleteAllStoryByIdWalk(walkDate);
        for (DatabaseStory story : stories) {
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
    public DatabaseWalk getUserWalkWithStories(String walkId, String walkDate) {
        DatabaseWalk databaseWalk = getUserWalk(walkId, walkDate);
        databaseWalk.stories = getWalkStoriesByDate(walkDate);
        return databaseWalk;
    }

    @Query("SELECT * FROM DatabaseUser WHERE id = :id")
    public abstract DatabaseUser getUserWithoutFriendsById(String id);

    @Query("SELECT * FROM DatabaseFriend WHERE current_user_id == :parentUserId")
    public abstract List<DatabaseFriend> getUserFriends(String parentUserId);

    @Query("SELECT * FROM DatabaseWalkAnnotation WHERE authorId == :authorId")
    public abstract List<DatabaseWalkAnnotation> getUserWalksAnnotations(String authorId);

    @Query("SELECT * FROM DatabaseWalk WHERE authorId == :id AND date == :date")
    public abstract DatabaseWalk getUserWalk(String id, String date);

    @Query("SELECT * FROM DatabaseStory WHERE walkDate = :walkDate")
    public abstract List<DatabaseStory> getWalkStoriesByDate(String walkDate);
}

package ru.mail.z_team.databases.local_storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.local_storage.friend.Friend;
import ru.mail.z_team.databases.local_storage.story.Story;
import ru.mail.z_team.databases.local_storage.walk.Walk;
import ru.mail.z_team.databases.local_storage.walk_annotation.WalkAnnotation;

@Database(entities = {DatabaseUser.class, Friend.class, WalkAnnotation.class, Walk.class, Story.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract UserDao getUserDao();
}

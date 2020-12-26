package ru.mail.z_team.databases.local_storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.DatabaseStory;
import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseWalk;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;

@Database(entities = {DatabaseUser.class, DatabaseFriend.class, DatabaseWalkAnnotation.class, DatabaseWalk.class, DatabaseStory.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract UserDao getUserDao();
}

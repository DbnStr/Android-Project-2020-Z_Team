package ru.mail.z_team.icon_fragments.go_out;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.z_team.Logger;
import ru.mail.z_team.databases.DatabaseStory;
import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseWalk;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;
import ru.mail.z_team.databases.network.DatabaseApiRepository;
import ru.mail.z_team.databases.network.UserApi;
import ru.mail.z_team.icon_fragments.DatabaseCallback;
import ru.mail.z_team.icon_fragments.Transformer;
import ru.mail.z_team.map.Story;

public class GoOutRepository {

    private static final String LOG_TAG = "GoOutRepository";
    private final Logger logger;

    private final UserApi userApi;
    private final Context context;

    private final MutableLiveData<GoOutRepository.PostStatus> postStatus = new MutableLiveData<>();

    SimpleDateFormat sdf =
            new SimpleDateFormat("yyMMddHHmmssZ");

    public GoOutRepository(Context context) {
        userApi = DatabaseApiRepository.from(context).getUserApi();
        logger = new Logger(LOG_TAG, true);
        this.context = context;
    }

    public void postWalk(UIWalk uiWalk) {
        String currentUserId = FirebaseAuth.getInstance().getUid();

        userApi.getUserById(currentUserId).enqueue(new DatabaseCallback<DatabaseUser>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<DatabaseUser> response) {
                logger.errorLog("Fail with update");
            }

            @Override
            public void onSuccessResponse(Response<DatabaseUser> response) {
                String currentUserName = response.body().name;
                String date = sdf.format(new Date());
                DatabaseWalk databaseWalk = Transformer.transformToWalk(uiWalk, currentUserName, currentUserId, date);
                databaseWalk.stories = transformToDatabaseStoryAll(uiWalk.getStories(), currentUserId, date);
                DatabaseWalkAnnotation walkAnnotation = new DatabaseWalkAnnotation(uiWalk.getTitle(), date, currentUserName, currentUserId);

                postWalkAndWalkAnnotationInRemoteDatabase(databaseWalk, walkAnnotation);
            }
        });
    }

    private void postWalkAndWalkAnnotationInRemoteDatabase(DatabaseWalk walk, DatabaseWalkAnnotation walkAnnotation) {
        userApi.getUserWalksAnnotationsById(walk.authorId).enqueue(new DatabaseCallback<ArrayList<DatabaseWalkAnnotation>>(LOG_TAG) {
            @Override
            public void onNullResponse(Response<ArrayList<DatabaseWalkAnnotation>> response) {
                addWalkInRemoteDB(walk);
                addWalkAnnotationInRemoteDB(0, walkAnnotation);
            }

            @Override
            public void onSuccessResponse(Response<ArrayList<DatabaseWalkAnnotation>> response) {
                int count = response.body().size();

                addWalkInRemoteDB(walk);
                addWalkAnnotationInRemoteDB(count, walkAnnotation);
            }
        });
    }


    private void addWalkInRemoteDB(DatabaseWalk walk) {
        logger.log("Post a walk");
        userApi.addWalk(walk.authorId, walk.date, walk).enqueue(new Callback<DatabaseWalk>() {
            @Override
            public void onResponse(Call<DatabaseWalk> call, Response<DatabaseWalk> response) {
                if (response.isSuccessful()) {
                    postStatus.postValue(PostStatus.OK);
                } else {
                    postStatus.postValue(PostStatus.FAILED);
                }
            }

            @Override
            public void onFailure(Call<DatabaseWalk> call, Throwable t) {
                logger.errorLog(t.getMessage());
                postStatus.postValue(PostStatus.FAILED);
            }
        });
    }

    private void addWalkAnnotationInRemoteDB(int currentWalkNumber, DatabaseWalkAnnotation walkAnnotation) {
        logger.log("Post a walk annotation");
        userApi.addWalkAnnotation(walkAnnotation.authorId, currentWalkNumber, walkAnnotation).enqueue(new Callback<DatabaseWalkAnnotation>() {
            @Override
            public void onResponse(Call<DatabaseWalkAnnotation> call, Response<DatabaseWalkAnnotation> response) {
                if (response.isSuccessful()) {
                    postStatus.postValue(PostStatus.OK);
                } else {
                    postStatus.postValue(PostStatus.FAILED);
                }
            }

            @Override
            public void onFailure(Call<DatabaseWalkAnnotation> call, Throwable t) {
                logger.errorLog(t.getMessage());
                postStatus.postValue(PostStatus.FAILED);
            }
        });
    }

    private ArrayList<DatabaseStory> transformToDatabaseStoryAll(ArrayList<Story> stories,
                                                                 String authorId,
                                                                 String date) {
        ArrayList<DatabaseStory> res = new ArrayList<>();
        for (Story story : stories) {
            res.add(transformToDatabaseStory(story, authorId, date, stories.indexOf(story)));
        }
        return res;
    }

    private DatabaseStory transformToDatabaseStory(Story story,
                                                   String authorId,
                                                   String date,
                                                   int i) {
        DatabaseStory apiStory = new DatabaseStory(story.getDescription());
        apiStory.place = story.getPlace();
        apiStory.id = story.getId();
        apiStory.images = new ArrayList<>();
        apiStory.point = story.getPoint().toJson();
        for (int j = 0; j < story.getUriImages().size(); j++) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                    .child("WalkMaps/" + authorId + "/" + date + "/stories/" + i + "/images/"
                            + story.getUriImages().get(j).getLastPathSegment());
            storageReference.putFile(story.getUriImages().get(j)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    logger.log("successfully added the image");
                } else {
                    logger.errorLog("error in adding the image");
                }
            });
            apiStory.images.add(storageReference.getPath());
        }
        return apiStory;
    }

    public MutableLiveData<PostStatus> getPostStatus() {
        return postStatus;
    }

    public enum PostStatus {
        OK,
        FAILED
    }
}

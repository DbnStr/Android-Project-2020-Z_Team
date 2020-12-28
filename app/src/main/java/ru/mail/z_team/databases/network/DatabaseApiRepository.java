package ru.mail.z_team.databases.network;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.mail.z_team.ApplicationModified;

public class DatabaseApiRepository {

    private static final String BASE_URL = "https://android-project-2020-zteam.firebaseio.com/";
    private final UserApi mUserApi;

    public DatabaseApiRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mUserApi = retrofit.create(UserApi.class);
    }

    public UserApi getUserApi() {
        return mUserApi;
    }

    public static DatabaseApiRepository from(Context context) {
        return ApplicationModified.from(context).getApis();
    }
}

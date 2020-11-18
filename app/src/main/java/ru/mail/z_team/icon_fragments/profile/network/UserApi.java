package ru.mail.z_team.icon_fragments.profile.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserApi {
    class User {
        public String name;
        public int age;
    }

    @GET("/Users/{id}.json")
    Call<User> getUserById(@Path("id") int id);
}

package ru.mail.z_team.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    class User {
        public String name;
        public int age;
        public String id;

        public User(String id) {
            this.id = id;
        }

        public User(){};
    }

    @GET("/Users/{id}.json")
    Call<User> getUserById(@Path("id") String id);

    @PUT("/Users/{id}.json")
    Call<User> addUser(@Path("id") String id, @Body User user);

    @PATCH("/Users/{id}.json")
    Call<User> changeUserInformation(@Path("id") String id, @Body User user);
}

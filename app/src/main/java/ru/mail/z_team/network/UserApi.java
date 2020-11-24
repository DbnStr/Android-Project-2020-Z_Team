package ru.mail.z_team.network;

import java.util.ArrayList;
import java.util.List;

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
        public ArrayList<Friend> friends;
        public String email;
        public String id;

        public User(String id, String email) {
            this.id = id;
            this.email = email;
            this.friends = new ArrayList<>();
            friends.add(new Friend(name, id, email, age));
        }

        public User(){}
    }

    class Friend {
        public String name;
        public int age;
        public String email;
        public String id;

        public Friend(String name, String id, String email, int age){
            this.name = name;
            this.age = age;
            this.email = email;
            this.id = id;
        }

        public Friend(){}
    }

    @GET("/Users/{id}.json")
    Call<User> getUserById(@Path("id") String id);

    @GET("/Users/{id}/friends.json")
    Call<List<Friend>> getUserFriendsById(@Path("id") String id);

    @PUT("/Users/{id}.json")
    Call<User> addUser(@Path("id") String id, @Body User user);

    @PUT("/Users/{id}/friends/{num}.json")
    Call<Friend> addFriend(@Path("id") String id, @Path("num") String num, @Body Friend friend);

    @PATCH("/Users/{id}.json")
    Call<User> changeUserInformation(@Path("id") String id, @Body User user);
}

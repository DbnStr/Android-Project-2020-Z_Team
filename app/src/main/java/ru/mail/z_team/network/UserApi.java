package ru.mail.z_team.network;

import java.util.ArrayList;

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

        public User(String id, String email, String name) {
            this.name = name;
            this.id = id;
            this.email = email;
            this.friends = new ArrayList<>();
        }

        public User(){}
    }

    class Friend {
        public String name;
        public String id;

        public Friend(String name, String id){
            this.name = name;
            this.id = id;
        }

        public Friend(){}
    }

    class WalkInfo {
        public String title;
        public String author;
        public String date;
        public String id;

        public WalkInfo(String title, String date, String author, String id) {
            this.title = title;
            this.date = date;
            this.author = author;
            this.id = id;
        }
    }

    class Walk {
        public String title;
        public String author;
        public String date;
        public String walk;
        public ArrayList<Story> stories;

        public Walk(String title, String date, String author, String walk, ArrayList<Story> stories) {
            this.title = title;
            this.date = date;
            this.author = author;
            this.walk = walk;
            this.stories = stories;
        }
    }

    class Story {
        public String description;

        public Story(String description) {
            this.description = description;
        }
    }

    @GET("/Users/{id}.json")
    Call<User> getUserById(@Path("id") String id);

    @GET("/WalkInfo/{id}.json")
    Call<ArrayList<WalkInfo>> getUserWalksById(@Path("id") String id);

    @GET("/WalkMaps/{id}/{date}.json")
    Call<Walk> getWalkByDateAndId(@Path("id") String id, @Path("date") String date);

    @GET("/FriendsIds/{id}.json")
    Call<ArrayList<String>> getUserFriendsIds(@Path("id") String id);

    @PUT("/Users/{id}.json")
    Call<User> addUser(@Path("id") String id, @Body User user);

    @PUT("/WalkInfo/{id}/{num}.json")
    Call<WalkInfo> addWalkInfo(@Path("id") String id, @Path("num") int num, @Body WalkInfo walk);

    @PUT("/WalkMaps/{id}/{num}.json")
    Call<Walk> addWalk(@Path("id") String id, @Path("num") String num, @Body Walk walk);

    @PUT("/FriendsIds/{id}/{num}.json")
    Call<String> addFriendId(@Path("id") String id, @Path("num") int num, @Body String friendId);

    @PUT("/Users/{id}/friends/{num}.json")
    Call<Friend> addFriend(@Path("id") String id, @Path("num") int num, @Body Friend friend);

    @PATCH("/Users/{id}.json")
    Call<User> changeUserInformation(@Path("id") String id, @Body User user);
}

package ru.mail.z_team.network;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    class User {
        public String name;
        public int age;
        public ArrayList<Friend> friends;
        public String imageUrl;
        public String email;
        public String id;

        public User(String id, String email, String name) {
            this.name = name;
            this.id = id;
            this.email = email;
            this.friends = new ArrayList<>();
        }

        public User() {
        }
    }

    class Friend {
        public String name;
        public String id;

        public Friend(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public Friend() {
        }
    }

    class WalkAnnotation {
        public String title;
        public String authorName;
        public String authorId;
        public String date;

        public WalkAnnotation(String title, String date, String authorName, String authorId) {
            this.title = title;
            this.date = date;
            this.authorName = authorName;
            this.authorId = authorId;
        }
    }

    class Walk {
        public String title;
        public String authorName;
        public String date;
        public String walk;
        public ArrayList<Story> stories;

        public Walk(String title, String date, String authorName, String walk, ArrayList<Story> stories) {
            this.title = title;
            this.date = date;
            this.authorName = authorName;
            this.walk = walk;
            this.stories = stories;
        }
    }

    class Story {
        public String description;
        public ArrayList<String> images;
        public String place;
        public String point;
        public String id;

        public Story(String description) {
            this.description = description;
        }
    }

    @GET("/Users/{id}.json")
    Call<User> getUserById(@Path("id") String id);

    @GET("/WalkAnnotation/{id}.json")
    Call<ArrayList<WalkAnnotation>> getUserWalksAnnotationsById(@Path("id") String id);

    @GET("/WalkMaps/{id}/{date}.json")
    Call<Walk> getWalkByDateAndId(@Path("id") String id, @Path("date") String date);

    @GET("/WalkMaps/{id}.json")
    Call<Map<String,Walk>> getAllWalks(@Path("id") String id);

    @GET("/FriendsIds/{id}.json")
    Call<ArrayList<String>> getUserFriendsIds(@Path("id") String id);

    @GET("/Users/{id}/friends.json")
    Call<ArrayList<Friend>> getUserFriendsById(@Path("id") String id);

    @PUT("/Users/{id}.json")
    Call<User> addUser(@Path("id") String id, @Body User user);

    @PUT("/WalkAnnotation/{id}/{num}.json")
    Call<WalkAnnotation> addWalkInfo(@Path("id") String id, @Path("num") int num, @Body WalkAnnotation walk);

    @PUT("/WalkMaps/{id}/{date}.json")
    Call<Walk> addWalk(@Path("id") String id, @Path("date") String date, @Body Walk walk);

    @PUT("/FriendsIds/{id}/{num}.json")
    Call<String> addFriendId(@Path("id") String id, @Path("num") int num, @Body String friendId);

    @PUT("/Users/{id}/friends/{num}.json")
    Call<Friend> addFriend(@Path("id") String id, @Path("num") int num, @Body Friend friend);

    @PUT("/FriendRequest/{id}/{num}.json")
    Call<Friend> addFriendToFriendsRequest(@Path("id") String id, @Path("num") int num, @Body Friend friend);

    @GET("/FriendRequest/{id}/{num}.json")
    Call<Friend> getFriendsRequest(@Path("id") String id, @Path("num") int num);

    @DELETE("/FriendRequest/{id}/{num}.json")
    Call<Friend> deleteFriendRequest(@Path("id") String id, @Path("num") int num);

    @GET("/FriendRequest/{id}.json")
    Call<ArrayList<Friend>> getFriendRequestList(@Path("id") String id);

    @PATCH("/Users/{id}.json")
    Call<User> changeUserInformation(@Path("id") String id, @Body User user);

    @GET("WalkMaps/{id}/{date}/stories/{number}.json")
    Call<Story> getStory(@Path("id") String userId, @Path("date") String date, @Path("number") int number);
}

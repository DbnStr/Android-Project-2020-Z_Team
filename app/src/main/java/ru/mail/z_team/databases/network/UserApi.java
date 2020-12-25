package ru.mail.z_team.databases.network;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.mail.z_team.databases.DatabaseFriend;
import ru.mail.z_team.databases.DatabaseUser;
import ru.mail.z_team.databases.DatabaseWalkAnnotation;

public interface UserApi {

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
    Call<DatabaseUser> getUserById(@Path("id") String id);

    @GET("/WalkAnnotation/{id}.json")
    Call<ArrayList<DatabaseWalkAnnotation>> getUserWalksAnnotationsById(@Path("id") String id);

    @GET("/WalkMaps/{id}/{date}.json")
    Call<Walk> getWalkByDateAndId(@Path("id") String id, @Path("date") String date);

    @GET("/WalkMaps/{id}.json")
    Call<Map<String,Walk>> getAllWalks(@Path("id") String id);

    @GET("/FriendsIds/{id}.json")
    Call<ArrayList<String>> getUserFriendsIds(@Path("id") String id);

    @GET("/Users/{id}/friends.json")
    Call<ArrayList<DatabaseFriend>> getUserFriendsById(@Path("id") String id);

    @PUT("/Users/{id}.json")
    Call<DatabaseUser> addUser(@Path("id") String id, @Body DatabaseUser user);

    @PUT("/WalkAnnotation/{id}/{num}.json")
    Call<DatabaseWalkAnnotation> addWalkInfo(@Path("id") String id, @Path("num") int num, @Body DatabaseWalkAnnotation walk);

    @PUT("/WalkMaps/{id}/{date}.json")
    Call<Walk> addWalk(@Path("id") String id, @Path("date") String date, @Body Walk walk);

    @PUT("/FriendsIds/{id}/{num}.json")
    Call<String> addFriendId(@Path("id") String id, @Path("num") int num, @Body String friendId);

    @PUT("/Users/{id}/friends/{num}.json")
    Call<DatabaseFriend> addFriend(@Path("id") String id, @Path("num") int num, @Body DatabaseFriend friend);

    @PUT("/FriendRequest/{id}/{num}.json")
    Call<DatabaseFriend> addFriendToFriendsRequest(@Path("id") String id, @Path("num") int num, @Body DatabaseFriend friend);

    @GET("/FriendRequest/{id}/{num}.json")
    Call<DatabaseFriend> getFriendsRequest(@Path("id") String id, @Path("num") int num);

    @DELETE("/FriendRequest/{id}/{num}.json")
    Call<DatabaseFriend> deleteFriendRequest(@Path("id") String id, @Path("num") int num);

    @GET("/FriendRequest/{id}.json")
    Call<ArrayList<DatabaseFriend>> getFriendRequestList(@Path("id") String id);

    @PATCH("/Users/{id}.json")
    Call<DatabaseUser> changeUserInformation(@Path("id") String id, @Body DatabaseUser user);

    @GET("WalkMaps/{id}/{date}/stories/{number}.json")
    Call<Story> getStory(@Path("id") String userId, @Path("date") String date, @Path("number") int number);
}

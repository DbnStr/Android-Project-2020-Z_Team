package ru.mail.z_team.databases;

public class DatabaseWalkAnnotation {

    public String title;

    public String authorName;

    public String authorId;

    public String date;

    public DatabaseWalkAnnotation(String title, String date, String authorName, String authorId) {
        this.title = title;
        this.date = date;
        this.authorName = authorName;
        this.authorId = authorId;
    }
}

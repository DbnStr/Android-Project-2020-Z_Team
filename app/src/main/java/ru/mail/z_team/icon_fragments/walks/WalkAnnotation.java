package ru.mail.z_team.icon_fragments.walks;

import java.util.Date;

public class WalkAnnotation implements Comparable<WalkAnnotation>{
    private String title;
    private String author;
    private String authorId;
    private Date date;

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(WalkAnnotation o) {
        return -getDate().compareTo(o.getDate());
    }

}

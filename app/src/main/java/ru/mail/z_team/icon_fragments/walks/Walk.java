package ru.mail.z_team.icon_fragments.walks;

import java.util.Date;

public class Walk {
    String title;
    Date date;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
}

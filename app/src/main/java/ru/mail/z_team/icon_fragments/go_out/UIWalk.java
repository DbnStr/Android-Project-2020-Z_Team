package ru.mail.z_team.icon_fragments.go_out;

import com.mapbox.geojson.FeatureCollection;

import java.util.ArrayList;

import ru.mail.z_team.map.Story;

public class UIWalk {

    private String title;

    private FeatureCollection walkInfo;

    private ArrayList<Story> stories;

    UIWalk(final String title, final FeatureCollection walkInfo, ArrayList<Story> stories) {
        this.title = title;
        this.walkInfo = walkInfo;
        this.stories = stories;
    }

    public String getTitle() {
        return title;
    }

    public FeatureCollection getWalkInfo() {
        return walkInfo;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWalkInfo(FeatureCollection walkInfo) {
        this.walkInfo = walkInfo;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }
}

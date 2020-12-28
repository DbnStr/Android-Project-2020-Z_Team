package ru.mail.z_team.icon_fragments.walks;

import com.mapbox.geojson.FeatureCollection;

import java.util.ArrayList;

import ru.mail.z_team.map.Story;

public class Walk extends WalkAnnotation {
    FeatureCollection map;
    ArrayList<Story> stories;

    public void setMap(FeatureCollection map) {
        this.map = map;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }

    public FeatureCollection getMap() {
        return map;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }
}

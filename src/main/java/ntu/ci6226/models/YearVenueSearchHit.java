package ntu.ci6226.models;

import java.util.ArrayList;

/**
 * Created by alex on 27/3/16.
 */
public class YearVenueSearchHit {
    private String venue;
    private Integer year;
    private String[] titles;
    private float score;

    public String getVenue() {
        return venue;
    }

    public YearVenueSearchHit setVenue(String venue) {
        this.venue = venue;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public YearVenueSearchHit setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String[] getTitles() {
        return titles;
    }

    public YearVenueSearchHit setTitles(String[] titles) {
        this.titles = titles;
        return this;
    }

    public float getScore() {
        return score;
    }

    public YearVenueSearchHit setScore(float score) {
        this.score = score;
        return this;
    }
}

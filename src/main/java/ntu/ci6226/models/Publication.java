package ntu.ci6226.models;/*
 * Created on 07.06.2005
 */

/**
 * @author ley
 *         <p>
 *         created first in project xml5_coauthor_graph
 */
public class Publication {

    private String key;
    private String type;
    private String title;
    private Integer year;
    private String venue;
    private String[] authors;    // or editors

    public Publication()
    {

    }
    public Publication(String key, String type, String title, String year, String venue, String[] authors) {
        this.key = key;
        this.type = type;
        this.title = title;
        this.year = Integer.parseInt(year);
        this.venue = venue;
        this.authors = authors;
    }


    public String getKey() {
        return key;
    }

    public Publication setKey(String key) {
        this.key = key;
        return this;
    }

    public String getType() {
        return type;
    }

    public Publication setType(String type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Publication setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public Publication setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getVenue() {
        return venue;
    }

    public Publication setVenue(String venue) {
        this.venue = venue;
        return this;
    }

    public String[] getAuthors() {
        return authors;
    }

    public Publication setAuthors(String[] authors) {
        this.authors = authors;
        return this;
    }
}

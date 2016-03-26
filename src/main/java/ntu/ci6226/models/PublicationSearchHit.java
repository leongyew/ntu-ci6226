package ntu.ci6226.models;

/**
 * Created by alex on 27/3/16.
 */
public class PublicationSearchHit {
    private String key;
    private String type;
    private String title;
    private Integer year;
    private String venue;
    private String url;
    private String[] authors;    // or editors
    private float score;

    public String getKey() {
        return key;
    }

    public PublicationSearchHit setKey(String key) {
        this.key = key;
        this.url = "http://dblp.org/rec/" + key;
        return this;
    }

    public String getType() {
        return type;
    }

    public PublicationSearchHit setType(String type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PublicationSearchHit setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public PublicationSearchHit setYear(Integer year) {
        this.year = year;
        return this;
    }

    public String getVenue() {
        return venue;
    }

    public PublicationSearchHit setVenue(String venue) {
        this.venue = venue;
        return this;
    }

    public String[] getAuthors() {
        return authors;
    }

    public PublicationSearchHit setAuthors(String[] authors) {
        this.authors = authors;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public float getScore() {
        return score;
    }

    public PublicationSearchHit setScore(float score) {
        this.score = score;
        return this;
    }
}

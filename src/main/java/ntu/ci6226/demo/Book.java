package ntu.ci6226.demo;

/**
 * Created by alex on 16/3/16.
 */
public class Book {

    private Integer id;
    private String title;
    private String author;
    private String content;
    private Integer year;

    public String getContent() {
        return content;
    }

    public Book setContent(String content) {
        this.content = content;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Book setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public Book setYear(Integer year) {
        this.year = year;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public Book setId(Integer id) {
        this.id = id;
        return this;
    }
}

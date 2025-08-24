package vn.eiu.prototype;
import java.util.ArrayList;
public class Report extends Document {
    private String author;
    private int pages;
    public Report(String author, String title, String content, int pages) {
        super(title, content);
        this.author = author;
        this.pages = pages;
    }
    public void setAuthor(String a) { this.author = a; }
    public void setPages(int p) { this.pages = p; }
    @Override
    public Prototype clonePrototype() {
        Report clone = new Report(this.author, this.title, this.content, this.pages);
        clone.tags = new ArrayList<>(this.tags);
        return clone;
    }
    @Override
    public String getDescription() {
        return String.format("[Report] Author: %s | Pages: %d | %s", author, pages, super.getDescription());
    }
}

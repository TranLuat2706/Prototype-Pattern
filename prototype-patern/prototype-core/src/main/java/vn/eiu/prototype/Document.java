package vn.eiu.prototype;
import java.util.ArrayList;
import java.util.List;
public abstract class Document implements Prototype {
    protected String title;
    protected String content;
    protected List<String> tags;
    public Document(String title, String content) {
        this.title = title;
        this.content = content;
        this.tags = new ArrayList<>();
    }
    public void addTag(String tag) { tags.add(tag); }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    @Override
    public String getDescription() {
        return String.format("Title: %s | Content length: %d | Tags: %s",
                title, content == null ? 0 : content.length(), tags);
    }
    protected void copyCommonFieldsTo(Document target) {
        target.title = this.title;
        target.content = this.content;
        target.tags = new ArrayList<>(this.tags);
    }
}

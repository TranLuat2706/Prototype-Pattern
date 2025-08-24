package vn.eiu.prototype;
import java.util.ArrayList;
import java.util.List;
public class Resume extends Document {
    private String candidateName;
    private List<String> experiences;
    public Resume(String candidateName, String title, String content) {
        super(title, content);
        this.candidateName = candidateName;
        this.experiences = new ArrayList<>();
    }
    public void addExperience(String exp) { experiences.add(exp); }
    public void setCandidateName(String name) { this.candidateName = name; }
    @Override
    public Prototype clonePrototype() {
        Resume clone = new Resume(this.candidateName, this.title, this.content);
        clone.experiences = new ArrayList<>(this.experiences);
        clone.tags = new ArrayList<>(this.tags);
        return clone;
    }
    @Override
    public String getDescription() {
        return String.format("[Resume] Candidate: %s | %s | Experiences: %s",
                candidateName, super.getDescription(), experiences);
    }
    public void setExperiences(java.util.List<String> exps) { this.experiences = exps; }
}

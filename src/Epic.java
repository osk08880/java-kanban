import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String title, String details, TaskStatus status) {
        super(id, title, details, status);
    }

    public List<Integer> getSubTaskIds() {
        return subtaskIds;
    }

    public void setSubTaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubTaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubTaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}

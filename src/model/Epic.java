package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import util.TaskStatus;

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
        if (id == getId()) {
            throw new IllegalArgumentException("Epic нельзя добавить в самого себя в виде подзадачи");
        }
        subtaskIds.add(id);
    }

    public void removeSubTaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", viewed=" + isViewed() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}

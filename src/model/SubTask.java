package model;

import util.TaskStatus;
import util.TaskType;

public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String title, String details, TaskStatus status, int epicId) {
        super(id, title, details, status);
        if (id == epicId) {
            throw new IllegalArgumentException("SubTask не может быть своим же эпиком");
        }
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
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
        return "SubTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", viewed=" + isViewed() +
                ", epicId=" + epicId +
                '}';
    }
}

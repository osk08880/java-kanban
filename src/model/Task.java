package model;

import java.util.Objects;
import util.TaskStatus;

public class Task {
    private int id;
    private String title;
    private String details;
    private TaskStatus status;

    public Task(int id, String title, String details, TaskStatus status) {
        this.title = title;
        this.id = id;
        this.details = details;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(details, task.details) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, details, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                '}';
    }
}


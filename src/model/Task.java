package model;

import util.TaskStatus;
import util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String details;
    private TaskStatus status;
    private boolean viewed;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(int id, String title, String details, TaskStatus status) {
        this.title = title;
        this.id = id;
        this.details = details;
        this.status = status;
        this.viewed = false;
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

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime != null && duration != null ? startTime.plus(duration) : null;
    }

    public boolean intersects(Task other) {
        if (this.startTime == null || other.getStartTime() == null || this.duration == null || other.getDuration() == null) {
            return false;
        }
        LocalDateTime thisStart = this.startTime;
        LocalDateTime thisEnd = this.getEndTime();
        LocalDateTime otherStart = other.getStartTime();
        LocalDateTime otherEnd = other.getEndTime();
        return thisStart.isBefore(otherEnd) && otherStart.isBefore(thisEnd) &&
                !(thisStart.equals(otherEnd) || otherStart.equals(thisEnd));
    }

    // В классе Task
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", title='" + title + '\'' + ", details='" + details + '\'' + ", status=" + status + ", viewed=" + viewed + '}';
    }
}


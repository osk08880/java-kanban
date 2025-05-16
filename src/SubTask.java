public class SubTask extends Task {
    private int epicId;

    public SubTask(int id, String title, String details, TaskStatus status, int epicId) {
        super(id, title, details, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", details='" + getDetails() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}

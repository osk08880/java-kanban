package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;
        Task copy = copyOf(task);  // создаём копию
        copy.setViewed(true);
        history.add(copy);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private Task copyOf(Task task) {
        if (task instanceof SubTask subTask) {
            return new SubTask(
                    subTask.getId(),
                    subTask.getTitle(),
                    subTask.getDetails(),
                    subTask.getStatus(),
                    subTask.getEpicId()
            );
        } else if (task instanceof Epic epic) {
            Epic copyEpic = new Epic(
                    epic.getId(),
                    epic.getTitle(),
                    epic.getDetails(),
                    epic.getStatus()
            );
            copyEpic.setSubTaskIds(new ArrayList<>(epic.getSubTaskIds())); // копия списка
            return copyEpic;
        } else {
            return new Task(
                    task.getId(),
                    task.getTitle(),
                    task.getDetails(),
                    task.getStatus()
            );
        }
    }
}

package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Optional<Task> getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Optional<Epic> getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    Optional<SubTask> getSubTaskById(int id);

    void createSubTask(SubTask subtask);

    void updateSubTask(SubTask subtask);

    void deleteSubTaskById(int id);

    List<SubTask> getSubTasksOfEpic(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    LocalDateTime getEpicStartTime(int epicId);

    LocalDateTime getEpicEndTime(int epicId);

    Duration getEpicDuration(int epicId);
}

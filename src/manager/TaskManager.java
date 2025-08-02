package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    Optional<Task> findTaskById(int id);

    default Task getTaskById(int id) throws NotFoundException {
        return findTaskById(id).orElseThrow(() ->
                new NotFoundException("Задача с id " + id + " не найдена"));
    }

    Optional<Epic> findEpicById(int id);

    default Epic getEpicById(int id) throws NotFoundException {
        return findEpicById(id).orElseThrow(() ->
                new NotFoundException("Эпик с id " + id + " не найдена"));
    }

    Optional<SubTask> findSubTaskById(int id);

    default SubTask getSubTaskById(int id) throws NotFoundException {
        return findSubTaskById(id).orElseThrow(() ->
                new NotFoundException("Подзадача с id " + id + " не найдена"));
    }

    List<Task> getAllTasks();

    void deleteAllTasks();

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

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
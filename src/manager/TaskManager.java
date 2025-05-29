package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;


public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    void createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    SubTask getSubTaskById(int id);

    void createSubTask(SubTask subtask);

    void updateSubTask(SubTask subtask);

    void deleteSubTaskById(int id);

    List<SubTask> getSubTasksOfEpic(int epicId);

    List<Task> getHistory();
}

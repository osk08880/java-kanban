package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected HistoryManager historyManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        manager = createTaskManager();
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task(0, "Task1", "Details1", TaskStatus.NEW);
        Task task2 = new Task(0, "Task2", "Details2", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size(), "Должно быть две задачи");
        assertEquals(task1.getId(), tasks.get(0).getId(), "ID задачи task1 должен совпадать");
        assertEquals(task1.getTitle(), tasks.get(0).getTitle(), "Название задачи task1 должно совпадать");
        assertEquals(task2.getId(), tasks.get(1).getId(), "ID задачи task2 должен совпадать");
        assertEquals(task2.getTitle(), tasks.get(1).getTitle(), "Название задачи task2 должно совпадать");
    }

    @Test
    void testGetAllTasksEmpty() {
        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void testDeleteAllTasks() {
        Task task1 = new Task(0, "Task1", "Details1", TaskStatus.NEW);
        Task task2 = new Task(0, "Task2", "Details2", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
        assertTrue(manager.getHistory().isEmpty(), "История должна быть очищена");
    }

    @Test
    void testFindTaskById() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);

        Optional<Task> retrieved = manager.findTaskById(task.getId());
        assertTrue(retrieved.isPresent(), "Задача должна быть найдена");
        assertEquals(task.getId(), retrieved.get().getId(), "ID задачи должен совпадать");

        List<Task> history = manager.getHistory();
        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(task.getId(), history.get(0).getId(), "Задача должна быть в истории");
    }

    @Test
    void testFindTaskByIdNonExistent() {
        Optional<Task> retrieved = manager.findTaskById(775);
        assertEquals(Optional.empty(), retrieved, "Должен вернуть Optional.empty() для несуществующего ID");
    }

    @Test
    void testCreateTask() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 30));
        task.setDuration(Duration.ofHours(1));
        manager.createTask(task);

        assertTrue(task.getId() > 0, "Должен быть сгенерирован ID");

        Optional<Task> retrieved = manager.findTaskById(task.getId());
        assertTrue(retrieved.isPresent(), "Задача должна быть найдена по ID");
        assertEquals(task.getId(), retrieved.get().getId(), "ID задачи должен совпадать");
    }

    @Test
    void testCreateTaskWithIntersection() {
        Task task1 = new Task(0, "Task1", "Details1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 30));
        task1.setDuration(Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task(0, "Task2", "Details2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 50));
        task2.setDuration(Duration.ofHours(1));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2),
                "Должно быть исключение при пересечении задач по времени");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);

        Task updatedTask = new Task(task.getId(), "Updated Task", "Updated Details", TaskStatus.IN_PROGRESS);
        updatedTask.setStartTime(LocalDateTime.of(2025, 7, 17, 11, 0));
        updatedTask.setDuration(Duration.ofHours(2));
        manager.updateTask(updatedTask);

        Optional<Task> retrieved = manager.findTaskById(task.getId());
        assertTrue(retrieved.isPresent(), "Задача должна быть обновлена");
        assertEquals(updatedTask.getTitle(), retrieved.get().getTitle(), "Название задачи должно быть обновлено");
        assertEquals(updatedTask.getStatus(), retrieved.get().getStatus(), "Статус задачи должен быть обновлен");
    }

    @Test
    void testUpdateNonExistentTask() {
        Task task = new Task(777, "Task", "Details", TaskStatus.NEW);
        manager.updateTask(task);
        Optional<Task> retrieved = manager.findTaskById(777);
        assertEquals(Optional.empty(), retrieved, "Не должна быть добавлена несуществующая задача");
    }

    @Test
    void testDeleteTaskById() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);
        // Обязательно добавить задачу в историю через findTaskById для теста очистки истории
        manager.findTaskById(task.getId()).ifPresent(t -> {});

        manager.deleteTaskById(task.getId());
        Optional<Task> retrieved = manager.findTaskById(task.getId());
        assertEquals(Optional.empty(), retrieved, "Задача должна быть удалена");

        assertFalse(manager.getHistory().stream().anyMatch(t -> t.getId() == task.getId()),
                "Задача должна быть удалена из истории");
    }

    @Test
    void testGetTaskByIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> manager.getTaskById(133),
                "Должно выбросить NotFoundException для несуществующего ID");
    }

    // Аналогичные изменения для эпиков и подзадач

    @Test
    void testFindEpicById() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);

        Optional<Epic> retrieved = manager.findEpicById(epic.getId());
        assertTrue(retrieved.isPresent(), "Эпик должен быть найден");
        assertEquals(epic.getId(), retrieved.get().getId(), "ID эпика должен совпадать");
    }

    @Test
    void testFindEpicByIdNonExistent() {
        Optional<Epic> retrieved = manager.findEpicById(888);
        assertEquals(Optional.empty(), retrieved, "Должен вернуть Optional.empty() для несуществующего ID");
    }

    @Test
    void testFindSubTaskById() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask", "Details", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        Optional<SubTask> retrieved = manager.findSubTaskById(subTask.getId());
        assertTrue(retrieved.isPresent(), "Подзадача должна быть найдена");
        assertEquals(subTask.getId(), retrieved.get().getId(), "ID подзадачи должен совпадать");
    }

    @Test
    void testFindSubTaskByIdNonExistent() {
        Optional<SubTask> retrieved = manager.findSubTaskById(999);
        assertEquals(Optional.empty(), retrieved, "Должен вернуть Optional.empty() для несуществующего ID");
    }

    @Test
    void testGetEpicByIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> manager.getEpicById(133),
                "Должно выбросить NotFoundException для несуществующего ID");
    }

    @Test
    void testGetSubTaskByIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> manager.getSubTaskById(133),
                "Должно выбросить NotFoundException для несуществующего ID");
    }
}

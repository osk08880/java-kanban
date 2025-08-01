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
        Task task2 = new Task(0, "Task1", "Details2", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        manager.deleteAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи должны быть удалены");
        assertTrue(manager.getHistory().isEmpty(), "История должна быть очищена");
    }

    @Test
    void testGetTaskById() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);

        Optional<Task> retrievedOpt = manager.getTaskById(task.getId());
        assertTrue(retrievedOpt.isPresent(), "Задача должна быть найдена");
        Task retrieved = retrievedOpt.get();
        assertEquals(task.getId(), retrieved.getId(), "ID задачи должен совпадать");
        List<Task> history = manager.getHistory();
        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(task.getId(), history.get(0).getId(), "Задача должна быть в истории");
    }

    @Test
    void testGetTaskByIdNonExistent() {
        Optional<Task> retrievedOpt = manager.getTaskById(775);
        assertTrue(retrievedOpt.isEmpty(), "Должен вернуть Optional.empty() для несуществующего ID");
    }

    @Test
    void testCreateTask() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 30));
        task.setDuration(Duration.ofHours(1));
        manager.createTask(task);

        assertTrue(task.getId() > 0, "Должен быть сгенерирован ID");
        Optional<Task> retrievedOpt = manager.getTaskById(task.getId());
        assertTrue(retrievedOpt.isPresent(), "Задача должна быть найдена по ID");
        assertEquals(task.getId(), retrievedOpt.get().getId(), "ID задачи должен совпадать");
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

        Task updateTask = new Task(task.getId(), "Update Task", "Update Details", TaskStatus.IN_PROGRESS);
        updateTask.setStartTime(LocalDateTime.of(2025, 7, 17, 11, 0));
        updateTask.setDuration(Duration.ofHours(2));
        manager.updateTask(updateTask);

        Optional<Task> retrievedOpt = manager.getTaskById(task.getId());
        assertTrue(retrievedOpt.isPresent(), "Задача должна быть найдена");
        Task retrieved = retrievedOpt.get();
        assertEquals(updateTask.getTitle(), retrieved.getTitle(), "Название должно обновиться");
        assertEquals(updateTask.getDetails(), retrieved.getDetails(), "Описание должно обновиться");
        assertEquals(updateTask.getStatus(), retrieved.getStatus(), "Статус задачи должен обновиться");
        assertEquals(updateTask.getStartTime(), retrieved.getStartTime(), "Время начала должно обновиться");
        assertEquals(updateTask.getDuration(), retrieved.getDuration(), "Продолжительность должна обновиться");
    }

    @Test
    void testUpdateNonExistentTask() {
        Task task = new Task(777, "Task", "Details", TaskStatus.NEW);
        manager.updateTask(task);
        Optional<Task> retrievedOpt = manager.getTaskById(777);
        assertTrue(retrievedOpt.isEmpty(), "Не должна быть добавлена несуществующая задача");
    }

    @Test
    void testDeleteTaskById() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        manager.deleteTaskById(task.getId());
        Optional<Task> retrievedOpt = manager.getTaskById(task.getId());
        assertTrue(retrievedOpt.isEmpty(), "Задача должна быть удалена");
        assertFalse(manager.getHistory().stream().anyMatch(t -> t.getId() == task.getId()),
                "Задача должна быть удалена из истории");
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic(0, "Epic1", "Details1", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Epic2", "Details2", TaskStatus.IN_PROGRESS);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size(), "Должно быть два эпика");
        assertEquals(epic1.getId(), epics.get(0).getId(), "Список должен содержать epic1");
        assertEquals(epic2.getId(), epics.get(1).getId(), "Список должен содержать epic2");
    }

    @Test
    void testGetAllEpicsEmpty() {
        List<Epic> epics = manager.getAllEpics();
        assertTrue(epics.isEmpty(), "Список эпиков должен быть пустым");
    }

    @Test
    void testDeleteAllEpics() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask", "SubDetails", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        manager.deleteAllEpics();
        assertTrue(manager.getAllEpics().isEmpty(), "Все эпики должны быть удалены");
        assertTrue(manager.getAllSubTasks().isEmpty(), "Все подзадачи должны быть удалены");
        assertTrue(manager.getHistory().isEmpty(), "История должна быть очищена");
    }

    @Test
    void testGetEpicById() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(epic.getId(), retrieved.getId(), "ID эпика должен совпадать");
        List<Task> history = manager.getHistory();
        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(epic.getId(), history.get(0).getId(), "Эпик должен быть в истории");
    }

    @Test
    void testGetEpicByIdNonExistent() {
        Optional<Epic> retrievedOpt = manager.getEpicById(888);
        assertTrue(retrievedOpt.isEmpty(), "Должен вернуть Optional.empty() для несуществующего ID");
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);

        assertTrue(epic.getId() > 0, "ID должен быть сгенерирован");
        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден по ID");
        assertEquals(epic.getId(), retrievedOpt.get().getId(), "ID эпика должен совпадать");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);

        Epic updatedEpic = new Epic(epic.getId(), "Updated Epic", "Updated Details", TaskStatus.IN_PROGRESS);
        manager.updateEpic(updatedEpic);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(updatedEpic.getTitle(), retrieved.getTitle(), "Название должно обновиться");
        assertEquals(updatedEpic.getDetails(), retrieved.getDetails(), "Описание должно обновиться");
        assertEquals(updatedEpic.getStatus(), retrieved.getStatus(), "Статус должен обновиться");
    }

    @Test
    void testUpdateNonExistentEpic() {
        Epic epic = new Epic(888, "Epic", "Details", TaskStatus.NEW);
        manager.updateEpic(epic);
        Optional<Epic> retrievedOpt = manager.getEpicById(888);
        assertTrue(retrievedOpt.isEmpty(), "Несуществующий эпик не должен быть добавлен");
    }

    @Test
    void testDeleteEpicById() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask", "SubDetails", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);
        manager.getEpicById(epic.getId());

        manager.deleteEpicById(epic.getId());
        Optional<Epic> epicOpt = manager.getEpicById(epic.getId());
        Optional<SubTask> subTaskOpt = manager.getSubTaskById(subTask.getId());
        assertTrue(epicOpt.isEmpty(), "Эпик должен быть удален");
        assertTrue(subTaskOpt.isEmpty(), "Подзадача должна быть удалена");
        assertFalse(manager.getHistory().stream().anyMatch(t -> t.getId() == epic.getId()),
                "Эпик должен быть удален из истории");
        assertFalse(manager.getHistory().stream().anyMatch(t -> t.getId() == subTask.getId()),
                "Подзадача должна быть удалена из истории");
    }

    @Test
    void testGetAllSubTasks() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.IN_PROGRESS, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<SubTask> subTasks = manager.getAllSubTasks();
        assertEquals(2, subTasks.size(), "Должно быть две подзадачи");
        assertEquals(subTask1.getId(), subTasks.get(0).getId(), "Список должен содержать subTask1");
        assertEquals(subTask2.getId(), subTasks.get(1).getId(), "Список должен содержать subTask2");
    }

    @Test
    void testGetAllSubTasksEmpty() {
        List<SubTask> subTasks = manager.getAllSubTasks();
        assertTrue(subTasks.isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void testDeleteAllSubTasks() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.IN_PROGRESS, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.getSubTaskById(subTask1.getId());
        manager.getSubTaskById(subTask2.getId());

        manager.deleteAllSubTasks();
        assertTrue(manager.getAllSubTasks().isEmpty(), "Все подзадачи должны быть удалены");
        assertTrue(manager.getHistory().isEmpty(), "История должна быть очищена");
        Optional<Epic> epicOpt = manager.getEpicById(epic.getId());
        assertTrue(epicOpt.isPresent(), "Эпик должен существовать");
        assertTrue(epicOpt.get().getSubTaskIds().isEmpty(), "Эпик не должен содержать подзадачи");
    }

    @Test
    void testGetSubTaskById() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask", "Details", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        Optional<SubTask> retrievedOpt = manager.getSubTaskById(subTask.getId());
        assertTrue(retrievedOpt.isPresent(), "Подзадача должна быть найдена");
        SubTask retrieved = retrievedOpt.get();
        assertEquals(subTask.getId(), retrieved.getId(), "ID подзадачи должен совпадать");
        List<Task> history = manager.getHistory();
        assertFalse(history.isEmpty(), "История не должна быть пустой");
        assertEquals(subTask.getId(), history.get(0).getId(), "Подзадача должна быть в истории");
    }

    @Test
    void testGetSubTaskByIdNonExistent() {
        Optional<SubTask> retrievedOpt = manager.getSubTaskById(999);
        assertTrue(retrievedOpt.isEmpty(), "Должен вернуть Optional.empty() для несуществующего ID");
    }

    @Test
    void testCreateSubTask() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask", "SubDetails", TaskStatus.NEW, epic.getId());
        subTask.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 0));
        subTask.setDuration(Duration.ofHours(1));
        manager.createSubTask(subTask);

        assertTrue(subTask.getId() > 0, "ID должен быть сгенерирован");
        Optional<SubTask> retrievedOpt = manager.getSubTaskById(subTask.getId());
        assertTrue(retrievedOpt.isPresent(), "Подзадача должна быть найдена по ID");
        assertEquals(subTask.getId(), retrievedOpt.get().getId(), "ID подзадачи должен совпадать");
        Optional<Epic> epicOpt = manager.getEpicById(epic.getId());
        assertTrue(epicOpt.isPresent(), "Эпик должен быть найден");
        assertTrue(epicOpt.get().getSubTaskIds().contains(subTask.getId()), "Подзадача должна быть добавлена в эпик");
    }

    @Test
    void testCreateSubTaskWithNonExistentEpic() {
        SubTask subTask = new SubTask(0, "SubTask", "SubDetails", TaskStatus.NEW, 999);
        assertThrows(IllegalArgumentException.class, () -> manager.createSubTask(subTask),
                "Должно выбросить исключение при создании подзадачи с несуществующим эпиком");
    }

    @Test
    void testCreateSubTaskWithIntersection() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        subTask1.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 0));
        subTask1.setDuration(Duration.ofHours(1));
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.NEW, epic.getId());
        subTask2.setStartTime(LocalDateTime.of(2025, 7, 17, 10, 30));
        subTask2.setDuration(Duration.ofHours(1));

        assertThrows(IllegalArgumentException.class, () -> manager.createSubTask(subTask2),
                "Должно выбросить исключение при пересечении времени");
    }

    @Test
    void testUpdateSubTask() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask", "SubDetails", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        SubTask updatedSubTask = new SubTask(subTask.getId(), "Updated SubTask", "Updated SubDetails",
                TaskStatus.IN_PROGRESS, epic.getId());
        updatedSubTask.setStartTime(LocalDateTime.of(2025, 7, 17, 12, 0));
        updatedSubTask.setDuration(Duration.ofHours(2));
        manager.updateSubTask(updatedSubTask);

        Optional<SubTask> retrievedOpt = manager.getSubTaskById(subTask.getId());
        assertTrue(retrievedOpt.isPresent(), "Подзадача должна быть найдена");
        SubTask retrieved = retrievedOpt.get();
        assertEquals(updatedSubTask.getTitle(), retrieved.getTitle(), "Название должно обновиться");
        assertEquals(updatedSubTask.getDetails(), retrieved.getDetails(), "Описание должно обновиться");
        assertEquals(updatedSubTask.getStatus(), retrieved.getStatus(), "Статус должен обновиться");
        assertEquals(updatedSubTask.getStartTime(), retrieved.getStartTime(), "Время начала должно обновиться");
        assertEquals(updatedSubTask.getDuration(), retrieved.getDuration(), "Длительность должна обновиться");
    }

    @Test
    void testUpdateNonExistentSubTask() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask = new SubTask(999, "SubTask", "SubDetails", TaskStatus.NEW, epic.getId());
        manager.updateSubTask(subTask);
        Optional<SubTask> retrievedOpt = manager.getSubTaskById(999);
        assertTrue(retrievedOpt.isEmpty(), "Несуществующая подзадача не должна быть добавлена");
    }

    @Test
    void testGetSubTasksOfEpic() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.IN_PROGRESS, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        List<SubTask> subTasks = manager.getSubTasksOfEpic(epic.getId());
        assertEquals(2, subTasks.size(), "Должно быть две подзадачи");
        assertEquals(subTask1.getId(), subTasks.get(0).getId(), "Список должен содержать subTask1");
        assertEquals(subTask2.getId(), subTasks.get(1).getId(), "Список должен содержать subTask2");
    }

    @Test
    void testGetSubTasksOfNonExistentEpic() {
        List<SubTask> subTasks = manager.getSubTasksOfEpic(999);
        assertTrue(subTasks.isEmpty(), "Должен вернуть пустой список для несуществующего эпика");
    }

    @Test
    void testGetHistory() {
        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask(0, "SubTask", "Details", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subTask.getId());

        List<Task> history = manager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три записи");
        assertEquals(task.getId(), history.get(0).getId(), "Первая задача в истории должна быть Task");
        assertEquals(epic.getId(), history.get(1).getId(), "Вторая задача в истории должна быть Epic");
        assertEquals(subTask.getId(), history.get(2).getId(), "Третья задача в истории должна быть SubTask");
    }

    @Test
    void testGetHistoryEmpty() {
        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void testGetPrioritizedTasks() {
        Task task1 = new Task(0, "Task1", "Details1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 16, 10, 0));
        task1.setDuration(Duration.ofHours(1));
        Task task2 = new Task(0, "Task2", "Details2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 16, 8, 0));
        task2.setDuration(Duration.ofHours(1));
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size(), "Должно быть две задачи");
        assertEquals(task2.getId(), prioritized.get(0).getId(), "Задача с более ранним временем должна быть первой");
    }

    @Test
    void testGetPrioritizedTasksEmpty() {
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertTrue(prioritized.isEmpty(), "Список приоритетных задач должен быть пустым");
    }

    @Test
    void testEpicStatusAllNew() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(TaskStatus.NEW, retrieved.getStatus(), "Статус эпика должен быть NEW, если все подзадачи NEW");
    }

    @Test
    void testEpicStatusAllDone() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.DONE, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.DONE, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(TaskStatus.DONE, retrieved.getStatus(), "Статус эпика должен быть DONE, если все подзадачи DONE");
    }

    @Test
    void testEpicStatusMixedNewAndDone() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.DONE, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(TaskStatus.IN_PROGRESS, retrieved.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если подзадачи NEW и DONE");
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.IN_PROGRESS, epic.getId());
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(TaskStatus.IN_PROGRESS, retrieved.getStatus(),
                "Статус эпика должен быть IN_PROGRESS, если есть подзадача IN_PROGRESS");
    }

    @Test
    void testEpicStatusEmptySubTasks() {
        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);

        Optional<Epic> retrievedOpt = manager.getEpicById(epic.getId());
        assertTrue(retrievedOpt.isPresent(), "Эпик должен быть найден");
        Epic retrieved = retrievedOpt.get();
        assertEquals(TaskStatus.NEW, retrieved.getStatus(), "Статус эпика должен быть NEW, если нет подзадач");
    }
}
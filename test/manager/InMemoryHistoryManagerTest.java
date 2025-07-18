package manager;

import model.Task;
import util.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testAddTask() {
        Task task = new Task(1, "Task", "Details", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task.getId(), history.get(0).getId(), "ID задачи должен совпадать");
        assertTrue(history.get(0).isViewed(), "Задача должна быть помечена как просмотренная");
    }

    @Test
    void testAddNullTask() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty(), "Добавление null не должно изменять историю");
    }

    @Test
    void testAddDuplicateTask() {
        Task task = new Task(1, "Task", "Details", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дублирующаяся задача должна быть только одна в истории");
        assertEquals(task.getId(), history.get(0).getId(), "Последняя добавленная задача должна быть в конце");
    }

    @Test
    void testRemoveTaskFromStart() {
        Task task1 = new Task(1, "Task1", "Details1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "Details2", TaskStatus.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна остаться 1 задача");
        assertEquals(task2.getId(), history.get(0).getId(), "Оставшаяся задача должна быть task2");
    }

    @Test
    void testRemoveTaskFromMiddle() {
        Task task1 = new Task(1, "Task1", "Details1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "Details2", TaskStatus.NEW);
        Task task3 = new Task(3, "Task3", "Details3", TaskStatus.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории должно остаться 2 задачи");
        assertEquals(task1.getId(), history.get(0).getId(), "Первая задача должна быть task1");
        assertEquals(task3.getId(), history.get(1).getId(), "Вторая задача должна быть task3");
    }

    @Test
    void testRemoveTaskFromEnd() {
        Task task1 = new Task(1, "Task1", "Details1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "Details2", TaskStatus.NEW);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна остаться 1 задача");
        assertEquals(task1.getId(), history.get(0).getId(), "Оставшаяся задача должна быть task1");
    }

    @Test
    void testRemoveNonExistentTask() {
        historyManager.remove(999);
        assertTrue(historyManager.getHistory().isEmpty(), "Удаление несуществующего ID не должно изменять историю");
    }

    @Test
    void testGetHistoryEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void testGetHistoryReturnsCopies() {
        Task task = new Task(1, "Task", "Details", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        Task saved = history.get(0);
        saved.setTitle("Modified");

        List<Task> newHistory = historyManager.getHistory();
        assertEquals("Task", newHistory.get(0).getTitle(), "История должна возвращать копии задач");
    }
}
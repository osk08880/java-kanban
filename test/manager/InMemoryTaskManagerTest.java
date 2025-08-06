package manager;

import model.Task;
import util.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void testHasIntersection() {
        Task task1 = new Task(0, "Task1", "Details1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 16, 10, 0));
        task1.setDuration(Duration.ofHours(1));
        manager.createTask(task1);

        Task task2 = new Task(0, "Task2", "Details2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 16, 10, 30));
        task2.setDuration(Duration.ofHours(1));
        assertTrue(manager.hasIntersection(task2), "Задачи должны пересекаться");

        task2.setStartTime(LocalDateTime.of(2025, 7, 16, 11, 0));
        task2.setDuration(Duration.ofHours(1));
        assertFalse(manager.hasIntersection(task2), "Задачи не должны пересекаться");
    }

    @Test
    public void testAddAndGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Test task", "details", TaskStatus.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task.getId(), history.get(0).getId(), "Задача в истории должна совпадать по ID");
    }
}
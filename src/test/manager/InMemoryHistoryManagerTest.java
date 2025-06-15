package manager;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void tasksAddedToHistoryShouldPreservePreviousState() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task original = new Task(1, "Оригинальное название", "Оригинальное описание", TaskStatus.NEW);

        historyManager.add(original);

        original.setTitle("Измененное название");
        original.setDetails("Измененное описание");
        original.setStatus(TaskStatus.DONE);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        Task saved = history.get(0);

        assertEquals("Оригинальное название", saved.getTitle(), "Название должно быть неизменным");
        assertEquals("Оригинальное описание", saved.getDetails(), "Описание не должно изменяться");
        assertEquals(TaskStatus.NEW, saved.getStatus(), "Статус не должен изменяться");
        assertTrue(saved.isViewed(), "Задача в истории должна быть помечена как просмотренная");
    }


    @Test
    void addShouldAddTaskToEndAndMarkViewed() {
        Task task1 = new Task(1, "T1", "Desc", TaskStatus.NEW);
        Task task2 = new Task(2, "T2", "Desc", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
        assertTrue(history.get(0).isViewed());
        assertEquals(task2.getId(), history.get(1).getId());
        assertTrue(history.get(1).isViewed());
    }

    @Test
    void removeShouldDeleteTaskById() {
        Task task1 = new Task(1, "T1", "Desc", TaskStatus.NEW);
        Task task2 = new Task(2, "T2", "Desc", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2.getId(), history.get(0).getId());
    }

    @Test
    void linkedListIntegrityAfterMultipleOperations() {
        for (int i = 1; i <= 7; i++) {
            historyManager.add(new Task(i, "T" + i, "Desc", TaskStatus.NEW));
        }
        historyManager.remove(5);
        List<Task> history = historyManager.getHistory();
        assertEquals(6, history.size());
        assertFalse(history.stream().anyMatch(t -> t.getId() == 5));
    }

    @Test
    void addShouldMoveExistingTaskToEnd() {
        Task task1 = new Task(1, "T1", "Desc", TaskStatus.NEW);
        Task task2 = new Task(2, "T2", "Desc", TaskStatus.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2.getId(), history.get(0).getId());
        assertEquals(task1.getId(), history.get(1).getId());
    }

    @Test
    void addShouldIgnoreNull() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void removeNonexistentIdShouldNotThrow() {
        assertDoesNotThrow(() -> historyManager.remove(42));
    }

    @Test
    void getHistoryShouldReturnCopies() {
        Task original = new Task(1, "T1", "Desc", TaskStatus.NEW);
        historyManager.add(original);

        List<Task> history = historyManager.getHistory();
        Task saved = history.get(0);
        saved.setTitle("Modified");

        List<Task> historyAfter = historyManager.getHistory();
        assertEquals("T1", historyAfter.get(0).getTitle());
    }

    @Test
    void removeFirstAndLastAndOnlyTask() {
        Task task = new Task(1, "T1", "Desc", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }
}


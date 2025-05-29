package manager;

import model.Task;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
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
}

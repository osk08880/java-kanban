package manager;

import model.Task;
import model.Epic;
import model.SubTask;
import util.TaskStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void shouldAddAndFindTasksOfDifferentTypes() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager(historyManager);

        Task task = new Task(0, "Task1", "Details Task1", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(0, "Epic1", "Details Epic1", TaskStatus.IN_PROGRESS);
        manager.createEpic(epic);

        SubTask subTask = new SubTask(0, "SubTask1", "Details SubTask1", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        assertTrue(task.getId() > 0);
        assertTrue(epic.getId() > 0);
        assertTrue(subTask.getId() > 0);

        assertEquals(task, manager.getTaskById(task.getId()));
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertEquals(subTask, manager.getSubTaskById(subTask.getId()));

        assertNull(manager.getTaskById(epic.getId()));
        assertNull(manager.getEpicById(task.getId()));
        assertNull(manager.getSubTaskById(task.getId()));

        assertEquals(3, manager.getHistory().size());
        assertTrue(manager.getHistory().contains(task));
        assertTrue(manager.getHistory().contains(epic));
        assertTrue(manager.getHistory().contains(subTask));
    }

    @Test
    void shouldHandleTasksWithGivenAndGeneratedIdsWithoutConflict() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager(historyManager);

        Task task1 = new Task(10, "Task 10", "Details Task 10", TaskStatus.NEW);
        manager.createTask(task1);
        assertEquals(10, task1.getId());

        Task task2 = new Task(0, "Task auto", "Details Task auto", TaskStatus.NEW);
        manager.createTask(task2);
        assertEquals(11, task2.getId());

        Task task3 = new Task(12, "Task 12", "Details Task 12", TaskStatus.NEW);
        manager.createTask(task3);
        assertEquals(12, task3.getId());

        Task task4 = new Task(0, "Task auto 2", "Details Task auto 2", TaskStatus.NEW);
        manager.createTask(task4);
        assertEquals(13, task4.getId());

        assertEquals(task1, manager.getTaskById(10));
        assertEquals(task2, manager.getTaskById(11));
        assertEquals(task3, manager.getTaskById(12));
        assertEquals(task4, manager.getTaskById(13));
    }

    @Test
    void shouldNotChangeEpicFieldsWhenAddedToManager() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager(historyManager);

        Epic originalEpic = new Epic(0, "Test Epic", "Epic details", TaskStatus.NEW);

        int originalId = originalEpic.getId();
        String originalTitle = originalEpic.getTitle();
        String originalDetails = originalEpic.getDetails();
        TaskStatus originalStatus = originalEpic.getStatus();

        assertTrue(originalEpic.getSubTaskIds().isEmpty());

        manager.createEpic(originalEpic);

        assertEquals(originalTitle, originalEpic.getTitle(), "Название остается прежним");
        assertEquals(originalDetails, originalEpic.getDetails(), "Описание остается прежним");
        assertEquals(originalStatus, originalEpic.getStatus(), "Статус остается прежним");

        assertTrue(originalEpic.getId() != 0, "Id должен быть установлен менеджером (не 0)");

        assertTrue(originalEpic.getSubTaskIds().isEmpty(), "Список подзадач должен остаться пустым");
    }

    @Test
    void deletingSubTaskRemovesFromEpicAndHistory() {
        HistoryManager history = new InMemoryHistoryManager();
        InMemoryTaskManager manager = new InMemoryTaskManager(history);

        Epic epic = new Epic(0, "EpicTest", "EpicDetails", TaskStatus.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask(0, "SubTest", "SubDetails", TaskStatus.NEW, epic.getId());
        manager.createSubTask(subTask);

        assertTrue(epic.getSubTaskIds().contains(subTask.getId()));

        manager.deleteSubTaskById(subTask.getId());

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertFalse(updatedEpic.getSubTaskIds().contains(subTask.getId()), "Подзадача должна быть удалена");
        assertNull(manager.getSubTaskById(subTask.getId()), "Подзадача должна отсутствовать в менеджере");
        assertFalse(manager.getHistory().contains(subTask), "Подзадача должна быть удалена из истории");
    }
}
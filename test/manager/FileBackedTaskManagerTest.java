package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import util.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void shouldSaveAndLoadEmptyManager() throws IOException {
        File file = File.createTempFile("empty", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubTasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(0, "Epic", "Epic details", TaskStatus.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask(0, "Subtask", "Sub details", TaskStatus.DONE, epic.getId());
        manager.createSubTask(subTask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(1, loaded.getAllSubTasks().size());

        Task loadedTask = loaded.getAllTasks().get(0);
        assertEquals(task.getTitle(), loadedTask.getTitle());

        Epic loadedEpic = loaded.getAllEpics().get(0);
        assertEquals(epic.getTitle(), loadedEpic.getTitle());

        SubTask loadedSub = loaded.getAllSubTasks().get(0);
        assertEquals(subTask.getTitle(), loadedSub.getTitle());
        assertEquals(epic.getId(), loadedSub.getEpicId());
    }

    @Test
    void shouldSaveFileWithCorrectFormat() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(0, "Epic", "Epic details", TaskStatus.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask(0, "Subtask", "Sub details", TaskStatus.DONE, epic.getId());
        manager.createSubTask(subTask);

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());

        assertEquals("id,type,name,status,details,epic", lines.get(0));

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",", -1);
            assertEquals(6, parts.length, "Неверное количество колонок в строке: " + lines.get(i));
            assertDoesNotThrow(() -> Integer.parseInt(parts[0]));
            assertTrue(java.util.Arrays.stream(util.TaskType.values())
                    .anyMatch(t -> t.name().equals(parts[1])), "Неверный тип задачи: " + parts[1]);
        }
    }
}

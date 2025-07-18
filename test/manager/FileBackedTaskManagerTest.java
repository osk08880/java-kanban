package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @TempDir
    File tempDir;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new File(tempDir, "tasks.csv"), new InMemoryHistoryManager());
    }

    @Test
    void testSaveAndLoadEmptyManager() throws IOException {
        File file = new File(tempDir, "empty.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());
        assertTrue(loaded.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loaded.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loaded.getAllSubTasks().isEmpty(), "Список подзадач должен быть пустым");
        assertTrue(loaded.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void testSaveAndLoadTasksWithTime() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.of(2025, 7, 16, 10, 0));
        task.setDuration(Duration.ofHours(1));
        manager.createTask(task);

        Epic epic = new Epic(0, "Epic", "Epic details", TaskStatus.NEW);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask(0, "Subtask1", "Sub details1", TaskStatus.DONE, epic.getId());
        subTask1.setStartTime(LocalDateTime.of(2025, 7, 16, 12, 0));
        subTask1.setDuration(Duration.ofHours(2));
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask(0, "Subtask2", "Sub details2", TaskStatus.NEW, epic.getId());
        subTask2.setStartTime(LocalDateTime.of(2025, 7, 16, 11, 0));
        subTask2.setDuration(Duration.ofHours(1));
        manager.createSubTask(subTask2);

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

        assertEquals(1, loaded.getAllTasks().size(), "Должна быть 1 задача");
        assertEquals(1, loaded.getAllEpics().size(), "Должен быть 1 эпик");
        assertEquals(2, loaded.getAllSubTasks().size(), "Должно быть 2 подзадачи");

        Optional<Task> loadedTaskOpt = loaded.getTaskById(task.getId());
        assertTrue(loadedTaskOpt.isPresent(), "Задача должна быть найдена");
        Task loadedTask = loadedTaskOpt.get();
        assertEquals(task.getTitle(), loadedTask.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Время начала задачи должно совпадать");
        assertEquals(task.getDuration(), loadedTask.getDuration(), "Длительность задачи должна совпадать");

        Optional<Epic> loadedEpicOpt = loaded.getEpicById(epic.getId());
        assertTrue(loadedEpicOpt.isPresent(), "Эпик должен быть найден");
        Epic loadedEpic = loadedEpicOpt.get();
        assertEquals(epic.getTitle(), loadedEpic.getTitle(), "Название эпика должно совпадать");
        assertEquals(subTask2.getStartTime(), loaded.getEpicStartTime(epic.getId()), "Время начала эпика должно быть минимальным");
        assertEquals(Duration.ofHours(3), loaded.getEpicDuration(epic.getId()), "Длительность эпика должна быть суммой");
        assertEquals(subTask1.getEndTime(), loaded.getEpicEndTime(epic.getId()), "Время окончания эпика должно быть максимальным");

        Optional<SubTask> loadedSub1Opt = loaded.getSubTaskById(subTask1.getId());
        assertTrue(loadedSub1Opt.isPresent(), "Подзадача 1 должна быть найдена");
        SubTask loadedSub1 = loadedSub1Opt.get();
        assertEquals(subTask1.getTitle(), loadedSub1.getTitle(), "Название подзадачи 1 должно совпадать");
        assertEquals(epic.getId(), loadedSub1.getEpicId(), "ID эпика подзадачи 1 должен совпадать");
        assertEquals(subTask1.getStartTime(), loadedSub1.getStartTime(), "Время начала подзадачи 1 должно совпадать");
        assertEquals(subTask1.getDuration(), loadedSub1.getDuration(), "Длительность подзадачи 1 должна совпадать");

        Optional<SubTask> loadedSub2Opt = loaded.getSubTaskById(subTask2.getId());
        assertTrue(loadedSub2Opt.isPresent(), "Подзадача 2 должна быть найдена");
        SubTask loadedSub2 = loadedSub2Opt.get();
        assertEquals(subTask2.getTitle(), loadedSub2.getTitle(), "Название подзадачи 2 должно совпадать");
        assertEquals(epic.getId(), loadedSub2.getEpicId(), "ID эпика подзадачи 2 должен совпадать");
        assertEquals(subTask2.getStartTime(), loadedSub2.getStartTime(), "Время начала подзадачи 2 должно совпадать");
        assertEquals(subTask2.getDuration(), loadedSub2.getDuration(), "Длительность подзадачи 2 должна совпадать");
    }

    @Test
    void testLoadFromNonExistentFile() {
        File file = new File(tempDir, "nonexistent.csv");
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager()),
                "Должно выбросить исключение при загрузке из несуществующего файла");
    }

    @Test
    void testGetEpicStartTimeDurationAndEndTime() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Epic epic = new Epic(0, "Epic", "Details", TaskStatus.NEW);
        manager.createEpic(epic);
        SubTask subTask1 = new SubTask(0, "SubTask1", "Details1", TaskStatus.NEW, epic.getId());
        subTask1.setStartTime(LocalDateTime.of(2025, 7, 16, 10, 0));
        subTask1.setDuration(Duration.ofHours(1));
        SubTask subTask2 = new SubTask(0, "SubTask2", "Details2", TaskStatus.NEW, epic.getId());
        subTask2.setStartTime(LocalDateTime.of(2025, 7, 16, 12, 0));
        subTask2.setDuration(Duration.ofHours(2));
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        assertEquals(subTask1.getStartTime(), manager.getEpicStartTime(epic.getId()),
                "Время начала эпика должно быть равно минимальному времени подзадач");
        assertEquals(Duration.ofHours(3), manager.getEpicDuration(epic.getId()),
                "Длительность эпика должна быть суммой длительностей подзадач");
        assertEquals(subTask2.getEndTime(), manager.getEpicEndTime(epic.getId()),
                "Время окончания эпика должно быть равно максимальному времени окончания подзадач");
    }

    @Test
    void testSaveAndLoadHistory() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());

        Task task = new Task(0, "Task", "Details", TaskStatus.NEW);
        manager.createTask(task);
        Optional<Task> taskOpt = manager.getTaskById(task.getId());
        assertTrue(taskOpt.isPresent(), "Задача должна быть найдена");
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());
        List<Task> history = loaded.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 запись");
        assertEquals(task.getId(), history.get(0).getId(), "ID задачи в истории должен совпадать");
        assertEquals(task.getTitle(), history.get(0).getTitle(), "Название задачи в истории должно совпадать");
    }
}
package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskConverterCsv;
import util.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,details,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(TaskConverterCsv.toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(TaskConverterCsv.toString(epic) + "\n");
            }
            for (SubTask subtask : getAllSubTasks()) {
                writer.write(TaskConverterCsv.toString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + e.getMessage(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) continue;
                Task task = TaskConverterCsv.fromString(line);
                switch (task.getType()) {
                    case TASK -> manager.createTask(task);
                    case EPIC -> manager.createEpic((Epic) task);
                    case SUBTASK -> manager.createSubTask((SubTask) task);
                    default -> throw new IllegalStateException("Неизвестный тип задачи: " + task.getType());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + e.getMessage(), e);
        }
        return manager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createSubTask(SubTask subtask) {
        super.createSubTask(subtask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    public static void main(String[] args) {
        File file = new File("tasks.csv");
        HistoryManager historyManager = new InMemoryHistoryManager();

        FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);

        Task task1 = new Task(0, "Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(0, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Эпик 2", "Описание", TaskStatus.IN_PROGRESS);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        SubTask sub1 = new SubTask(0, "Подзадача 1", "Описание", TaskStatus.DONE, epic1.getId());
        manager.createSubTask(sub1);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

        System.out.println("Задачи:");
        for (Task t : loadedManager.getAllTasks()) {
            System.out.println(t);
        }

        System.out.println("\nЭпики:");
        for (Epic e : loadedManager.getAllEpics()) {
            System.out.println(e);
        }

        System.out.println("\nПодзадачи:");
        for (SubTask s : loadedManager.getAllSubTasks()) {
            System.out.println(s);
        }
    }
}




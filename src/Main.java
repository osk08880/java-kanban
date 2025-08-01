import manager.*;
import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        File file = new File("tasks.csv");
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager manager = new FileBackedTaskManager(file, historyManager);

        try {
            Task task1 = new Task(0, "Задача №1", "Описание задачи 1", TaskStatus.NEW);
            task1.setStartTime(LocalDateTime.of(2025, 7, 18, 10, 0));
            task1.setDuration(Duration.ofHours(1));
            Task task2 = new Task(0, "Задача №2", "Описание задачи 2", TaskStatus.IN_PROGRESS);
            task2.setStartTime(LocalDateTime.of(2025, 7, 18, 12, 0));
            task2.setDuration(Duration.ofHours(2));
            manager.createTask(task1);
            manager.createTask(task2);

            Epic epic1 = new Epic(0, "Эпик №1", "Три подзадачи", TaskStatus.NEW);
            Epic epic2 = new Epic(0, "Эпик №2", "Без подзадач", TaskStatus.NEW);
            manager.createEpic(epic1);
            manager.createEpic(epic2);

            SubTask subTask1 = new SubTask(0, "Подзадача №1", "К эпик №1", TaskStatus.NEW, epic1.getId());
            subTask1.setStartTime(LocalDateTime.of(2025, 7, 18, 14, 0));
            subTask1.setDuration(Duration.ofHours(1));
            SubTask subTask2 = new SubTask(0, "Подзадача №2", "К эпик №1", TaskStatus.NEW, epic1.getId());
            subTask2.setStartTime(LocalDateTime.of(2025, 7, 18, 15, 30));
            subTask2.setDuration(Duration.ofHours(1));
            SubTask subTask3 = new SubTask(0, "Подзадача №3", "К эпик №1", TaskStatus.DONE, epic1.getId());
            subTask3.setStartTime(LocalDateTime.of(2025, 7, 18, 17, 0));
            subTask3.setDuration(Duration.ofHours(1));
            manager.createSubTask(subTask1);
            manager.createSubTask(subTask2);
            manager.createSubTask(subTask3);

            printAll(manager);

            System.out.println("\nЗапрос Подзадачи №2 (id=" + subTask2.getId() + ")");
            manager.getSubTaskById(subTask2.getId());
            printHistory(manager);

            System.out.println("\nЗапрос Задачи №1 (id=" + task1.getId() + ")");
            manager.getTaskById(task1.getId());
            printHistory(manager);

            System.out.println("\nЗапрос Задачи №2 (id=" + task2.getId() + ")");
            manager.getTaskById(task2.getId());
            printHistory(manager);

            System.out.println("\nЗапрос Эпик №2 (id=" + epic2.getId() + ")");
            manager.getEpicById(epic2.getId());
            printHistory(manager);

            System.out.println("\nЗапрос Подзадачи №3 (id=" + subTask3.getId() + ")");
            manager.getSubTaskById(subTask3.getId());
            printHistory(manager);

            System.out.println("\nЗапрос Эпик №1 (id=" + epic1.getId() + ")");
            manager.getEpicById(epic1.getId());
            printHistory(manager);

            System.out.println("\nЗапрос Подзадачи №1 (id=" + subTask1.getId() + ")");
            manager.getSubTaskById(subTask1.getId());
            printHistory(manager);

            System.out.println("\nПовторный запрос Эпик №2 (id=" + epic2.getId() + ")");
            manager.getEpicById(epic2.getId());
            printHistory(manager);

            System.out.println("\nПовторный запрос Подзадачи №1 (id=" + subTask1.getId() + ")");
            manager.getSubTaskById(subTask1.getId());
            printHistory(manager);

            System.out.println("\nУдаляем Задачу №2 (id=" + task2.getId() + ")");
            manager.deleteTaskById(task2.getId());
            printHistory(manager);

            System.out.println("\nУдаляем Эпик №1 (id=" + epic1.getId() + ") с подзадачами");
            manager.deleteEpicById(epic1.getId());
            printHistory(manager);

            printAll(manager);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file, new InMemoryHistoryManager());

            System.out.println("\nЗадачи после загрузки из файла:");
            for (Task t : loadedManager.getAllTasks()) {
                System.out.println(t);
            }

            System.out.println("\nЭпики после загрузки из файла:");
            for (Epic e : loadedManager.getAllEpics()) {
                System.out.println(e + ", startTime=" + loadedManager.getEpicStartTime(e.getId()) +
                        ", duration=" + loadedManager.getEpicDuration(e.getId()) +
                        ", endTime=" + loadedManager.getEpicEndTime(e.getId()));
            }

            System.out.println("\nПодзадачи после загрузки из файла:");
            for (SubTask s : loadedManager.getAllSubTasks()) {
                System.out.println(s);
            }

            System.out.println("\nИстория после загрузки из файла:");
            printHistory(loadedManager);

        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static void printAll(TaskManager manager) {
        System.out.println("\n-- Задачи --");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n-- Эпики --");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic + ", startTime=" + manager.getEpicStartTime(epic.getId()) +
                    ", duration=" + manager.getEpicDuration(epic.getId()) +
                    ", endTime=" + manager.getEpicEndTime(epic.getId()));
        }

        System.out.println("\n-- Подзадачи --");
        for (SubTask subTask : manager.getAllSubTasks()) {
            System.out.println(subTask);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\n-- История просмотров --");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

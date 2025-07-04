import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(0, "Задача №1", "Описание", TaskStatus.NEW);
        Task task2 = new Task(0, "Задача №2", "Описание", TaskStatus.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(0, "Эпик №1", "Три подзадачи", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Эпик №2", "Без подзадач", TaskStatus.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        SubTask subTask1 = new SubTask(0, "Подзадача №1", "К эпик №1", TaskStatus.NEW, epic1.getId());
        SubTask subTask2 = new SubTask(0, "Подзадача №2", "К эпик №1", TaskStatus.NEW, epic1.getId());
        SubTask subTask3 = new SubTask(0, "Подзадача №3", "К эпик №1", TaskStatus.NEW, epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        manager.getSubTaskById(subTask2.getId());
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic2.getId());
        manager.getSubTaskById(subTask3.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubTaskById(subTask1.getId());

        System.out.println("Запрос Эпик №2");
        manager.getEpicById(epic2.getId());
        printHistory(manager);

        System.out.println("Запрос Задачи №1");
        manager.getTaskById(task1.getId());
        printHistory(manager);

        System.out.println("Запрос Подзадачи №3");
        manager.getSubTaskById(subTask3.getId());
        printHistory(manager);

        System.out.println("Запрос Подзадачи №1");
        manager.getSubTaskById(subTask1.getId());
        printHistory(manager);

        System.out.println("Повторный запрос Эпик №2");
        manager.getEpicById(epic2.getId());
        printHistory(manager);

        System.out.println("Повторный запрос Подзадачи №1");
        manager.getSubTaskById(subTask1.getId());
        printHistory(manager);

        System.out.println("\nУдаляем Задачу №2 (id=" + task2.getId() + ")");
        manager.deleteTaskById(task2.getId());
        printHistory(manager);

        System.out.println("\nУдаляем эпик Эпик №1 (id=" + epic1.getId() + ") с подзадачами");
        manager.deleteEpicById(epic1.getId());
        printHistory(manager);
    }

    private static void printAll(TaskManager manager) {
        System.out.println("\n-- Задачи --");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("-- Эпики --");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("-- Подзадачи --");
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

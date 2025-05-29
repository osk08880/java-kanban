import manager.InMemoryTaskManager;
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

        Epic epic1 = new Epic(0, "Эпик №1", "Две подзадачи", TaskStatus.NEW);
        Epic epic2 = new Epic(0, "Эпик №2", "Одна подзадача", TaskStatus.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        SubTask subTask1 = new SubTask(0, "Подзадача №1", "К эпик №1", TaskStatus.NEW, epic1.getId());
        SubTask subTask2 = new SubTask(0, "Подзадача №2", "К эпик №1", TaskStatus.NEW, epic1.getId());
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        manager.getEpicById(epic1.getId());
        manager.getEpicById(epic2.getId());

        manager.getSubTaskById(subTask1.getId());
        manager.getSubTaskById(subTask2.getId());

        System.out.println("== Начальное состояние ==");
        printAll(manager);
        printHistory(manager);

        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);


        System.out.println("\n== После обновления статусов ==");
        printAll(manager);
        printHistory(manager);

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("\n== После удаления задачи и эпика ==");
        printAll(manager);
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
        for (SubTask SubTask : manager.getAllSubTasks()) {
            System.out.println(SubTask);
        }
    }
    private static void printHistory(TaskManager manager) {
        System.out.println("\n-- История просмотров --");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

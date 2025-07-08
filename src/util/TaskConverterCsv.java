package util;

import model.Epic;
import model.SubTask;
import model.Task;

public class TaskConverterCsv {

    public static String toString(Task task) {
        String[] fields = {
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getTitle(),
                task.getStatus().name(),
                task.getDetails(),
                task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", -1); // -1 сохраняет пустые поля
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String details = parts[4];

        return switch (type) {
            case TASK -> new Task(id, name, details, status);
            case EPIC -> new Epic(id, name, details, status);
            case SUBTASK -> new SubTask(id, name, details, status, Integer.parseInt(parts[5]));
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
    }
}

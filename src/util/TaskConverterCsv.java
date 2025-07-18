package util;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskConverterCsv {
    public static String toString(Task task) {
        String duration = task.getDuration() != null ? task.getDuration().toString() : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        if (task instanceof SubTask subTask) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                    task.getId(), task.getType(), task.getTitle(), task.getStatus(),
                    task.getDetails(), subTask.getEpicId(), duration, startTime);
        } else {
            return String.format("%d,%s,%s,%s,%s,,%s,%s",
                    task.getId(), task.getType(), task.getTitle(), task.getStatus(),
                    task.getDetails(), duration, startTime);
        }
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line);
        }
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String details = parts[4];
        String epicId = parts[5];
        Duration duration = parts[6].isEmpty() ? null : Duration.parse(parts[6]);
        LocalDateTime startTime = parts[7].isEmpty() ? null : LocalDateTime.parse(parts[7]);

        Task task = switch (type) {
            case TASK -> new Task(id, name, details, status);
            case EPIC -> new Epic(id, name, details, status);
            case SUBTASK -> new SubTask(id, name, details, status, Integer.parseInt(epicId));
        };
        task.setDuration(duration);
        task.setStartTime(startTime);
        return task;
    }
}

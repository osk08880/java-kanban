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
        String viewed = String.valueOf(task.isViewed());
        if (task instanceof SubTask subTask) {
            return String.format("%d,%s,%s,%s,%s,%d,%s,%s,%s",
                    task.getId(), task.getType(), task.getTitle(), task.getStatus(),
                    task.getDetails(), subTask.getEpicId(), duration, startTime, viewed);
        } else {
            return String.format("%d,%s,%s,%s,%s,,%s,%s,%s",
                    task.getId(), task.getType(), task.getTitle(), task.getStatus(),
                    task.getDetails(), duration, startTime, viewed);
        }
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 9) {
            throw new IllegalArgumentException("Некорректный формат строки: " + line + ". Ожидается минимум 9 полей.");
        }
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String details = parts[4];
        String epicIdStr = parts[5];
        Duration duration = parts[6].isEmpty() ? null : parseDuration(parts[6]);
        LocalDateTime startTime = parts[7].isEmpty() ? null : LocalDateTime.parse(parts[7]);
        boolean viewed = Boolean.parseBoolean(parts[8]);

        Task task = switch (type) {
            case TASK -> new Task(id, name, details, status);
            case EPIC -> new Epic(id, name, details, status);
            case SUBTASK -> {
                if (epicIdStr.isEmpty() || epicIdStr.equals("null")) {
                    throw new IllegalArgumentException("Epic ID не указан для подзадачи: " + line);
                }
                yield new SubTask(id, name, details, status, Integer.parseInt(epicIdStr));
            }
        };
        task.setDuration(duration);
        task.setStartTime(startTime);
        task.setViewed(viewed);
        return task;
    }

    private static Duration parseDuration(String durationStr) {
        try {
            return durationStr.isEmpty() ? null : Duration.parse(durationStr);
        } catch (Exception e) {
            return null;
        }
    }
}
package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskConverterCsv;


import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,details,epic,duration,startTime\n");
            for (Task task : getAllTasks()) {
                writer.write(TaskConverterCsv.toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(TaskConverterCsv.toString(epic) + "\n");
            }
            for (SubTask subtask : getAllSubTasks()) {
                writer.write(TaskConverterCsv.toString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(getHistory()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file.getPath(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, historyManager);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                try {
                    Task task = TaskConverterCsv.fromString(line);
                    switch (task.getType()) {
                        case TASK -> manager.createTask(task);
                        case EPIC -> manager.createEpic((Epic) task);
                        case SUBTASK -> manager.createSubTask((SubTask) task);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Ошибка при обработке строки: " + line + ". Причина: " + e.getMessage());
                }
            }
            line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                for (int id : historyFromString(line)) {
                    if (manager.tasks.containsKey(id)) {
                        manager.getTaskById(id);
                    } else if (manager.epics.containsKey(id)) {
                        manager.getEpicById(id);
                    } else if (manager.subtasks.containsKey(id)) {
                        manager.getSubTaskById(id);
                    }
                }
            }
            int maxId = Math.max(
                    manager.tasks.keySet().stream().mapToInt(Integer::intValue).max().orElse(0),
                    Math.max(
                            manager.epics.keySet().stream().mapToInt(Integer::intValue).max().orElse(0),
                            manager.subtasks.keySet().stream().mapToInt(Integer::intValue).max().orElse(0)
                    )
            );
            manager.nextId = maxId + 1;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке задач из файла: " + file.getPath(), e);
        }
        return manager;
    }

    private String historyToString(List<Task> history) {
        return history.stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String line) {
        if (line.isEmpty()) return new ArrayList<>();
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
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

    @Override
    public Optional<Task> getTaskById(int id) {
        Optional<Task> taskOpt = super.getTaskById(id);
        if (taskOpt.isPresent()) {
            save();
        }
        return taskOpt;
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Optional<Epic> epicOpt = super.getEpicById(id);
        if (epicOpt.isPresent()) {
            save();
        }
        return epicOpt;
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        Optional<SubTask> subTaskOpt = super.getSubTaskById(id);
        if (subTaskOpt.isPresent()) {
            save();
        }
        return subTaskOpt;
    }

    @Override
    public LocalDateTime getEpicStartTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        return epic.getSubTaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEpicEndTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        return epic.getSubTaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getEpicDuration(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return null;
        return epic.getSubTaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus, Duration::plus);
    }
}
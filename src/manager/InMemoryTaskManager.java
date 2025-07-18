package manager;

import model.Epic;
import model.SubTask;
import model.Task;
import util.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null || t2.getStartTime() == null) return t1.getId() - t2.getId();
        return t1.getStartTime().compareTo(t2.getStartTime());
    });
    protected int nextId = 1;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public boolean hasIntersection(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> t != task)
                .anyMatch(task::intersects);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        List<Integer> taskIds = new ArrayList<>(tasks.keySet());
        taskIds.forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        });
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return Optional.ofNullable(task);
    }

    @Override
    public void createTask(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null && hasIntersection(task)) {
            throw new IllegalArgumentException("Данная задача пересекается с другой по времени выполнения");
        }
        if (task.getId() == 0) {
            task.setId(nextId++);
        } else if (task.getId() >= nextId) {
            nextId = task.getId() + 1;
        }
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            prioritizedTasks.remove(oldTask);
            if (task.getStartTime() != null && task.getDuration() != null && hasIntersection(task)) {
                prioritizedTasks.add(oldTask);
                throw new IllegalArgumentException("Данная задача пересекается с другой по времени выполнения");
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> epicIds = new ArrayList<>(epics.keySet());
        epicIds.forEach(id -> {
            Epic epic = epics.get(id);
            epic.getSubTaskIds().forEach(subId -> {
                historyManager.remove(subId);
                prioritizedTasks.remove(subtasks.get(subId));
                subtasks.remove(subId);
            });
            historyManager.remove(id);
            epics.remove(id);
        });
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return Optional.ofNullable(epic);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        } else if (epic.getId() >= nextId) {
            nextId = epic.getId() + 1;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            List<Integer> subTaskIds = new ArrayList<>(epic.getSubTaskIds());
            subTaskIds.forEach(subtaskId -> {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        }
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubTaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtaskIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);

        boolean allDone = subtaskIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        List<Integer> subTaskIds = new ArrayList<>(subtasks.keySet());
        subTaskIds.forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
        });
        epics.values().forEach(epic -> {
            epic.getSubTaskIds().clear();
            updateEpicStatus(epic.getId());
        });
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        SubTask subTask = subtasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return Optional.ofNullable(subTask);
    }

    @Override
    public void createSubTask(SubTask subtask) {
        if (subtask.getStartTime() != null && subtask.getDuration() != null && hasIntersection(subtask)) {
            throw new IllegalArgumentException("Данная задача пересекается с другой по времени выполнения");
        }
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        } else if (subtask.getId() >= nextId) {
            nextId = subtask.getId() + 1;
        }
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("SubTask не может быть своим же эпиком");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с id " + subtask.getEpicId() + " не существует.");
        }
        subtasks.put(subtask.getId(), subtask);
        epic.addSubTaskId(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            SubTask oldSubtask = subtasks.get(subtask.getId());
            prioritizedTasks.remove(oldSubtask);
            if (subtask.getStartTime() != null && subtask.getDuration() != null && hasIntersection(subtask)) {
                prioritizedTasks.add(oldSubtask);
                throw new IllegalArgumentException("Данная задача пересекается с другой по времени выполнения");
            }
            if (subtask.getId() == subtask.getEpicId()) {
                prioritizedTasks.add(oldSubtask);
                throw new IllegalArgumentException("SubTask не может быть своим же эпиком");
            }
            Epic epic = epics.get(subtask.getEpicId());
            if (epic == null) {
                prioritizedTasks.add(oldSubtask);
                throw new IllegalArgumentException("Эпик с id " + subtask.getEpicId() + " не существует.");
            }
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(subtask.getEpicId());
            }
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public List<SubTask> getSubTasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Collections.emptyList();
        }
        return epic.getSubTaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
                .reduce(Duration.ZERO, Duration::plus);
    }

    protected void addToHistory(Task task) {
        if (task != null) {
            historyManager.add(task);
        }
    }
}
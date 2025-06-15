package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getId());
        Task copy = copyOf(task);
        copy.setViewed(true);
        linkLast(copy);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            result.add(copyOf(current.task));
            current = current.next;
        }
        return result;
    }

    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        Node newNode = new Node(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }

        node.prev = null;
        node.next = null;
        node.task = null;
    }

    private Task copyOf(Task task) {
        Task copy;
        if (task instanceof SubTask subTask) {
            copy = new SubTask(
                    subTask.getId(),
                    subTask.getTitle(),
                    subTask.getDetails(),
                    subTask.getStatus(),
                    subTask.getEpicId()
            );
        } else if (task instanceof Epic epic) {
            Epic copyEpic = new Epic(
                    epic.getId(),
                    epic.getTitle(),
                    epic.getDetails(),
                    epic.getStatus()
            );
            copyEpic.setSubTaskIds(new ArrayList<>(epic.getSubTaskIds())); // копия списка
            copy = copyEpic;
        } else {
            copy = new Task(
                    task.getId(),
                    task.getTitle(),
                    task.getDetails(),
                    task.getStatus()
            );
        }
        copy.setViewed(task.isViewed());
        return copy;
    }
}

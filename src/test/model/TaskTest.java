package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import util.TaskStatus;

public class TaskTest {

    @Test
    public void testTasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task(1, "Title A", "Details A", TaskStatus.DONE);
        Task task2 = new Task(1, "Title B", "Details B", TaskStatus.NEW);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны, даже если остальные поля разные");
    }

    @Test
    public void testTasksAreNotEqualIfIdsDiffer() {
        Task task1 = new Task(1, "Title A", "Details A", TaskStatus.IN_PROGRESS);
        Task task2 = new Task(2, "Title A", "Details A", TaskStatus.IN_PROGRESS);

        assertNotEquals(task1, task2, "Задачи с разными id не должны быть равны");
    }

    @Test
    public void testEqualsWithDifferentObjects() {
        Task task = new Task(1, "Title", "Details", TaskStatus.NEW);

        assertNotEquals(task, null, "Задача не должна быть равна null");

        String other = "not a task";
        assertNotEquals(task, other, "Задача не должна быть равна объекту другого класса");
    }
}
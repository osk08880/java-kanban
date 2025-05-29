package model;

import org.junit.jupiter.api.Test;
import util.TaskStatus;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void epicAndSubTaskShouldBeEqualIfSameId() {
        Epic epic = new Epic(42, "Epic Title", "Epic Details", TaskStatus.NEW);
        SubTask subTask = new SubTask(42, "Subtask Title", "Subtask Details", TaskStatus.DONE, 100);

        assertEquals(epic, subTask, "Epic и SubTask с одинаковым id должны быть равны");
        assertEquals(subTask, epic, "SubTask и Epic с одинаковым id должны быть равны (рефлексивность)");
    }

    @Test
    public void epicAndSubTaskShouldNotBeEqualIfDifferentIds() {
        Epic epic = new Epic(1, "Epic Title", "Epic Details", TaskStatus.NEW);
        SubTask subTask = new SubTask(2, "Subtask Title", "Subtask Details", TaskStatus.DONE, 100);

        assertNotEquals(epic, subTask, "Epic и SubTask с разными id не должны быть равны");
    }
    @Test
    public void shouldNotAllowSelfAsSubtask() {
        Epic epic = new Epic(10, "Epic", "Details", TaskStatus.NEW);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> epic.addSubTaskId(10));
        assertEquals("Epic нельзя добавить в самого себя в виде подзадачи", exception.getMessage());
    }
}

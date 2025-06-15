package model;

import org.junit.jupiter.api.Test;
import util.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void shouldThrowExceptionIfSubTaskIsItsOwnEpic() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new SubTask(10, "SubTask Title", "SubTask Details", TaskStatus.NEW, 10);
        });
        assertEquals("SubTask не может быть своим же эпиком", exception.getMessage());
    }

    @Test
    void shouldCreateSubTaskIfEpicIdIsDifferent() {
        assertDoesNotThrow(() -> {
            new SubTask(10, "SubTask Title", "SubTask Details", TaskStatus.NEW, 20);
        });
    }
}
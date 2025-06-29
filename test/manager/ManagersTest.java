package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void managersReturnInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не должен быть null");

        assertNotNull(taskManager.getHistory(), "История не должна быть null");
        assertTrue(taskManager.getHistory().isEmpty(), "История должна быть пустой по умолчанию");

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не должен быть null");
    }

    @Test
    void getDefaultReturnsNewInstanceEachTime() {
        TaskManager first = Managers.getDefault();
        TaskManager second = Managers.getDefault();
        assertNotSame(first, second, "getDefault должен возвращать новый объект при каждом вызове");
    }
}
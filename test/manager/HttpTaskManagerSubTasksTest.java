package manager;

import com.google.gson.Gson;
import model.Epic;
import model.SubTask;
import util.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubTasksTest {
    private TaskManager manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    private HttpTaskServer taskServer = new HttpTaskServer(manager);
    private static final Gson gson = HttpTaskServer.getGson();
    private HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubTask_Success() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Тестовый эпик", "Описание тестового эпика", TaskStatus.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask(0, "Тестовая подзадача", "Описание тестовой подзадачи", TaskStatus.NEW, epic.getId());
        subTask.setStartTime(LocalDateTime.now().plusHours(2));
        subTask.setDuration(Duration.ofMinutes(30));
        String subTaskJson = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        var subTasks = manager.getAllSubTasks();
        assertEquals(1, subTasks.size());
        assertEquals("Тестовая подзадача", subTasks.get(0).getTitle());
    }

    @Test
    public void testAddSubTask_InvalidEpic() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(0, "Тестовая подзадача", "Описание тестовой подзадачи", TaskStatus.NEW, 999);
        String subTaskJson = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
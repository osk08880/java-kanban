package manager;

import com.google.gson.Gson;
import model.Task;
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

public class HttpTaskManagerTasksTest {
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
    void testAddTask_Success() throws IOException, InterruptedException {
        Task task = new Task(0, "Тестовая задача", "Описание тестовой задачи", TaskStatus.NEW);
        task.setStartTime(LocalDateTime.now().plusHours(1));
        task.setDuration(Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        var tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        Task createdTask = tasks.get(0);
        assertEquals("Тестовая задача", createdTask.getTitle());
        assertNotEquals(0, createdTask.getId(), "ID должен быть сгенерирован менеджером");
    }

    @Test
    void testGetTask_NotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
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

public class HttpTaskManagerPrioritizedTest {
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
    public void testGetPrioritized_Empty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        var prioritized = gson.fromJson(response.body(), manager.getPrioritizedTasks().getClass());
        assertTrue(prioritized.isEmpty());
    }

    @Test
    public void testGetPrioritized_WithTasks() throws IOException, InterruptedException {
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(2));
        task2.setDuration(Duration.ofMinutes(30));
        manager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        var prioritized = gson.fromJson(response.body(), manager.getPrioritizedTasks().getClass());
        assertEquals(2, prioritized.size());
    }
}
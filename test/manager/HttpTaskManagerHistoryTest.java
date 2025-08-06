package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Task;
import util.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {
    private TaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());
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
    public void testGetHistory_Empty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = gson.fromJson(response.body(), listType);
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void testGetHistory_AfterTaskView() throws IOException, InterruptedException {
        Task task = new Task(0, "Тестовая задача", "Описание теста", TaskStatus.NEW);
        manager.createTask(task);
        Optional<Task> taskOpt = manager.findTaskById(task.getId());
        assertTrue(taskOpt.isPresent(), "Задача должна быть найдена");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидается успешный ответ");

        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> history = gson.fromJson(response.body(), listType);
        assertEquals(1, history.size(), "История должна содержать 1 запись");
        assertEquals(task.getId(), history.get(0).getId(), "ID задачи в истории должен совпадать");
        assertEquals(task.getTitle(), history.get(0).getTitle(), "Название задачи в истории должно совпадать");
    }
}
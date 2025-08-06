package manager;

import com.google.gson.Gson;
import model.Epic;
import util.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
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
    public void testAddEpic_Success() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Тестовый эпик", "Описание тестового эпика", TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        var epics = manager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Тестовый эпик", epics.get(0).getTitle());
    }

    @Test
    public void testGetEpic_NotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
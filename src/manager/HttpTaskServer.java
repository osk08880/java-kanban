package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {
                @Override
                public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                    if (duration == null) {
                        jsonWriter.nullValue();
                    } else {
                        jsonWriter.value(duration.toString());
                    }
                }

                @Override
                public Duration read(JsonReader jsonReader) throws IOException {
                    String value = jsonReader.nextString();
                    return value == null || value.isEmpty() ? null : Duration.parse(value);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                    if (localDateTime == null) {
                        jsonWriter.nullValue();
                    } else {
                        jsonWriter.value(localDateTime.toString());
                    }
                }

                @Override
                public LocalDateTime read(JsonReader jsonReader) throws IOException {
                    String value = jsonReader.nextString();
                    return value == null || value.isEmpty() ? null : LocalDateTime.parse(value);
                }
            })
            .create();

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() throws IOException {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks", new TaskHandler(taskManager));
            server.createContext("/subtasks", new SubTaskHandler(taskManager));
            server.createContext("/epics", new EpicHandler(taskManager));
            server.createContext("/history", new HistoryHandler(taskManager));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager));
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP сервер использует порт " + PORT + " в " + LocalDateTime.now());
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            throw e;
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP сервер остановлен в " + LocalDateTime.now());
        }
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        try {
            System.out.println("Начальные данные инициализированы.");
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка инициализации данных: " + e.getMessage());
        }

        HttpTaskServer taskServer = new HttpTaskServer(manager);
        try {
            taskServer.start();
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            System.exit(1);
        }
    }
}
package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = HttpTaskServer.getGson();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(HttpExchange h, String message) throws IOException {
        sendText(h, gson.toJson(new ErrorResponse(404, message)), 404);
    }

    protected void sendHasIntersection(HttpExchange h, String message) throws IOException {
        sendText(h, gson.toJson(new ErrorResponse(406, message)), 406);
    }

    protected void sendInternalError(HttpExchange h, String message) throws IOException {
        sendText(h, gson.toJson(new ErrorResponse(500, message)), 500);
    }

    public static class ErrorResponse {
        private final int code;
        private final String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
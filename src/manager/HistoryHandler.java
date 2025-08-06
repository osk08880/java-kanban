package manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            String[] pathParts = path.split("/");
            if (pathParts.length != 2 || !pathParts[1].equals("history")) {
                sendNotFound(exchange, "Ресурс не найден");
                return;
            }

            if ("GET".equals(method)) {
                String historyJson = gson.toJson(taskManager.getHistory());
                sendText(exchange, historyJson, 200);
            } else {
                sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (Exception e) {
            sendInternalError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }
}

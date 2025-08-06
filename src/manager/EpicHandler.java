package manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            String[] pathParts = path.split("/");
            if (pathParts.length < 2 || !pathParts[1].equals("epics")) {
                try {
                    sendNotFound(exchange, "Ресурс не найден");
                } catch (IOException e) {
                    sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                }
                return;
            }

            if (method.equals("GET")) {
                if (pathParts.length == 2) {
                    String epicsJson = gson.toJson(taskManager.getAllEpics());
                    try {
                        sendText(exchange, epicsJson, 200);
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                } else if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    try {
                        Epic epic = taskManager.getEpicById(id);
                        try {
                            sendText(exchange, gson.toJson(epic), 200);
                        } catch (IOException e) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                        }
                    } catch (NotFoundException e) {
                        try {
                            sendNotFound(exchange, e.getMessage());
                        } catch (IOException ioException) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + ioException.getMessage());
                        }
                    }
                }
            } else if (method.equals("POST")) {
                if (pathParts.length == 2) {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic.getSubTaskIds() == null) {
                        epic.setSubTaskIds(new ArrayList<>());
                    }

                    taskManager.createEpic(epic);
                    try {
                        sendText(exchange, "", 201);
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                } else if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (epic.getSubTaskIds() == null) {
                        epic.setSubTaskIds(new ArrayList<>());
                    }

                    epic.setId(id);
                    taskManager.updateEpic(epic);
                    try {
                        sendText(exchange, "", 201);
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                }
            } else if (method.equals("DELETE")) {
                if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.deleteEpicById(id);
                    try {
                        sendText(exchange, "", 200);
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                } else {
                    try {
                        sendNotFound(exchange, "Некорректный запрос");
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                }
            } else {
                try {
                    sendNotFound(exchange, "Метод не поддерживается");
                } catch (IOException e) {
                    sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            try {
                sendNotFound(exchange, "Некорректный ID");
            } catch (IOException ioException) {
                sendInternalError(exchange, "Ошибка при отправке ответа: " + ioException.getMessage());
            }
        } catch (Exception e) {
            try {
                sendInternalError(exchange, "Ошибка сервера: " + e.getMessage());
            } catch (IOException ioException) {
                System.err.println("Не удалось отправить ошибку сервера: " + ioException.getMessage());
            }
        }
    }
}

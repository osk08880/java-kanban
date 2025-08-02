package manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            String[] pathParts = path.split("/");
            if (pathParts.length < 2 || !pathParts[1].equals("tasks")) {
                try {
                    sendNotFound(exchange, "Ресурс не найден");
                } catch (IOException e) {
                    sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                }
                return;
            }

            if (method.equals("GET")) {
                if (pathParts.length == 2) {
                    String tasksJson = gson.toJson(taskManager.getAllTasks());
                    try {
                        sendText(exchange, tasksJson, 200);
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                } else if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    try {
                        Task task = taskManager.getTaskById(id);
                        try {
                            sendText(exchange, gson.toJson(task), 200);
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
                    Task task = gson.fromJson(body, Task.class);
                    try {
                        taskManager.createTask(task);
                        try {
                            sendText(exchange, "", 201);
                        } catch (IOException e) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                        }
                    } catch (IllegalArgumentException e) {
                        try {
                            sendHasIntersection(exchange, e.getMessage());
                        } catch (IOException ioException) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + ioException.getMessage());
                        }
                    }
                } else if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    task.setId(id);
                    try {
                        taskManager.updateTask(task);
                        try {
                            sendText(exchange, "", 201);
                        } catch (IOException e) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                        }
                    } catch (IllegalArgumentException e) {
                        try {
                            sendHasIntersection(exchange, e.getMessage());
                        } catch (IOException ioException) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + ioException.getMessage());
                        }
                    }
                }
            } else if (method.equals("DELETE")) {
                if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.deleteTaskById(id);
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
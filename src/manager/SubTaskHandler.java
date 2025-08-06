package manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            String[] pathParts = path.split("/");
            if (pathParts.length < 2 || !pathParts[1].equals("subtasks")) {
                try {
                    sendNotFound(exchange, "Ресурс не найден");
                } catch (IOException e) {
                    sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                }
                return;
            }

            if (method.equals("GET")) {
                if (pathParts.length == 2) {
                    String subtasksJson = gson.toJson(taskManager.getAllSubTasks());
                    try {
                        sendText(exchange, subtasksJson, 200);
                    } catch (IOException e) {
                        sendInternalError(exchange, "Ошибка при отправке ответа: " + e.getMessage());
                    }
                } else if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    try {
                        SubTask subtask = taskManager.getSubTaskById(id);
                        try {
                            sendText(exchange, gson.toJson(subtask), 200);
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
                    SubTask subtask = gson.fromJson(body, SubTask.class);
                    try {
                        taskManager.getEpicById(subtask.getEpicId());
                        if (subtask.getEpicId() <= 0) {
                            throw new NotFoundException("Эпик ID должен быть положительным");
                        }
                        try {
                            taskManager.createSubTask(subtask);
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
                    } catch (NotFoundException e) {
                        try {
                            sendNotFound(exchange, "Эпик для подзадачи не найден: " + e.getMessage());
                        } catch (IOException ioException) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + ioException.getMessage());
                        }
                    }
                } else if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subtask = gson.fromJson(body, SubTask.class);
                    subtask.setId(id);
                    try {
                        taskManager.getEpicById(subtask.getEpicId());
                        if (subtask.getEpicId() <= 0) {
                            throw new NotFoundException("Эпик ID должен быть положительным");
                        }
                        try {
                            taskManager.updateSubTask(subtask);
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
                    } catch (NotFoundException e) {
                        try {
                            sendNotFound(exchange, "Эпик для подзадачи не найден: " + e.getMessage());
                        } catch (IOException ioException) {
                            sendInternalError(exchange, "Ошибка при отправке ответа: " + ioException.getMessage());
                        }
                    }
                }
            } else if (method.equals("DELETE")) {
                if (pathParts.length == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.deleteSubTaskById(id);
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
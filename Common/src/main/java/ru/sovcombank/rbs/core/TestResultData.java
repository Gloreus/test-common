package ru.sovcombank.rbs.core;

import lombok.Data;

/// Результат выполнения теста
@Data
public class TestResultData {
    /// Продолжительность выполнения теста в мсек
    private long duration;
    /// Результат от сервера в виде JSON
    private String json;
    /// Сообщение при ошибке выполнения запроса от сервера
    private String errorMessage;
    /// Статус ответа от сервера, если требуется. При ошибке тут должен быть errorCode
    private String errorCode;
    /// Сырой ответ от сервера
    private Object value;
}

package ru.sovcombank.rbs.caseentity;

import lombok.Data;

/// Ответ исполнителя тест-кейса
@Data
public class TestCaseResponse<T> {
    ///  Текстовое сообщение от сервера, обычно тут текст ошибки, если возникла при выполнении
    private String message;
    ///  Код ошибки, если возникла при выполнении
    private String status;
    ///  Реальный ответ от сервера
    private T value;
}

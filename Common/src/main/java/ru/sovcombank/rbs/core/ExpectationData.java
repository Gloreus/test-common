package ru.sovcombank.rbs.core;

///  Ожидаемый результат выполнения тест-кейса
public interface ExpectationData {
    /// Способ проверки
    ValidationType getValidationType();

    /// Тип ожидаемого значения
    DbTypes getExpectType();
}

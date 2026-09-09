package ru.sovcombank.rbs.core;

/// Виды сравнения результата теста с ожиданием
public enum ValidationType {
    ///  Тип валидатора не определён
    NONE,
    // Базовые сравнения
    TO_BE_EQUAL,
    TO_BE_NOT_EQUAL,
    TO_BE_NULL,
    TO_BE_NOT_NULL,

    // Числовые сравнения
    TO_BE_GREATER_THAN,
    TO_BE_LESS_THAN,
    TO_BE_GREATER_THAN_OR_EQUAL,
    TO_BE_LESS_THAN_OR_EQUAL,
    TO_BE_BETWEEN,

    // Строковые сравнения
    TO_CONTAIN,
    TO_START_WITH,
    TO_END_WITH,
    TO_BE_EMPTY,

    // Пользовательские проверки
    CHECK_PREDICATE
}

package ru.sovcombank.rbs.core;

@FunctionalInterface
public interface Validator<T extends ExpectationData> {
    default ValidationType getType() {
        return ValidationType.NONE;
    }

    /// Сравниваем ожидания с ответом сервера
    void validate(T expectationData, TestResultData testResultData) throws AssertionError;
}
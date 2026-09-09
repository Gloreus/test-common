package ru.sovcombank.rbs.core;

import ru.sovcombank.rbs.caseentity.TestCaseResponse;

@FunctionalInterface
public interface TestCaseRunner<T> {
    TestCaseResponse<T> run(TestCaseData testCaseData);

    default TestCaseResponse<T> init() {
        throw new UnsupportedOperationException("Метод не реализован");
    }

    default String getSystem() {
        return "";
    }
}

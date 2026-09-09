package ru.sovcombank.rbs.caseentity;

import lombok.NonNull;

import java.io.FileNotFoundException;
import java.util.List;

public interface TestDataRepository {
    ///  Загружаем конкретный профиль тестирования по имени
    TestProfile loadProfile(@NonNull String profileName) throws FileNotFoundException;

    /// Загружаем конкретный тест-кейс по ссылки из профиля
    TestCase loadCase(@NonNull TestCaseReference reference) throws FileNotFoundException;

    /// Все кейсы для указанного профиля
    List<TestCase> loadCases(TestProfile profile);
}

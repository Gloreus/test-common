package ru.sovcombank.rbs.caseentity;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface TestDataRepository {
    ///  Загружаем конкретный профиль тестирования по имени
    TestProfile loadProfile(@NonNull String profileName) throws FileNotFoundException, IOException;

    /// Загружаем конкретный тест-кейс по ссылки из профиля
    TestCase loadCase(@NonNull TestCaseReference reference) throws IOException;

    /// Все кейсы для указанного профиля
    List<TestCase> loadCases(TestProfile profile);

    String writeCaseToString(TestCase testCase) throws JsonProcessingException;
}

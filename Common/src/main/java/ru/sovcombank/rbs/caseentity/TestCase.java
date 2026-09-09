package ru.sovcombank.rbs.caseentity;

import lombok.Data;
import ru.sovcombank.rbs.core.ExpectationData;
import ru.sovcombank.rbs.core.TestCaseData;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestCase {
    /// Описание тест-кейса
    private TestCaseData testCaseData;
    /// Ожидаемые результаты, все должны выполниться для успешного теста
    private List<ExpectationData> expectations;

    TestCase() {
        expectations = new ArrayList<>(0);
    }
}

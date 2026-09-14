package ru.sovcombank.rbs.validators;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sovcombank.rbs.TestCommonApp;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.caseentity.TestDataRepository;
import ru.sovcombank.rbs.caseentity.TestProfile;
import ru.sovcombank.rbs.core.TestResultData;
import ru.sovcombank.rbs.core.ValidationType;
import ru.sovcombank.rbs.expectations.ParamsExpectationData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest(classes = {TestCommonApp.class})
class JsonValidatorTest {
    @Autowired
    private JsonValidator jsonValidator;

    @Autowired
    private TestDataRepository repository;

    private final ParamsExpectationData expectationData = new ParamsExpectationData();
    private final TestResultData resultData = new TestResultData();

    @Autowired
    @Qualifier("YmlMapper")
    private ObjectMapper ymlMapper;

    @Test
    public void getType() {
        assertEquals(ValidationType.TO_BE_EQUAL, jsonValidator.getType(), "Не правильный тип валидатора");
    }

    @Test
    public void validateForEmptyParams() {
        assertThrows(AssertionError.class, () ->
            jsonValidator.validate(expectationData, resultData));
    }

    @SneakyThrows
    @Test
    @Disabled
    public void LoadProfileTest() {
        TestProfile profile = repository.loadProfile("123");
        List<TestCase> testCaseList = repository.loadCases(profile);
        assertNotNull(testCaseList);
        assertEquals(3, testCaseList.size(), "Загрузились не все тесты");
        log.info(testCaseList.get(2).getExpectations().toString());

    }
}
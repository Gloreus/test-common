package ru.sovcombank.rbs.caseentity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.sovcombank.rbs.TestCommonApp;
import ru.sovcombank.rbs.core.BlockType;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.ParamMode;
import ru.sovcombank.rbs.core.TestCaseData;
import ru.sovcombank.rbs.expectations.ParamsExpectationData;
import ru.sovcombank.rbs.ora.OracleTypes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {TestCommonApp.class})
@ActiveProfiles("test")
@Slf4j
class TestStoreConverterTest {

    @Autowired
    private TestStoreConverter converter;

    @Test
    void readTestCasesFromInputFile() throws IOException {
        List<TestCase> cases = converter.readTestCases(Path.of("TestStore/input/01.3.yml"));
        log.info("Прочитано тест-кейсов: {}", cases.size());
        assertNotNull(cases);
        assertEquals(14, cases.size());
    }

    @Test
    void firstCaseHasNoParams() throws IOException {
        List<TestCase> cases = converter.readTestCases(Path.of("TestStore/input/01.3.yml"));
        TestCase testCase = cases.get(0);
        TestCaseData data = testCase.getTestCaseData();
        assertEquals("Без объявления переменных, пустые вложенные блоки кода", data.getDescription());
        assertEquals(BlockType.BLOCK, data.getBlockType());
        assertTrue(data.getBlockSql().contains("BEGIN"));
        assertNull(data.getParams());
        assertTrue(testCase.getExpectations().isEmpty());
    }

    @Test
    void caseWithInOutParam() throws IOException {
        List<TestCase> cases = converter.readTestCases(Path.of("TestStore/input/01.3.yml"));
        // Кейс 3: n в inputBinds (1) и outputBinds (2) -> IN_OUT
        TestCase testCase = cases.get(2);
        TestCaseData data = testCase.getTestCaseData();
        assertNotNull(data.getParams());
        assertEquals(1, data.getParams().size());

        DbParam param = data.getParams().get(0);
        assertEquals("n", param.getName());
        assertEquals(ParamMode.IN_OUT, param.getParamMode());
        assertEquals(OracleTypes.NUMBER, param.getParamType());
        assertEquals(1, param.getValue());

        // Ожидание из result
        assertEquals(1, testCase.getExpectations().size());
        ParamsExpectationData expectation =
                (ParamsExpectationData) testCase.getExpectations().get(0);
        assertEquals(1, expectation.getParams().size());
        DbParam expected = expectation.getParams().get("n");
        assertNotNull(expected);
        assertEquals("n", expected.getName());
        assertEquals(2, expected.getValue());
    }

    @Test
    void errorCaseHasNoParamsAndNoExpectations() throws IOException {
        List<TestCase> cases = converter.readTestCases(Path.of("TestStore/input/01.3.yml"));
        // Кейс 10: resultType = error
        TestCase testCase = cases.get(9);
        assertNull(testCase.getTestCaseData().getParams());
        assertTrue(testCase.getExpectations().isEmpty());
    }
}

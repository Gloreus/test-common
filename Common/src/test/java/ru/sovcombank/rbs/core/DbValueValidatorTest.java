package ru.sovcombank.rbs.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sovcombank.rbs.expectations.OutputExpectationData;
import ru.sovcombank.rbs.ora.OracleTypes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DbValueValidatorTest extends DbValueValidator {

    @Test
    void getTypeTest() {
        assertEquals(ValidationType.TO_BE_EQUAL, getType());
    }

    @Test
    void validateTest() {
        TestResultData td = new TestResultData();
        td.setErrorCode("200 Ok");
        DbValue resValue = new DbValue();
        resValue.setParamType(OracleTypes.VARCHAR2);
        resValue.setValue("1024");
        td.setValue(resValue);

        OutputExpectationData ed = new OutputExpectationData(OracleTypes.VARCHAR2, ValidationType.TO_BE_EQUAL, "1024", "");

        assertDoesNotThrow(() -> validate(ed, td));

        ed.setExpectType(OracleTypes.BLOB);
        assertThrows(AssertionError.class, () -> validate(ed, td));
    }
}
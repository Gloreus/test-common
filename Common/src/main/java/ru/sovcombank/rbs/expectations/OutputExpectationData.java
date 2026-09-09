package ru.sovcombank.rbs.expectations;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sovcombank.rbs.core.DbTypes;
import ru.sovcombank.rbs.core.ExpectationData;
import ru.sovcombank.rbs.core.ValidationType;

@Data
@AllArgsConstructor
public class OutputExpectationData implements ExpectationData {
    private DbTypes expectType;
    private ValidationType validationType;
    private Object expected;
    private Object expected2;

    OutputExpectationData() {
        expectType = null;
        validationType = ValidationType.TO_BE_EQUAL;
        expected = null;
        expected2 = null;
    }
}

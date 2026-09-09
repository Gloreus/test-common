package ru.sovcombank.rbs.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.expectations.OutputExpectationData;

@Component
@Slf4j
public class DbValueValidator implements Validator<OutputExpectationData> {
    @Override
    public final ValidationType getType() {
        return ValidationType.TO_BE_EQUAL;
    }

    @Override
    public void validate(OutputExpectationData expectationData, TestResultData testResultData) throws AssertionError {
        log.debug("expectationData: " + expectationData);
        log.debug("testResultData: " + testResultData);

        if (expectationData == null || testResultData == null) {
            throw new IllegalArgumentException("Входные параметры должны быть заданы");
        }

        if (!(testResultData.getValue() instanceof DbValue value)) {
            throw new AssertionError("Ответ от сервера должен быть OracleTypes, получен: "
                    + testResultData.getValue());
        }

        if (!(value.getParamType().getJdbcType() == expectationData.getExpectType().getJdbcType())
                || !(value.getValue().equals(expectationData.getExpected()))) {
            throw new AssertionError(
                    "Ожидалось " + expectationData.getExpected()
                            + " типа " + expectationData.getExpectType()
                            + " а получено " + value.getValue() + " типа " + value.getParamType());
        }
    }
}

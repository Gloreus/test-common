package ru.sovcombank.rbs.expectations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sovcombank.rbs.TestCommonApp;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.DbParams;
import ru.sovcombank.rbs.core.ExpectationData;
import ru.sovcombank.rbs.core.ParamMode;
import ru.sovcombank.rbs.core.ValidationType;
import ru.sovcombank.rbs.ora.OracleTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Disabled
@SpringBootTest(classes = {TestCommonApp.class})
class ParamsExpectationDataTest {

    @Autowired
    @Qualifier("YmlMapper")
    private ObjectMapper ymlMapper;

    private ParamsExpectationData createExpectation() {
        ParamsExpectationData expectation = new ParamsExpectationData();
        expectation.setValidationType(ValidationType.TO_BE_EQUAL);
        expectation.getParams().add("P1", ParamMode.IN, OracleTypes.VARCHAR2, "Test string content");
        expectation.getParams().add("P2", ParamMode.IN_OUT, OracleTypes.NUMBER, 32);
        return expectation;
    }

    @SneakyThrows
    @Test
    void serializeContainsExpectClass() {
        ParamsExpectationData expectation = createExpectation();

        String yaml = ymlMapper.writeValueAsString(expectation);
        log.info("Serialized ParamsExpectationData:\n{}", yaml);

        assertTrue(yaml.contains("expectClass: \"ParamsExpectationData\"")
                        || yaml.contains("expectClass: ParamsExpectationData"),
                "Сериализованный YAML должен содержать expectClass: ParamsExpectationData");
        assertTrue(yaml.contains("P1"), "Сериализованный YAML должен содержать параметр P1");
        assertTrue(yaml.contains("P2"), "Сериализованный YAML должен содержать параметр P2");
    }

    @SneakyThrows
    @Test
    void serializeAndDeserializeRoundTrip() {
        ParamsExpectationData expectation = createExpectation();

        String yaml = ymlMapper.writeValueAsString(expectation);
        ExpectationData restored = ymlMapper.readValue(yaml, ExpectationData.class);

        assertNotNull(restored, "Десериализованный объект не должен быть null");
        assertInstanceOf(ParamsExpectationData.class, restored,
                "По expectClass должен восстановиться ParamsExpectationData");

        ParamsExpectationData restoredParams = (ParamsExpectationData) restored;
        assertEquals(ValidationType.TO_BE_EQUAL, restoredParams.getValidationType(),
                "validationType должен сохраниться при сериализации");

        DbParams restoredDbParams = restoredParams.getParams();
        assertNotNull(restoredDbParams, "params не должны быть null");
        assertEquals(2, restoredDbParams.size(), "Должно восстановиться 2 параметра");

        DbParam p1 = restoredDbParams.get("P1");
        assertNotNull(p1, "Параметр P1 должен восстановиться");
        assertEquals("Test string content", p1.getValue());
        assertEquals(OracleTypes.VARCHAR2, p1.getParamType());
        assertEquals(ParamMode.IN, p1.getParamMode());

        DbParam p2 = restoredDbParams.get("P2");
        assertNotNull(p2, "Параметр P2 должен восстановиться");
        assertEquals(32, p2.getValue());
        assertEquals(OracleTypes.NUMBER, p2.getParamType());
        assertEquals(ParamMode.IN_OUT, p2.getParamMode());
    }

    @SneakyThrows
    @Test
    void serializeEmptyExpectation() {
        ParamsExpectationData expectation = new ParamsExpectationData();

        String yaml = ymlMapper.writeValueAsString(expectation);
        log.info("Serialized empty ParamsExpectationData:\n{}", yaml);

        ExpectationData restored = ymlMapper.readValue(yaml, ExpectationData.class);
        assertInstanceOf(ParamsExpectationData.class, restored);
        ParamsExpectationData restoredParams = (ParamsExpectationData) restored;
        assertEquals(ValidationType.TO_BE_EQUAL, restoredParams.getValidationType());
        assertNotNull(restoredParams.getParams());
        assertTrue(restoredParams.getParams().isEmpty(), "Пустой набор параметров должен остаться пустым");
    }
}

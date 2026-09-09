package ru.sovcombank.rbs.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.core.TestResultData;
import ru.sovcombank.rbs.core.ValidationType;
import ru.sovcombank.rbs.core.Validator;
import ru.sovcombank.rbs.expectations.ParamsExpectationData;

import java.util.List;


@Component
@Slf4j
public class JsonValidator implements Validator<ParamsExpectationData> {

    @Qualifier("ParamsJsonMapper")
    @Autowired
    private JsonMapper jsonMapper;

    @Override
    public final ValidationType getType() {
        return ValidationType.TO_BE_EQUAL;
    }

    @Override
    public void validate(ParamsExpectationData expectationData, TestResultData testResultData) throws AssertionError {
        log.debug("expectationData: {}", expectationData);
        log.debug("testResultData: {}", testResultData);

        if (null == expectationData || null == testResultData) {
            throw new IllegalArgumentException("Входные параметры должны быть заданы");
        }
        if (null == testResultData.getValue()) {
            throw new AssertionError("В ответ от сервера получили NULL, а ожидали набор параметров");
        }
        String incomingJson = testResultData.getJson();
        String expectJson = "";

        try {
            expectJson = jsonMapper.writeValueAsString(expectationData.getParams());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

        log.debug("Ожидаем: {}", expectJson);
        log.debug("Факт: {}", incomingJson);
        if (!compareJson(expectJson, incomingJson)) {
            log.debug("Тест провален");
            throw new AssertionError("Полученные параметры отличаются от ожидаемых");
        } else log.debug("Тест успешно пройден");
    }

    private void validateAsDbParams(ParamsExpectationData expectationData, TestResultData testResultData) throws AssertionError {
        // Полученное значение должно быть списокм параметров и набор параметров дожен совпасть с ожидаемым
        if (testResultData.getValue() instanceof List<?> list) {
            // Если ожидали пустой список и его получили, то всё хорошо
            if (list.isEmpty() && expectationData.getParams().isEmpty()) return;

            if (list.size() != expectationData.getParams().size()) {
                throw new AssertionError("Количество параметров не совпало с ожиданием, получено " + list.size());
            }

            if (!expectationData.getParams().equals(list)) {
                throw new AssertionError("Не совпали ожидаемые и реальные параметры ");
            }
        } else {
            throw new AssertionError("Ожидался набор параметров, а получен " + testResultData.getValue());
        }
    }

    /// Сравнивает строки как json без учета порядка объектов и форматирования
    /// Пустые строки считаем равными
    private boolean compareJson(String json1, String json2) {
        // Пустые строки считаем равными
        if (json1.isBlank() && json2.isBlank()) {
            return true;
        }

        try {
            JsonNode node1 = jsonMapper.readTree(json1);
            JsonNode node2 = jsonMapper.readTree(json2);
            return node1.equals(node2);
        } catch (JsonMappingException e) {
            log.warn("Ошибка чтения JSON: {}", e.getMessage());
            return false;
        } catch (JsonProcessingException e) {
            log.warn("Ошибка обработки JSON: {}", e.getMessage());
            return false;
        }
    }
}
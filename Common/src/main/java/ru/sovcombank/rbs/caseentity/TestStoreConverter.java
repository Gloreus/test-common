package ru.sovcombank.rbs.caseentity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.core.BlockType;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.DbTypes;
import ru.sovcombank.rbs.core.ParamMode;
import ru.sovcombank.rbs.core.TestCaseData;
import ru.sovcombank.rbs.expectations.ParamsExpectationData;
import ru.sovcombank.rbs.ora.OracleTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/// Конвертор входных YAML-файлов из папки input в объекты TestCase
@Slf4j
@Component
public class TestStoreConverter {

    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;

    @Autowired
    public TestStoreConverter(@Qualifier("YmlMapper") ObjectMapper yamlMapper,
                              @Qualifier("ParamsJsonMapper") ObjectMapper jsonMapper) {
        this.yamlMapper = yamlMapper;
        this.jsonMapper = jsonMapper;
    }

    /// Читает входной YAML-файл и возвращает список тест-кейсов
    public List<TestCase> readTestCases(Path inputFile) throws IOException {
        InputTest inputTest = yamlMapper.readValue(Files.newInputStream(inputFile), InputTest.class);
        if (inputTest == null || inputTest.getTest() == null) {
            throw new IOException("Входной файл не содержит узла Test: " + inputFile);
        }
        List<InputCase> inputCases = inputTest.getTest().getCases();
        List<TestCase> result = new ArrayList<>(inputCases.size());
        for (InputCase inputCase : inputCases) {
            result.add(convert(inputCase));
        }
        return result;
    }

    /// Преобразует один входной кейс в TestCase
    private TestCase convert(InputCase inputCase) throws IOException {
        TestCase testCase = new TestCase();

        TestCaseData data = new TestCaseData();
        data.setDescription(inputCase.getCaption() == null ? "" : inputCase.getCaption());
        data.setBlockSql(inputCase.getCode());
        data.setBlockType(BlockType.BLOCK);

        InputBindsData input = parseBinds(inputCase.getInput());
        if (input != null) {
            List<DbParam> params = buildParams(input);
            if (!params.isEmpty()) {
                data.setParams(params);
            }
        }
        testCase.setTestCaseData(data);

        InputBindsData result = parseBinds(inputCase.getResult());
        if (result != null) {
            ParamsExpectationData expectation = buildExpectation(result);
            if (expectation.getParams().size() > 0) {
                testCase.getExpectations().add(expectation);
            }
        }

        return testCase;
    }

    /// Разбирает JSON-строку input/result. Возвращает null, если это не binds (например, error)
    private InputBindsData parseBinds(String json) throws IOException {
        if (json == null || json.isBlank()) {
            return null;
        }
        InputBindsData binds = jsonMapper.readValue(json, InputBindsData.class);
        if (binds == null || !"binds".equalsIgnoreCase(binds.getResultType())) {
            return null;
        }
        return binds;
    }

    /// Строит список параметров из inputBinds/outputBinds
    private List<DbParam> buildParams(InputBindsData binds) {
        Map<String, DbParam> params = new LinkedHashMap<>();

        // Сначала входные параметры
        for (InputBindData bind : binds.getInputBinds()) {
            DbParam param = new DbParam();
            param.setName(bind.getName());
            param.setParamType(mapType(bind.getType()));
            param.setValue(bind.getValue());
            param.setParamMode(ParamMode.IN);
            params.put(bind.getName(), param);
        }

        // Затем выходные: если параметр уже есть - IN_OUT (значение остаётся входным), иначе OUT
        for (InputBindData bind : binds.getOutputBinds()) {
            DbParam existing = params.get(bind.getName());
            if (existing != null) {
                existing.setParamMode(ParamMode.IN_OUT);
            } else {
                DbParam param = new DbParam();
                param.setName(bind.getName());
                param.setParamType(mapType(bind.getType()));
                param.setValue(bind.getValue());
                param.setParamMode(ParamMode.OUT);
                params.put(bind.getName(), param);
            }
        }

        return new ArrayList<>(params.values());
    }

    /// Строит ожидание по выходным параметрам результата
    private ParamsExpectationData buildExpectation(InputBindsData result) {
        ParamsExpectationData expectation = new ParamsExpectationData();
        for (InputBindData bind : result.getOutputBinds()) {
            DbParam param = new DbParam();
            param.setName(bind.getName());
            param.setParamType(mapType(bind.getType()));
            param.setValue(bind.getValue());
            param.setParamMode(ParamMode.OUT);
            expectation.getParams().add(param);
        }
        return expectation;
    }

    /// Сопоставляет строковый тип из JSON с OracleTypes
    private DbTypes mapType(String type) {
        if (type == null) {
            return OracleTypes.VARCHAR2;
        }
        // todo: добавить типы в OracleTypes
        return switch (type.toLowerCase()) {
            case "number", "integer", "pls_integer", "binary_integer" -> OracleTypes.NUMBER;
            case "float", "binary_float" -> OracleTypes.FLOAT;
            case "binary_double" -> OracleTypes.DOUBLE;
            case "varchar2" -> OracleTypes.VARCHAR2;
            case "char" -> OracleTypes.CHAR;
            case "nvarchar2" -> OracleTypes.NVARCHAR2;
            case "nchar" -> OracleTypes.NCHAR;
            case "date" -> OracleTypes.DATE;
            case "timestamp" -> OracleTypes.TIMESTAMP;
            case "timestamp_with_time_zone" -> OracleTypes.TIMESTAMP_WITH_TIME_ZONE;
            case "timestamp_with_local_time_zone" -> OracleTypes.TIMESTAMP_WITH_TIME_ZONE;
            case "clob" -> OracleTypes.CLOB;
            case "nclob" -> OracleTypes.NCLOB;
            case "blob" -> OracleTypes.BLOB;
            case "raw" -> OracleTypes.RAW;
            case "rowid" -> OracleTypes.ROWID;
            case "urowid" -> OracleTypes.ROWID;
            case "bfile" -> OracleTypes.BFILE;
            case "interval_year_to_month", "interval_day_to_second" -> OracleTypes.INTERVAL;
            default -> OracleTypes.VARCHAR2;
        };
    }
}

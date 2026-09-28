package ru.sqbt.plsqltests.manager.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.core.*;
import ru.sovcombank.rbs.ora.OracleTypes;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class TestStoryReader {
    private final ObjectMapper mapper;
    private final JsonMapper jsonMapper;

    public TestStoryReader(@NonNull @Qualifier("YmlCaseMapper") ObjectMapper mapper, @Qualifier("ParamsJsonMapper") JsonMapper jsonMapper) {
        this.mapper = mapper;
        this.jsonMapper = jsonMapper;
    }

    public TestStories readYam(Path pathToYml) throws IOException {
        log.debug("Читаем {}", pathToYml);
        try (InputStream stream =  Files.newInputStream(pathToYml)) {
            return mapper.readValue(stream, TestStories.class);
        } catch (IOException e) {
            log.error("Не смог прочитать тесты из {}, {}" , pathToYml.toString(), e.getMessage());
            throw new IOException("Не смог прочитать тесты из " + pathToYml.toString());
        }
    }

    public List<TestCase> buldFromTestStries(TestStories testStories) {
        List<TestCase> result = new ArrayList<>(30);
        testStories.getCases().forEach(testStoryCase -> {
            TestCase testCase = new TestCase();
            TestCaseData testCaseData = new TestCaseData();
            testCaseData.setUid(Integer.toString(testStoryCase.getId()));
            testCaseData.setDescription(testStoryCase.getCaption());
            testCaseData.setBlockType(BlockType.BLOCK);
            testCaseData.setBlockSql(testStoryCase.getCode());
            testCaseData.setTransactionMode(TransactionMode.ROLLBACK); // hardcode
            List<DbParam> params = new ArrayList<>(6);

            TestStoryBinds binds = null;
            try {
                binds = jsonMapper.readValue(testStoryCase.getInput(), TestStoryBinds.class);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            if (binds != null) {
                binds.getInputBinds().forEach(testStoryParam -> {
                    DbParam param = new DbParam();
                    param.setName(testStoryParam.getName());
                    param.setParamMode(ParamMode.IN_OUT);
                    param.setParamType(OracleTypes.VARCHAR2);
                    param.setValue(testStoryParam.getValue());
                    params.add(param);
                });
                testCaseData.setParams(params);
                testCase.setTestCaseData(testCaseData);
                result.add(testCase);
            }
        });
        return result;
    }
}

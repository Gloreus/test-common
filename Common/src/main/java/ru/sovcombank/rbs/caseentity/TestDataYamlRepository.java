package ru.sovcombank.rbs.caseentity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.TestStoreProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TestDataYamlRepository implements TestDataRepository {
    private final TestStoreProperties testStoreProperties;

    @Autowired
    @Qualifier("YmlMapper")
    private ObjectMapper yamlMapper;

    public TestDataYamlRepository(TestStoreProperties testStoreProperties) {
        this.testStoreProperties = testStoreProperties;
        log.debug(this.testStoreProperties.toString());
    }

    public static String getFileName(String profileName) {
        int n = profileName.lastIndexOf(".");
        if (-1 == n) {
            return profileName + ".yaml";
        } else if (profileName.substring(n + 1).equalsIgnoreCase("yaml")
                || profileName.substring(n + 1).equalsIgnoreCase("yml")) {
            return profileName;
        } else {
            log.error("Файл профиля должен быть yaml, а получен {}", profileName);
            throw new IllegalArgumentException("Файл профиля должен быть типа .yaml, а получен " + profileName);
        }
    }

    @Override
    public TestProfile loadProfile(@NonNull String profileName) throws IOException {
        String fname = getFileName(profileName);
        Path p = testStoreProperties.getProfilesFullPath().resolve(fname);
        log.debug("Profile file name: {}", p);

        InputStream inputStream = Files.newInputStream(p, StandardOpenOption.READ);
        if (null == inputStream) {
            throw new FileNotFoundException("Test profile file not found: " + fname);
        }
        try {
            return yamlMapper.readValue(inputStream, TestProfile.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public TestCase loadCase(@NonNull TestCaseReference reference) throws IOException {
        String fname = getFileName(reference.getFilePath());

        Path p = testStoreProperties.getCasesFullPath().resolve(fname);
        log.debug("Case file name: {}", p);

        InputStream inputStream = Files.newInputStream(p, StandardOpenOption.READ);

        if (null == inputStream) {
            throw new FileNotFoundException("Test case file not found: " + fname);
        }
        try {
            return yamlMapper.readValue(inputStream, TestCase.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TestCase> loadCases(@NonNull TestProfile profile) {
        log.info("Загружаю кейсы для " + profile.getProfileName());
        List<TestCase> testCases = new ArrayList<>(20);
        for (TestCaseReference testCaseReference : profile.getReferences()) {
            TestCase testCase = null;
            try {
                testCase = loadCase(testCaseReference);
                log.debug(testCase.toString());
                testCases.add(testCase);
            } catch (FileNotFoundException e) {
                log.warn(e.getMessage());
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
        log.info("Успешно загружено: " + Integer.toString(testCases.size()));
        return testCases;
    }

    @Override
    public String writeCaseToString(TestCase testCase) throws JsonProcessingException {
        return yamlMapper.writeValueAsString(testCase);
    }
}

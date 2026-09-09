package ru.sovcombank.rbs.caseentity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TestDataYamlRepository implements TestDataRepository {

    @Value("${plsqltests.repopath}")
    private static String REPO_PATH = "tests";
    @Value("${plsqltests.profilespath}")
    private static String PROFILES_PATH = "profiles";
    @Value("${plsqltests.casespath}")
    private static String CASES_PATH = "cases";

    private final String profilesPath = FilenameUtils.concat(REPO_PATH, PROFILES_PATH);
    private final String casesPath = FilenameUtils.concat(REPO_PATH, CASES_PATH);
    @Autowired
    @Qualifier("YmlMapper")
    private ObjectMapper yamlMapper;

    @Override
    public TestProfile loadProfile(@NonNull String profileName) throws FileNotFoundException {

        String fname = getFileName(profileName);
        fname = FilenameUtils.concat(profilesPath, fname);
        log.debug("Profile file name: {}", fname);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fname);
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

    public static String getFileName(String profileName) {
        int n = profileName.lastIndexOf(".");
        if (-1 == n) {
            return profileName + ".yaml";
        } else if (profileName.substring(n + 1).equalsIgnoreCase("yaml")) {
            return profileName;
        } else {
            log.error("Файл профиля должен быть yaml, а получен {}", profileName);
            throw new IllegalArgumentException("Файл профиля должен быть типа .yaml, а получен " + profileName);
        }
    }

    public TestCase loadCase(@NonNull TestCaseReference reference) throws FileNotFoundException {
        String fname = getFileName(reference.getFilePath());
        fname = FilenameUtils.concat(casesPath, fname);
        log.debug("Load test case from: {}", fname);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fname);
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
            }
        }
        log.info("Успешно загружено: " + Integer.toString(testCases.size()));
        return testCases;
    }

}

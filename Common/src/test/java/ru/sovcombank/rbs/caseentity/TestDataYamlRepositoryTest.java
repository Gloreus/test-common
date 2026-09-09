package ru.sovcombank.rbs.caseentity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.sovcombank.rbs.TestCommonApp;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@SpringBootTest(classes = {TestCommonApp.class})
@Slf4j
class TestDataYamlRepositoryTest {
    @Autowired
    private TestDataYamlRepository repository;

    @Test
    void testLoadProfile() throws FileNotFoundException {
        assertThrowsExactly(FileNotFoundException.class,
                () -> repository.loadProfile("1233333"));
        TestProfile profile = repository.loadProfile("123");
        log.info("Profile: {}", profile);
        assertNotNull(profile);
    }

    @Test
    void testLoadCases() throws FileNotFoundException {
        TestProfile profile = repository.loadProfile("123");
        List<TestCase> caseList = repository.loadCases(profile);
        log.info("Найдено тест-кейсов: {}", caseList.size());
        assertNotNull(caseList);
    }

    @Test
    void getFileName() {
        assertEquals(repository.getFileName("123"), "123.yaml");
        assertEquals(repository.getFileName("123.yaML"), "123.yaML");
        assertEquals(repository.getFileName("2/1.2.123.yaML"), "2/1.2.123.yaML");
        assertThrowsExactly(IllegalArgumentException.class,
                () -> repository.getFileName("1.txt"));
    }
}
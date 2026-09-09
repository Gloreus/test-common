package ru.sovcombank.rbs;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;

import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@Disabled
class CheckTestServiceTest {
    private final TestStoreProperties testStoreProperties = new TestStoreProperties();
    /*
    @Autowired
    private TestDataYamlRepository repository;
    @Autowired
    private CheckTestService service;
*/


    @Test
    void testCheckAllTest() throws FileNotFoundException {
        /*
        TestProfile profile = repository.loadProfile("123");
        log.info(profile.toString());
        service.checkAllTest(profile);
        */

    }

    @Test
    void testGetRunnerNames() {
        //  log.info(service.getRunnerNames().toString());
    }

    @Test
    public void getCasesPathTest() {

    }

    @Test
    public void getProfilesPathTest() {

    }

    @Test
    public void getRootPathTest() {

    }
}
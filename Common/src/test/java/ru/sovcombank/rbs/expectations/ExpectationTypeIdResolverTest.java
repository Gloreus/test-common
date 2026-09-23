package ru.sovcombank.rbs.expectations;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.sovcombank.rbs.TestCommonApp;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = {TestCommonApp.class})
class ExpectationTypeIdResolverTest {
    private ExpectationTypeIdResolver resolver = new ExpectationTypeIdResolver();

    private ParamsExpectationData paramsExpectationData = new ParamsExpectationData();
    private OutputExpectationData outputExpectationData = new OutputExpectationData();

    private DatabindContext context = Mockito.mock(DatabindContext.class);

    @BeforeEach
    void setUp() {
        Mockito.when(context.getTypeFactory())
                .thenReturn(TypeFactory.defaultInstance());
    }

    @Test
    void idFromValueTest1() {
        String id = resolver.idFromValue(paramsExpectationData);
        log.debug("id = {}", id);
        assertEquals(paramsExpectationData.getClass().getSimpleName(), id, "Не определили класс");
    }

    @Test
    void idFromValueTest2() {
        String id = resolver.idFromValue(outputExpectationData);
        log.debug("id = {}", id);
        assertEquals(outputExpectationData.getClass().getSimpleName(), id, "Не определили класс");
    }

    @Test
    void idFromValueAndType() {
        String id = resolver.idFromValueAndType(outputExpectationData, OutputExpectationData.class);
        log.debug("id = {}", id);
        assertEquals(outputExpectationData.getClass().getSimpleName(), id, "Не определили класс");
    }

    @Test
    void typeFromId() {
        JavaType jt = resolver.typeFromId(context, "OutputExpectationData");
        assertEquals(OutputExpectationData.class, jt.getRawClass(), "Не определили класс по Id" );
    }
}
package ru.sovcombank.rbs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.DbParams;
import ru.sovcombank.rbs.core.DbTypes;
import ru.sovcombank.rbs.core.ExpectationData;
import ru.sovcombank.rbs.expectations.ExpectationDataJsonDeserializer;
import ru.sovcombank.rbs.ora.DbParamJsonSerializer;
import ru.sovcombank.rbs.ora.DbParamListJsonDeserializer;
import ru.sovcombank.rbs.ora.DbParamListJsonSerializer;
import ru.sovcombank.rbs.ora.DbTypesJsonDeserializer;

@Configuration
public class YamlConfig {
    @Bean("YmlMapper")
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new SimpleModule() {{
                addDeserializer(DbTypes.class, new DbTypesJsonDeserializer());
                addDeserializer(ExpectationData.class, new ExpectationDataJsonDeserializer());
            }
        });
        return mapper;
    }

    @Bean("ParamsJsonMapper")
    public JsonMapper jsonMapper() {
        JsonMapper mapper = new JsonMapper();
        SimpleModule module =
                new SimpleModule().addSerializer(DbParam.class, new DbParamJsonSerializer())
                        .addSerializer(DbParams.class, new DbParamListJsonSerializer())
                        .addDeserializer(DbParams.class, new DbParamListJsonDeserializer());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(module);
        return mapper;
    }
}

package ru.sovcombank.rbs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.DbParams;
import ru.sovcombank.rbs.core.DbTypes;
import ru.sovcombank.rbs.core.ExpectationData;
import ru.sovcombank.rbs.expectations.ExpectationTypeIdResolver;
import ru.sovcombank.rbs.ora.DbParamJsonSerializer;
import ru.sovcombank.rbs.ora.DbParamListJsonDeserializer;
import ru.sovcombank.rbs.ora.DbParamListJsonSerializer;
import ru.sovcombank.rbs.ora.DbTypesJsonDeserializer;

@Configuration
public class YamlConfig {

    @Bean("YmlMapper")
    public ObjectMapper objectMapper() {
        YAMLMapper mapper = new YAMLMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(DbParams.class, new DbParamListJsonSerializer());
        module.addDeserializer(DbTypes.class, new DbTypesJsonDeserializer());

        mapper.registerModule(module);
        mapper.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID); // отключаем нативные ID
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

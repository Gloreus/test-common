package ru.sovcombank.rbs.ora;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.DbParams;

import java.io.IOException;

@Component
public class DbParamListJsonSerializer extends JsonSerializer<DbParams> {
    @Override
    public void serialize(DbParams value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        for (DbParam param : value) {
            jsonGenerator.writeFieldName(param.getName());

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("paramType", param.getParamType());
            jsonGenerator.writeObjectField("paramMode", param.getParamMode());
            jsonGenerator.writeObjectField("value", param.getValue());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndObject();
    }
}

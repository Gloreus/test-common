package ru.sovcombank.rbs.ora;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.core.DbParam;

import java.io.IOException;

@Component
public class DbParamJsonSerializer extends JsonSerializer<DbParam> {
    @Override
    public void serialize(DbParam value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName(value.getName());

        gen.writeStartObject();
        gen.writeObjectField("paramType", value.getParamType());
        gen.writeObjectField("paramMode", value.getParamMode());
        gen.writeObjectField("value", value.getValue());
        gen.writeEndObject();

        gen.writeEndObject();
    }

}

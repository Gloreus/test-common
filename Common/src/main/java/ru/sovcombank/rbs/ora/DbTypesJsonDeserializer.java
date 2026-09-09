package ru.sovcombank.rbs.ora;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import ru.sovcombank.rbs.core.DbTypes;

import java.io.IOException;

@Slf4j
public class DbTypesJsonDeserializer extends JsonDeserializer<DbTypes> {
    @Override
    public DbTypes deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String typeName = p.getText();
        try {
            log.debug("DbType: {}", typeName);
            return OracleTypes.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            throw new IOException("Unknown database type: " + typeName, e);
        }
    }
}
package ru.sovcombank.rbs.ora;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.core.DbParam;
import ru.sovcombank.rbs.core.DbParams;

import java.io.IOException;

@Component
public class DbParamListJsonDeserializer extends JsonDeserializer<DbParams> {
    @Override
    public DbParams deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode arrayNode = mapper.readTree(jsonParser);
        DbParams result = new DbParams();
        for (JsonNode jsonNode : arrayNode) {
            result.add(mapper.treeToValue(jsonNode, DbParam.class));
        }
        return result;
    }
}

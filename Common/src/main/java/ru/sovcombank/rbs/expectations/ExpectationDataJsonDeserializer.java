package ru.sovcombank.rbs.expectations;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import ru.sovcombank.rbs.core.ExpectationData;

import java.io.IOException;

@Slf4j
public class ExpectationDataJsonDeserializer extends JsonDeserializer<ExpectationData> {
    @Override
    public ExpectationData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String basePack = getClass().getPackageName();
        ObjectCodec codec = p.getCodec();

        ObjectNode node = codec.readTree(p);

        // Получаем полное имя класса из поля @class
        String className = node.get("expectClass").asText();
        log.debug("ExpectationData class: {}", className);
        try {
            // Загружаем класс по имени
            Class<?> clazz = Class.forName(basePack + "." + className);

            // Проверяем, что класс является наследником ExpectationData
            if (!ExpectationData.class.isAssignableFrom(clazz)) {
                throw new IOException("Class " + className + " is not a subclass of ExpectationData");
            }
            // Создаём ObjectMapper для десериализации в конкретный тип
            ObjectMapper mapper = (ObjectMapper) codec;
            node.remove("expectClass");
            return (ExpectationData) mapper.convertValue(node, clazz);
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found: " + className, e);
        }
    }
}

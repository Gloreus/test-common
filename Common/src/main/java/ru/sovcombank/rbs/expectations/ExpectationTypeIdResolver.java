package ru.sovcombank.rbs.expectations;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import ru.sovcombank.rbs.core.ExpectationData;

public class ExpectationTypeIdResolver extends TypeIdResolverBase {
    private static final String BASE_PACKAGE = "ru.sovcombank.rbs.expectations";

    @Override
    public String idFromValue(Object value) {
        return value.getClass().getSimpleName();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return idFromValue(value);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        try {
            String fullClassName = BASE_PACKAGE + "." + id;
            Class<?> cls = Class.forName(fullClassName);
            if (!ExpectationData.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException("Класс " + fullClassName + " не реализует ExpectationData");
            }
            return context.getTypeFactory().constructType(cls);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Не найден класс: " + id, e);
        }
    }
}

package ru.sovcombank.rbs.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import ru.sovcombank.rbs.expectations.ExpectationTypeIdResolver;

///  Ожидаемый результат выполнения тест-кейса
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "expectClass"
)
@JsonTypeIdResolver(ExpectationTypeIdResolver.class)
public interface ExpectationData {
    /// Способ проверки
    ValidationType getValidationType();

    /// Тип ожидаемого значения
    DbTypes getExpectType();
}

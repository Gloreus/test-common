package ru.sovcombank.rbs.expectations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sovcombank.rbs.core.DbParams;
import ru.sovcombank.rbs.core.DbTypes;
import ru.sovcombank.rbs.core.ExpectationData;
import ru.sovcombank.rbs.core.ValidationType;
import ru.sovcombank.rbs.ora.DbParamListJsonDeserializer;

@Data
@AllArgsConstructor
public class ParamsExpectationData implements ExpectationData {
    private ValidationType validationType;
    /// Набор входных и выходных параметров
    @JsonDeserialize(using = DbParamListJsonDeserializer.class)
    private DbParams params;

    public ParamsExpectationData() {
        validationType = ValidationType.TO_BE_EQUAL;
        params = new DbParams();
    }

    @Override
    public DbTypes getExpectType() {
        return null;
    }
}

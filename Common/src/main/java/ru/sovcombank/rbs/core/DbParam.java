package ru.sovcombank.rbs.core;

import lombok.Data;

import java.util.Objects;

@Data
///  Параметр запроса или процедуры
public class DbParam {
    private String name;
    private Object value;
    private DbTypes paramType;
    /// In-Out
    private ParamMode paramMode;

    @SuppressWarnings("CallToSimpleSetterFromWithinClass")
    public static DbParam createReturnig(DbValue value, DbTypes dbType) {
        DbParam param = new DbParam();
        param.setParamMode(ParamMode.OUT);
        param.setName("return");
        param.setValue(value.getValue());
        param.setParamType(dbType);
        return param;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DbParam param)) {
            return false;
        } else {
            return Objects.equals(name, param.name) &&
                    paramMode == param.paramMode &&
                    Objects.equals(value, param.value) &&
                    Objects.equals(paramType, param.paramType);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paramMode, paramType, value);
    }
}

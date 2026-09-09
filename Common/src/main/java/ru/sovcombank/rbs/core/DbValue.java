package ru.sovcombank.rbs.core;

import lombok.Data;
import ru.sovcombank.rbs.ora.OracleTypes;

/// Возвращаемое значение, например, результат функции
@Data
public class DbValue {
    private Object value;
    private DbTypes paramType;

    public DbValue(DbTypes paramType, Object value) {
        this.paramType = paramType;
        this.value = value;
    }

    public DbValue() {
        paramType = OracleTypes.VARCHAR2;
        value = "";
    }
}

package ru.sovcombank.rbs.ora;

import ru.sovcombank.rbs.core.DbTypes;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Types;
import java.time.LocalDateTime;

public enum OracleTypes implements DbTypes {
    // Числовые типы
    NUMBER(Types.NUMERIC),          // Общий тип для NUMBER
    INTEGER(Types.INTEGER),         // Для целых чисел
    FLOAT(Types.FLOAT),             // Для чисел с плавающей точкой
    DOUBLE(Types.DOUBLE),           // Для BINARY_DOUBLE

    // Строковые типы
    VARCHAR2(Types.VARCHAR),        // Основной строковый тип
    CHAR(Types.CHAR),               // Фиксированная длина
    NVARCHAR2(Types.NVARCHAR),      // Юникодные строки
    NCHAR(Types.NCHAR),             // Фиксированные юникодные строки

    // Даты и время
    DATE(Types.DATE),               // Дата и время
    TIMESTAMP(Types.TIMESTAMP),     // Точное время с секундами
    TIMESTAMP_WITH_TIME_ZONE(Types.TIMESTAMP_WITH_TIMEZONE),

    // BLOB/CLOB
    CLOB(Types.CLOB),               // Текстовые большие объекты
    NCLOB(Types.NCLOB),             // Юникодные CLOB
    BLOB(Types.BLOB),               // Бинарные данные

    // Логический тип (Oracle не имеет native BOOLEAN, но часто эмулируется)
    BOOLEAN(Types.BOOLEAN),         // Для логических значений (0/1, Y/N, TRUE/FALSE)

    // Для REF CURSOR (PL/SQL)
    CURSOR(Types.REF_CURSOR),

    // Для RAW (бинарные данные фиксированной длины)
    RAW(Types.VARBINARY),

    // Для XMLType 
    XMLTYPE(Types.SQLXML),

    // Для ROWID (часто передаётся как строка)
    ROWID(Types.VARCHAR),

    // Для массивов (PL/SQL TABLE, VARRAY)
    ARRAY(Types.ARRAY),

    // Для объектных типов (STRUCT)
    OBJECT(Types.STRUCT),

    // Для REF (указатель на объект)
    REF(Types.REF),

    // Для PL/SQL PLS_INTEGER / BINARY_INTEGER
    PLS_INTEGER(Types.INTEGER),
    BINARY_INTEGER(Types.INTEGER),

    // Для LONG (устаревший, но иногда встречается)
    LONG(Types.LONGVARCHAR),

    // Для LONG RAW (устаревший)
    LONG_RAW(Types.LONGVARBINARY),

    // Для JSON (Oracle 12c+ — часто как CLOB)
    JSON(Types.CLOB),

    // Для INTERVAL (если нужно — как строка или OTHER)
    INTERVAL(Types.OTHER),

    // Для BFILE (внешние файлы — обычно не поддерживается напрямую)
    BFILE(Types.OTHER);


    // Методы и конструкторы
    private int jdbcType;

    OracleTypes(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    public int getJdbcType() {
        return jdbcType;
    }

    @Override
    public Class<?> getJavaType() {

        switch (this) {
            // Числовые типы
            case NUMBER:
                return BigDecimal.class;

            case INTEGER:
            case PLS_INTEGER:
            case BINARY_INTEGER:
                return long.class;

            case FLOAT:
            case DOUBLE:
                return Double.class;

            // Строковые типы
            case VARCHAR2:
            case CHAR:
            case NVARCHAR2:
            case NCHAR:
            case LONG:
            case ROWID:
                return String.class;

            // LOB типы
            case CLOB:
            case NCLOB:
            case JSON:
                return String.class;

            // Бинарные типы
            case BLOB:
            case RAW:
            case LONG_RAW:
                return byte[].class;

            // Дата/время
            case DATE:
            case TIMESTAMP:
            case TIMESTAMP_WITH_TIME_ZONE:
                return LocalDateTime.class;

            // Логический тип
            case BOOLEAN:
                return Boolean.class;

            // XML
            case XMLTYPE:
                return String.class;  // или javax.xml.transform.Source

            // Специфичные типы Oracle
            case CURSOR:
                return Array.class;
            case ARRAY:
                return Array.class;
            case OBJECT:
                return Object.class;
            case REF:
                return Object.class;
            case INTERVAL:
            case BFILE:
                return String.class;  // или специальные Oracle классы

            default:
                return Object.class;
        }
    }

}
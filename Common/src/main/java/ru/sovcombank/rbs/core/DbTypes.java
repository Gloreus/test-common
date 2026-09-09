package ru.sovcombank.rbs.core;

///  Тип данных для параметров и переменных в тест-кейсах
public interface DbTypes {
    ///  Возвращает подходящий jdbc-тип
    int getJdbcType();

    ///  Возвращает подходящий java-тип
    Class<?> getJavaType();
}
package ru.sovcombank.rbs.core;

import lombok.Data;

import java.util.List;

/// Описание тест-кейса, атрибуты для выполнения на БД
@Data
public class TestCaseData {
    /// Уникальный ID кейса (GUID), не обязательно
    private String uid;
    /// Описание теста
    private String description = "";
    ///  Исполняемый код
    private String blockSql;
    private TransactionMode transactionMode = TransactionMode.ROLLBACK;
    private BlockType blockType = BlockType.BLOCK;
    /// Для блоков типа FUNCTION - тип возвращаемого значения
    private DbTypes returningType;
    /// Набор входных и выходных параметров
    private List<DbParam> params;
}

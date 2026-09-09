package ru.sovcombank.rbs.caseentity;

import lombok.Data;

@Data
public class TestCaseReference {
    public static final String DEFAULT_ID = "";
    ///  Уникальный ID тесткейса, должен ссответствовать ID в файле
    private String testId = DEFAULT_ID;
    /// Путь к файлу тесткейса относительно корня репо
    private String filePath;
}

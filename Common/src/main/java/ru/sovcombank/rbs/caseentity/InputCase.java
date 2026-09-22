package ru.sovcombank.rbs.caseentity;

import lombok.Data;

/// Один тест-кейс из входного YAML
@Data
public class InputCase {
    private int id;
    private String caption;
    private String code;
    private String input;
    private String result;
    private String comment;
}

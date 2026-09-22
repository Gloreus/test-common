package ru.sovcombank.rbs.caseentity;

import lombok.Data;

/// Один бинд-параметр из JSON input/result
@Data
public class InputBindData {
    private String name;
    private String type;
    private Object value;
}

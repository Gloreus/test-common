package ru.sqbt.plsqltests.manager.model;

import lombok.Data;

/// Один бинд-параметр из JSON input/result
@Data
public class TestStoryParam {
    private String name;
    private String type;
    private Object value;
}

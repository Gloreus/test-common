package ru.sovcombank.rbs.caseentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/// Корневой узел входного YAML-файла из папки input
@Data
public class InputTest {
    @JsonProperty("Test")
    private InputTestData test;
}

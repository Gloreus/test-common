package ru.sovcombank.rbs.caseentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/// JSON-структура input/result с типом binds
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InputBindsData {
    private String resultType;
    private List<InputBindData> inputBinds = new ArrayList<>(0);
    private List<InputBindData> outputBinds = new ArrayList<>(0);
}

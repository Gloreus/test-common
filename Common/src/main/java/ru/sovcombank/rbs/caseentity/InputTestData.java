package ru.sovcombank.rbs.caseentity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/// Метаданные набора тестов и список кейсов из входного YAML
@Data
public class InputTestData {
    private String chapter;
    private String jira_issue;
    private String name;
    private String caption;
    private String comment;
    private List<InputCase> cases = new ArrayList<>(0);
}

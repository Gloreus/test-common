package ru.sqbt.plsqltests.manager.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/// Корневой узел входного YAML-файла из папки input
@Data
@JsonRootName("Test")
public class TestStories {
    private String chapter;
    private String jira_issue;
    private String name;
    private String caption;
    private String comment;
    private List<TestStoryCase> cases = new ArrayList<>(0);
}

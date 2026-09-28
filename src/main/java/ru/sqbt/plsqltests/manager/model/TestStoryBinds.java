package ru.sqbt.plsqltests.manager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStoryBinds {
    private String resultType;
    private List<TestStoryParam> inputBinds = new ArrayList<>(0);
    private List<TestStoryParam> outputBinds = new ArrayList<>(0);
}

package ru.sovcombank.rbs.core;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ComponentScan(basePackages = "ru.sqbt.plsqltests")
public class TestCaseRunnerFactory {

    private final Map<String, TestCaseRunner<?>> runnerMap;

    public TestCaseRunnerFactory(List<TestCaseRunner<?>> runners) {
        this.runnerMap = runners.stream()
                .collect(Collectors.toMap(TestCaseRunner::getSystem, runner -> runner));
    }

    public TestCaseRunner<?> getTestCaseRunner(String systemName) {
        TestCaseRunner<?> runner = runnerMap.get(systemName);
        if (null == runner) {
            throw new IllegalArgumentException("No test case runner found for system: " + systemName);
        }
        return runner;
    }

    public List<String> getAllSystems() {
        return runnerMap.keySet().stream().toList();
    }

    public Collection<TestCaseRunner<?>> getRunners() {
        return runnerMap.values();
    }

}
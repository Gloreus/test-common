package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.core.TestCaseData;

import java.util.List;

@Slf4j
public class TestCaseForm extends Composite<VerticalLayout> {
    private TestCase testCase;
    private final H2 description = new H2();
    private final Pre codeTextArea = new Pre();
    private final ParamGrid inputParamGrid = new ParamGrid("Тестовые данные:");
    private final ParamGrid resultParamGrid = new ParamGrid("Ожидаемый результат");

    public TestCaseForm() {
        var layout = getContent();
        layout.setMinWidth("8em");
        layout.setMinHeight("4em");
        layout.add(description);

        codeTextArea.setWidthFull();
        codeTextArea.addClassName("code-text-detail");
        layout.add(codeTextArea);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.setPadding(false);
        horizontalLayout.setWidthFull();
        horizontalLayout.setSpacing("1em");
        horizontalLayout.add(inputParamGrid, resultParamGrid);
        layout.add(horizontalLayout);
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
        TestCaseData td = testCase.getTestCaseData();
        String s = String.format("[%s] %s", td.getUid(), td.getDescription());
        description.setText(s);
        codeTextArea.setText(td.getBlockSql());
        inputParamGrid.setItems(td.getParams());
        resultParamGrid.setItems(td.getParams());
    }

    public void clear() {
        this.testCase = null;
        description.setText("");
        codeTextArea.setText("");
        inputParamGrid.setItems(List.of());
        resultParamGrid.setItems(List.of());
    }
}

package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.core.TestCaseData;

@Slf4j
public class TestCaseForm extends Composite<VerticalLayout> {
    private TestCase testCase;
    private final H2 description = new H2();
    private final TextArea codeTextArea = new TextArea();
    private final ParamGrid inputParamGrid = new ParamGrid();
    private final ParamGrid resultParamGrid = new ParamGrid();

    public TestCaseForm() {
        var layout = getContent();
        layout.setMinWidth("8em");
        layout.setMinHeight("8em");
        layout.add(description);
        codeTextArea.setWidthFull();
        codeTextArea.getElement().getStyle().set("font-family", "var(--lumo-font-family-monospace)");
        layout.add(codeTextArea);
        HorizontalLayout horizontalLayout = new HorizontalLayout();

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
        codeTextArea.setValue(td.getBlockSql());
        inputParamGrid.setItems(td.getParams());
        resultParamGrid.setItems(td.getParams());
    }
}

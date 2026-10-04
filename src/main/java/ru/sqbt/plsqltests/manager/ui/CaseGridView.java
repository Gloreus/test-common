package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;

import java.util.List;

@Import(YamlConfig.class)
@Component
@Slf4j
public class CaseGridView extends Composite<VerticalLayout> {
    private final static String SELECT_COUNT_FORMAT = "Выбрано кейсов: %5d из %5d";

    private final Grid<TestCase> caseGrid = new Grid<>(TestCase.class, false);
    private final Div selectCountValue = new Div();
    private int itemsCount = 0;

    public CaseGridView() {
        VerticalLayout layout = getContent();
        layout.setPadding(false);
        layout.setMargin(false);
        layout.setSpacing(false);
        caseGrid.addClassNames("case-grid");
        caseGrid.setSizeFull();
        caseGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        caseGrid.addColumn(testCase -> testCase.getTestCaseData().getDescription())
                .setKey("testcase")
                .setTooltipGenerator(testCase -> testCase.getTestCaseData().getDescription())
                .setFlexGrow(1)
                .setAutoWidth(false)
                .setHeader("Набор тестов");
        caseGrid.setEmptyStateText("Нет ни одного теста");
        selectCountValue.addClassName("case-grid-info-text");
        layout.add(caseGrid);
        layout.add(selectCountValue);
        caseGrid.setItemDetailsRenderer(createTestCaseDetailsRenderer());
        caseGrid.addSelectionListener(event -> {
           updateSelectCount();
        });
    }

    public com.vaadin.flow.shared.Registration addSelectionListener(com.vaadin.flow.data.selection.SelectionListener<Grid<TestCase>, TestCase> listener) {
        return caseGrid.addSelectionListener(listener);
    }

    private void updateSelectCount() {
        int selCnt = caseGrid.getSelectedItems().size();
        selectCountValue.setText(String.format(SELECT_COUNT_FORMAT, selCnt, itemsCount));
    }

    public void setItems(List<TestCase> items, String caption) {
        caseGrid.setItems(items);
        caseGrid.getColumnByKey("testcase").setHeader(caption);
        itemsCount = items.size();
        updateSelectCount();
    }
    public List<TestCase> getSelectedItems() {
        return caseGrid.getSelectedItems().stream().toList();
    }

    private ComponentRenderer<TestCaseDetailsFormLayout, TestCase> createTestCaseDetailsRenderer() {
        return new ComponentRenderer<>(() -> new TestCaseDetailsFormLayout(),
                TestCaseDetailsFormLayout::setTestCase);
    }

    private static class TestCaseDetailsFormLayout extends VerticalLayout {
        private final Pre codeText = new Pre();


        public TestCaseDetailsFormLayout() {
            setPadding(false);
            codeText.addClassName("code-text-detail");
            add(codeText);

            setWrap(true);
            setWidthFull();
        }

        public void setTestCase(TestCase testCase) {
            log.debug(testCase.toString());
            codeText.setText(testCase.getTestCaseData().getBlockSql());
        }
    }

    public void clear() {
        caseGrid.setItems(List.of());
        caseGrid.getColumnByKey("testcase").setHeader("Нет тестов или файл не выбран");
        itemsCount = 0;
    }

}

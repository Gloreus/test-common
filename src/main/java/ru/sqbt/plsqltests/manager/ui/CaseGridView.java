package ru.sqbt.plsqltests.manager.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;

import java.util.List;

@Import(YamlConfig.class)
@Component
@Slf4j
public class CaseGridView extends Composite<VerticalLayout> {
    private final Grid<TestCase> caseGrid = new Grid<>(TestCase.class, false);
   // private final ObjectMapper objectMapper;

    public CaseGridView(/* @Qualifier("YmlMapper") ObjectMapper objectMapper */) {

        getContent().setPadding(false);
        getContent().setMargin(false);
        caseGrid.setSizeFull();
        caseGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        caseGrid.addColumn(testCase -> testCase.getTestCaseData().getDescription())
                .setKey("testcase")
                .setTooltipGenerator(testCase -> testCase.getTestCaseData().getDescription())
                .setFlexGrow(1)
                .setAutoWidth(false)
                .setHeader("Набор тестов");
        caseGrid.addComponentColumn(item -> {
                    Icon icon = VaadinIcon.CHEVRON_DOWN.create();
                    icon.getStyle().set("cursor", "pointer");
                    icon.addClickListener(e -> {
                        boolean isVisible = caseGrid.isDetailsVisible(item);
                        caseGrid.setDetailsVisible(item, !isVisible);
                    });
                    return icon;
                })
                .setHeader("")
                .setFlexGrow(0)
                .setWidth("48px");
        caseGrid.setEmptyStateText("Нет ни одного теста");

        getContent().add(caseGrid);
        caseGrid.setItemDetailsRenderer(createTestCaseDetailsRenderer());
    }

    public void setItems(List<TestCase> items, String caption) {
        caseGrid.setItems(items);
      //   GridMultiSelectionModel<TestCase> ms = (GridMultiSelectionModel<TestCase>) caseGrid.getSelectionModel();
       // ms.selectAll();
        caseGrid.getColumnByKey("testcase").setHeader(caption);
    }

    private ComponentRenderer<TestCaseDetailsFormLayout, TestCase> createTestCaseDetailsRenderer() {
        return new ComponentRenderer<>(() -> new TestCaseDetailsFormLayout(),
                TestCaseDetailsFormLayout::setTestCase);
    }

    private static class TestCaseDetailsFormLayout extends VerticalLayout {
        private final TextArea codeTextArea = new TextArea();


        public TestCaseDetailsFormLayout() {
            codeTextArea.setSizeFull();
            codeTextArea.getElement().getStyle().set("padding", "0");
            codeTextArea.getElement().getStyle().set("--aura-font-family", "monospace");
            codeTextArea.setReadOnly(true);
            add(codeTextArea);

            setWrap(true);
            setWidthFull();
        }

        public void setTestCase(TestCase testCase) {
            log.debug(testCase.toString());
            codeTextArea.setValue(testCase.getTestCaseData().getBlockSql());
        }
    }
}

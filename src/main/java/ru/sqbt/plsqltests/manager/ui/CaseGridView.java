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
    @Autowired
    @Qualifier("YmlMapper")
    private ObjectMapper objectMapper;

    public CaseGridView() {

        caseGrid.setSizeFull();
        caseGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        caseGrid.addColumn(testCase -> testCase.getTestCaseData().getDescription())
                .setKey("testcase")
                .setFlexGrow(1)
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
    }

    @PostConstruct
    public void afterCreate() {
        caseGrid.setItemDetailsRenderer(createTestCaseDetailsRenderer());
        caseGrid.addItemClickListener(event -> {
            TestCase item = event.getItem();
            caseGrid.setDetailsVisible(item, !caseGrid.isDetailsVisible(item));
        });
    }

    public void setItems(List<TestCase> items, String caption) {
        caseGrid.setItems(items);
      //   GridMultiSelectionModel<TestCase> ms = (GridMultiSelectionModel<TestCase>) caseGrid.getSelectionModel();
       // ms.selectAll();
        caseGrid.getColumnByKey("testcase").setHeader(caption);
    }

    private ComponentRenderer<TestCaseDetailsFormLayout, TestCase> createTestCaseDetailsRenderer() {
        return new ComponentRenderer<>(() -> new TestCaseDetailsFormLayout(objectMapper),
                TestCaseDetailsFormLayout::setTestCase);
    }

    private static class TestCaseDetailsFormLayout extends VerticalLayout {
        private final TextArea jsonTextArea = new TextArea();
        private final ObjectMapper mapper;

        public TestCaseDetailsFormLayout(@NonNull ObjectMapper mapper) {
            this.mapper = mapper;
            setWrap(true);
            jsonTextArea.setReadOnly(true);
            jsonTextArea.setWidthFull();
            add(jsonTextArea);
            setWidthFull();
        }

        public void setTestCase(TestCase testCase) {
            log.info(testCase.toString());
            try {
                jsonTextArea.setValue(mapper.writeValueAsString(testCase));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

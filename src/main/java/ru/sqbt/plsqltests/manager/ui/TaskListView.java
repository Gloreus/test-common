package ru.sqbt.plsqltests.manager.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.TestStoreProperties;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.caseentity.TestDataRepository;
import ru.sovcombank.rbs.caseentity.TestProfile;
import ru.sqbt.plsqltests.base.ui.ViewTitle;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Route(value = "")
@PageTitle("Test List")
@Menu(order = 0, icon = "icons/clipboard-check.svg", title = "Test List")
@Component
@Import(YamlConfig.class)
class TaskListView extends VerticalLayout {

    private final TestStoreProperties testStoreProperties;
    @Autowired
    private TestDataRepository repository;

    @Autowired
    @Qualifier("YmlMapper")
    private ObjectMapper objectMapper;

    private final ComboBox<Path> profilesComboBox;
    private final MultiSelectListBox<String> casesListBox;
    private final Grid<TestCase> caseGrig = new Grid<>(TestCase.class, false);
    private final TextArea logTtextArea;
    private final Button createBtn;

    TaskListView(TestStoreProperties testStoreProperties) {
        this.testStoreProperties = testStoreProperties;

        profilesComboBox = new ComboBox<>("Профиль");
        profilesComboBox.setPlaceholder("Профиль тестирования");
        profilesComboBox.setMinWidth("8em");
        profilesComboBox.setAllowCustomValue(false);

        casesListBox = new MultiSelectListBox<>();
        casesListBox.setMinWidth("8em");
        casesListBox.setWidthFull();

        Path profilesPath = testStoreProperties.getProfilesFullPath() ;

        try {
            profilesComboBox.setItems(findYamlFiles(profilesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createBtn = new Button("Выполнить выбранные");
        createBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        var toolbar = new HorizontalLayout();
        add(new ViewTitle("Просмотр и запуск тестов"));
        toolbar.add(profilesComboBox, casesListBox);

        toolbar.setWrap(true);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, profilesComboBox);
        setSizeFull();
        add(toolbar);

        VerticalLayout mainForm = new VerticalLayout();
        mainForm.setSizeFull();
        initCaseGrid();
        mainForm.add(caseGrig);
        mainForm.add(createBtn);
        add(mainForm);
        logTtextArea = new TextArea();
        initInfoPanel();
        add(logTtextArea);
        setFlexGrow(1, logTtextArea);
        profilesComboBox.addValueChangeListener(this::onProfileSelected);
    }

    private void initInfoPanel() {
        logTtextArea.setValue("Это лог работы");
        logTtextArea.setWidthFull();
        logTtextArea.setHeight("12em");
        logTtextArea.setMaxHeight("12em");
        logTtextArea.setMaxRows(10);
        logTtextArea.setMinHeight("8em");
        logTtextArea.setReadOnly(true);
        logTtextArea.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private void initCaseGrid() {
        caseGrig.setSelectionMode(Grid.SelectionMode.MULTI);

        caseGrig.addColumn(testCase -> testCase.getTestCaseData().getDescription())
                .setKey("testcase")
                .setFlexGrow(1)
                .setHeader("Набор тестов");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        caseGrig.setItemDetailsRenderer(createTestCaseDetailsRenderer(objectMapper));
    }

    private ComponentRenderer<TestCaseDetailsFormLayout, TestCase> createTestCaseDetailsRenderer(ObjectMapper objectMapper) {
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

        public void setTestCase(TestCase testCase)  {
            try {
                jsonTextArea.setValue(mapper.writeValueAsString(testCase));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void onProfileSelected(HasValue.ValueChangeEvent<Path> event) {
        Path selected = event.getValue();
        if (selected == null) {
            writeInfo("Профиль не выбран");
        } else {
            writeInfo(selected.toString());
            try {
                TestProfile profile = repository.loadProfile(selected.getFileName().toString());
                reloadCases(profile);
                writeInfo("Загружен: " + profile.getProfileName());
            } catch (IOException e) {
                writeInfo(e.getMessage());
            }
        }
    }

    private void reloadCases(TestProfile profile) {
        caseGrig.setItems(repository.loadCases(profile));
        GridMultiSelectionModel<TestCase> ms = (GridMultiSelectionModel<TestCase>) caseGrig.getSelectionModel();
        ms.selectAll();
        caseGrig.getColumnByKey("testcase").setHeader(profile.getDescription());
    }

    private List<String> loadCases(TestProfile profile) {
        return repository.loadCases(profile).stream()
                .map(testCase -> testCase.getTestCaseData().getDescription())
                .toList();
    }

    private void writeInfo(String s) {
        logTtextArea.setValue(logTtextArea.getValue() + "\n" + s);
        log.debug(s);
        logTtextArea.scrollToEnd();
    }

    public List<Path> findYamlFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{yaml,YAML}")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    result.add(entry);
                }
            }
        }
        return result;
    }

}

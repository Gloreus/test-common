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
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.shared.SelectionPreservationMode;
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
import ru.sovcombank.rbs.TestStoreProperties;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.caseentity.TestDataRepository;
import ru.sovcombank.rbs.caseentity.TestDataYamlRepository;
import ru.sovcombank.rbs.caseentity.TestProfile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import ru.sqbt.plsqltests.base.ui.ViewTitle;

@Slf4j
@Route(value = "oratests")
@PageTitle("Ora tests")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Ora Tests")
@Import(YamlConfig.class)
class TaskListView extends VerticalLayout {

    private final TestStoreProperties testStoreProperties;
    @Autowired
    private TestDataYamlRepository repository;

    @Autowired
    @Qualifier("YmlMapper") // todo: Сделать сериализацию тест-кейсов в yaml
    private ObjectMapper objectMapper;

    private final ComboBox<Path> profilesComboBox;
    private final MultiSelectListBox<String> casesListBox;
    private final Grid<TestCase> caseGrid = new Grid<>(TestCase.class, false);
    private final TextArea logTextArea;
    private final Button createBtn;
    private final RadioButtonGroup<String>  radioGroupProfileType = new RadioButtonGroup<>();

    TaskListView(TestStoreProperties testStoreProperties) {
        this.testStoreProperties = testStoreProperties;

        radioGroupProfileType.setItems("Профиль", "ora-Test");
        radioGroupProfileType.setValue("ora-Test");
        radioGroupProfileType.addThemeVariants(RadioGroupVariant.AURA_HORIZONTAL);

        profilesComboBox = new ComboBox<>();
        profilesComboBox.setPlaceholder("Профиль тестирования");
        profilesComboBox.setMinWidth("8em");
        profilesComboBox.setWidthFull();
        profilesComboBox.setAllowCustomValue(false);
        casesListBox = new MultiSelectListBox<>();
        casesListBox.setMinWidth("8em");
        casesListBox.setWidthFull();

        createBtn = new Button("Выполнить выбранные");
        createBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        var toolbar = new VerticalLayout();
        add(new ViewTitle("Просмотр и запуск тестов"));
        toolbar.add(radioGroupProfileType, profilesComboBox, casesListBox);

        toolbar.setWrap(true);
        toolbar.setWidthFull();
        add(toolbar);
        setSizeFull();

        VerticalLayout mainForm = new VerticalLayout();
        mainForm.setSizeFull();
        initCaseGrid();
        mainForm.add(caseGrid);
        mainForm.add(createBtn);
        add(mainForm);
        logTextArea = new TextArea();
        initInfoPanel();
        add(logTextArea);
        setFlexGrow(1, logTextArea);
        profilesComboBox.addValueChangeListener(this::onProfileSelected);
        radioGroupProfileType.addValueChangeListener(this::onProfileTypeSelected);
    }

    private void reloadYamls(Path profilesPath) {
        writeInfo(profilesPath.toString());
        try {
            profilesComboBox.setItems(findYamlFiles(profilesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initInfoPanel() {
        logTextArea.setValue("Это лог работы");
        logTextArea.setWidthFull();
        logTextArea.setHeight("12em");
        logTextArea.setMaxHeight("12em");
        logTextArea.setMaxRows(10);
        logTextArea.setMinHeight("8em");
        logTextArea.setReadOnly(true);
        logTextArea.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private void initCaseGrid() {
        caseGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        caseGrid.addColumn(testCase -> testCase.getTestCaseData().getDescription())
                .setKey("testcase")
                .setFlexGrow(1)
                .setHeader("Набор тестов");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        caseGrid.setItemDetailsRenderer(createTestCaseDetailsRenderer(objectMapper));
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

    private void onProfileTypeSelected(HasValue.ValueChangeEvent<String> event) {
        String p = event.getValue();
        writeInfo(p);
        Path profilesPath;
        if (radioGroupProfileType.getValue() == "Профиль") {
            profilesPath = testStoreProperties.getProfilesFullPath();
        } else {
            profilesPath = Path.of(testStoreProperties.getRootDir(), "ora_tests/02").toAbsolutePath();
        }
        reloadYamls(profilesPath);
    }

    private void onProfileSelected(HasValue.ValueChangeEvent<Path> event) {
        Path selected = event.getValue();
        if (selected == null) {
            writeInfo("Профиль не выбран");
        } else {
            writeInfo(selected.toString());
            try {
                if (radioGroupProfileType.getValue() == "Профиль") {
                    TestProfile profile = repository.loadProfile(selected.getFileName().toString());
                    reloadCases(profile);
                    writeInfo("Загружен: " + profile.getProfileName());
                } else {

                }

            } catch (IOException e) {
                writeInfo(e.getMessage());
            }
        }
    }

    private void reloadCases(TestProfile profile) {
        caseGrid.setItems(repository.loadCases(profile));
        GridMultiSelectionModel<TestCase> ms = (GridMultiSelectionModel<TestCase>) caseGrid.getSelectionModel();
        ms.selectAll();
        caseGrid.getColumnByKey("testcase").setHeader(profile.getDescription());
    }

    private void writeInfo(String s) {
        logTextArea.setValue(logTextArea.getValue() + "\n" + s);
        log.debug(s);
        logTextArea.scrollToEnd();
    }

    public List<Path> findYamlFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{yaml,YAML,yml,YML}")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    result.add(entry);
                }
            }
        }
        return result;
    }

}

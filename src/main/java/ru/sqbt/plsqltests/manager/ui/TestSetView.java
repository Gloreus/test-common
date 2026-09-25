package ru.sqbt.plsqltests.manager.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.sovcombank.rbs.TestStoreProperties;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.caseentity.TestDataYamlRepository;
import ru.sqbt.plsqltests.base.ui.ViewLogPanel;
import ru.sqbt.plsqltests.base.ui.ViewTitle;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Route(value = "testset")
@PageTitle("Тесты из файла")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Набор тестов")
@Import(YamlConfig.class)
class TestSetView extends VerticalLayout {

    private final TestStoreProperties testStoreProperties;
    private final ComboBox<Path> fileComboBox = new ComboBox<>();
    private final ViewLogPanel logPanel = new ViewLogPanel();
    private final Button createBtn;
    @Autowired
    private TestDataYamlRepository repository;
    private Path currentPath;
    private Path rootPath;

    TestSetView(@NonNull TestStoreProperties testStoreProperties) {
        this.testStoreProperties = testStoreProperties;
        currentPath = null;
        rootPath = testStoreProperties.getOraTestsFullPath();
        initFileComboBox();
        createBtn = new Button("Выполнить выбранные");
        createBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        var toolbar = new VerticalLayout();
        add(new ViewTitle("Тесты из файла"));

        toolbar.add(fileComboBox);
        toolbar.setWrap(true);
        toolbar.setWidthFull();
        add(toolbar);
        setSizeFull();

        VerticalLayout mainForm = new VerticalLayout();
        mainForm.setSizeFull();
        mainForm.add(createBtn);
        add(mainForm);
        add(logPanel);
        setFlexGrow(1, logPanel);
        try {
            reloadYamls(rootPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initFileComboBox() {
        fileComboBox.setPlaceholder("Набор тестов");
        fileComboBox.setMinWidth("8em");
        fileComboBox.setWidthFull();
        fileComboBox.setAllowCustomValue(false);
        fileComboBox.addValueChangeListener(this::onProfileSelected);
        fileComboBox.setFocusSelectedItem(true);
        fileComboBox.setItemLabelGenerator(item -> {
            Path p = rootPath.relativize(item);
            return "Тесты:" + p.toString();
        });
    }

    private void reloadYamls(@NonNull Path dir) throws IOException {
        writeInfo(dir.toString());
        List<Path> result = new ArrayList<>();

        if (!rootPath.equals(dir)) {
            Path prev = dir.relativize(currentPath);
            result.add(prev);
        }
        result.add(dir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    result.add(entry);
                } else {
                    String s = entry.toString().toLowerCase(Locale.getDefault());
                    if (s.endsWith(".yml")) {
                        result.add(entry);
                    }
                }
            }
        }
        currentPath = dir;
        fileComboBox.setItems(result);
        fileComboBox.setValue(dir);
    }

    private ComponentRenderer<TestCaseDetailsFormLayout, TestCase> createTestCaseDetailsRenderer(ObjectMapper objectMapper) {
        return new ComponentRenderer<>(() -> new TestCaseDetailsFormLayout(objectMapper),
                TestCaseDetailsFormLayout::setTestCase);
    }

    private void onProfileSelected(HasValue.ValueChangeEvent<Path> event) {
        Path selected = event.getValue();
        if (selected == null) {
            writeInfo("Профиль не выбран");
        } else {
            if (!selected.equals(currentPath)) {
                writeInfo(selected.toString());
                if (Files.isDirectory(selected)) {
                    try {
                        reloadYamls(selected);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        currentPath = selected;
    }

    private void writeInfo(String s) {
        logPanel.writeLog(s);
        log.debug(s);
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
            try {
                jsonTextArea.setValue(mapper.writeValueAsString(testCase));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }


}

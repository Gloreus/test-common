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
import ru.sovcombank.rbs.TestStoreProperties;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.caseentity.TestDataYamlRepository;
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
@PageTitle("Профиль")
@Menu(order = 0, icon = "icons/clipboard-check.svg", title = "Профили тестирования")

class ProfilesView extends VerticalLayout {
    private final ObjectMapper objectMapper;
    private final TestStoreProperties testStoreProperties;
    @Autowired
    private TestDataYamlRepository repository;

    private final ComboBox<Path> profilesComboBox;
    private final MultiSelectListBox<String> casesListBox;
    private final CaseGridView caseGrid;
    private final TextArea logTextArea;
    private final Button createBtn;

    ProfilesView(TestStoreProperties testStoreProperties,  @NonNull @Qualifier("YmlMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.testStoreProperties = testStoreProperties;
        caseGrid = new CaseGridView(objectMapper);
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
        toolbar.add(profilesComboBox, casesListBox);

        toolbar.setWrap(true);
        toolbar.setWidthFull();
        add(toolbar);
        setSizeFull();

        VerticalLayout mainForm = new VerticalLayout();
        mainForm.setSizeFull();

        mainForm.add(caseGrid);
        mainForm.add(createBtn);
        add(mainForm);
        logTextArea = new TextArea();
        initInfoPanel();
        add(logTextArea);
        setFlexGrow(1, logTextArea);
        profilesComboBox.addValueChangeListener(this::onProfileSelected);
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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        reloadYamls(testStoreProperties.getProfilesFullPath());
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
        caseGrid.setItems(repository.loadCases(profile), profile.getDescription());
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

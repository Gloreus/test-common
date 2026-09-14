package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.extern.slf4j.Slf4j;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import ru.sovcombank.rbs.TestStoreProperties;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.caseentity.TestCaseReference;
import ru.sovcombank.rbs.caseentity.TestDataRepository;
import ru.sovcombank.rbs.caseentity.TestProfile;
import ru.sqbt.plsqltests.base.ui.ViewTitle;


@Slf4j
@Route(value = "")
@PageTitle("Test List")
@Menu(order = 0, icon = "icons/clipboard-check.svg", title = "Test List")
class TaskListView extends VerticalLayout {

    private final TestStoreProperties testStoreProperties;
    @Autowired
    private TestDataRepository repository;

    final ComboBox<Path> profilesComboBox;
    final MultiSelectListBox<String> casesListBox;
    final TextArea logTtextArea;
    final Button createBtn;

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

        createBtn = new Button("Create");
        createBtn.addThemeVariants(ButtonVariant.PRIMARY);

        var toolbar = new HorizontalLayout();
        add(new ViewTitle("Просмотр и запуск тестов"));
        toolbar.add(profilesComboBox, casesListBox);

        toolbar.setWrap(true);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, profilesComboBox);
        setSizeFull();
        add(toolbar);
        add(casesListBox);
        VerticalLayout mainForm = new VerticalLayout();
        mainForm.setSizeFull();
        mainForm.add();
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

    private void onProfileSelected(HasValue.ValueChangeEvent<Path> event) {
        Path selected = event.getValue();
        if (selected == null) {
            writeInfo("Профиль не выбран");
        } else {
            writeInfo(selected.toString());
            try {
                TestProfile profile = repository.loadProfile(selected.getFileName().toString());
                writeInfo("Загружен: " + profile.getProfileName());

                casesListBox.setItems(loadCases(profile));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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

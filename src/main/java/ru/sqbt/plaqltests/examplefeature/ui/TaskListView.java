package ru.sqbt.plaqltests.examplefeature.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sqbt.plaqltests.TestStoreProperties;
import ru.sqbt.plaqltests.base.ui.ViewTitle;
import ru.sqbt.plaqltests.examplefeature.Task;
import ru.sqbt.plaqltests.examplefeature.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route(value = "")
@PageTitle("Task List")
@Menu(order = 0, icon = "icons/clipboard-check.svg", title = "Task List")
class TaskListView extends VerticalLayout {

    private final TestStoreProperties testStoreProperties;

    private final TaskService taskService;

    final ComboBox<Path> profilesComboBox;

    final Button createBtn;

    TaskListView(TestStoreProperties testStoreProperties, TaskService taskService) {
        this.testStoreProperties = testStoreProperties;
        this.taskService = taskService;

        profilesComboBox = new ComboBox<>("Профиль");
        profilesComboBox.setPlaceholder("Профиль тестирования");
        profilesComboBox.setMinWidth("8em");
        profilesComboBox.setWidthFull();

        Path profilesPath = testStoreProperties.getProfilesPath();

        try {
            profilesComboBox.setItems(findYamlFiles(profilesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createBtn = new Button("Create");
        createBtn.addThemeVariants(ButtonVariant.PRIMARY);

        var toolbar = new HorizontalLayout();
        toolbar.add(new ViewTitle("Task List"), profilesComboBox, createBtn);
        toolbar.setFlexGrow(1, profilesComboBox);
        toolbar.setWrap(true);
        toolbar.setWidthFull();

        setSizeFull();
        add(toolbar);
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

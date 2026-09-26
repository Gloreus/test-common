package ru.sqbt.plsqltests.manager.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.Tree;
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

    private final TreeGrid<Path> fileTree;
    private final ViewLogPanel logPanel = new ViewLogPanel();
    private final Button createBtn;
    @Autowired
    private TestDataYamlRepository repository;
    private Path currentPath;
    private Path rootPath;

    private TreeData<Path> treeData = new TreeData<>();
    private final TreeDataProvider<Path> treeDataProvider = new TreeDataProvider<>(treeData, HierarchicalDataProvider.HierarchyFormat.FLATTENED);

    TestSetView(@NonNull TestStoreProperties testStoreProperties) {
        this.testStoreProperties = testStoreProperties;
        currentPath = null;
        rootPath = testStoreProperties.getOraTestsFullPath();
        HorizontalLayout fileLayout = new HorizontalLayout();
        fileTree = createFileTree();
        fileLayout.add(fileTree);
        TextArea fileInfo = new TextArea("Подробнее");
        fileLayout.add(fileInfo);
        fileLayout.setWidthFull();
        fileLayout.setFlexGrow(1, fileInfo, fileTree);

        createBtn = new Button("Выполнить выбранные");
        createBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        var toolbar = new VerticalLayout();
        add(new ViewTitle("Тесты из файла"));

        toolbar.add(fileLayout);
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
    }

    private TreeGrid<Path> createFileTree() {
        TreeGrid<Path> ft = new TreeGrid<>();
        ft.setMinHeight("10em");
        ft.setMinWidth("35em");
        ft.setDataProvider(treeDataProvider);
        ft.addHierarchyColumn(path -> path.getFileName().toString()).setHeader("Файл");
        ft.setEmptyStateText("Не нашлось ни одного теста");

        treeData.clear();
        treeData.addRootItems(rootPath);
        loadDir(rootPath);
        treeDataProvider.refreshAll();
        return ft;
    }

    private void loadDir(@NonNull Path dir) {
        writeInfo(dir.toString());
        // Подкаталоги
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, Files::isDirectory )) {
            for (Path entry : stream) {
                treeData.addItem(dir, entry);
                loadDir(entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Файлы
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.yml")) {
            for (Path entry : stream) {
                treeData.addItem(dir, entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void reloadYamls(@NonNull Path dir) throws IOException {
        currentPath = dir;
    }

    private void writeInfo(String s) {
        logPanel.writeLog(s);
        log.debug(s);
    }
}

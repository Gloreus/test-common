package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
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
import ru.sqbt.plsqltests.manager.model.TestStories;
import ru.sqbt.plsqltests.manager.model.TestStoryNode;
import ru.sqbt.plsqltests.manager.model.TestStoryReader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Route(value = "testset")
@PageTitle("Тесты из файла")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Набор тестов")
@Import(YamlConfig.class)
class TestSetView extends VerticalLayout {
    private static final String DEFAULT_INFO_TEXT = "Выберите файл с тестами для работы";

    private final TestStoreProperties testStoreProperties;

    private final ViewLogPanel logPanel = new ViewLogPanel();
    private final Button createBtn;
    private final TreeGrid<TestStoryNode> fileTree;
    private final TextArea fileInfo = new TextArea("Подробнее");
    private final TestCaseForm testCaseForm = new TestCaseForm();
    @Autowired
    TestStoryReader testStoryReader;
    @Autowired
    private TestDataYamlRepository repository;
    private Path rootPath;
    private TreeData<TestStoryNode> treeData = new TreeData<>();
    private final TreeDataProvider<TestStoryNode> treeDataProvider = new TreeDataProvider<>(treeData, HierarchicalDataProvider.HierarchyFormat.FLATTENED);

    TestSetView(@NonNull TestStoreProperties testStoreProperties) {
        this.testStoreProperties = testStoreProperties;
        rootPath = testStoreProperties.getOraTestsFullPath();
        HorizontalLayout fileLayout = new HorizontalLayout();
        fileTree = createFileTree();
        fileLayout.add(fileTree);

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
        testCaseForm.getContent().setSizeFull();
        mainForm.add(testCaseForm);

        mainForm.add(createBtn);
        add(mainForm);
        add(logPanel);
        setFlexGrow(1, logPanel);
    }

    private TreeGrid<TestStoryNode> createFileTree() {
        TreeGrid<TestStoryNode> ft = new TreeGrid<>();
        ft.getElement().getStyle().set("BackgroundColor", "--lumo-base-color");
        ft.setMinHeight("4em");
        ft.setMinWidth("35em");
        ft.setDataProvider(treeDataProvider);
        ft.addThemeVariants(GridVariant.NO_BORDER, GridVariant.LUMO_COMPACT);
        ft.addHierarchyColumn(TestStoryNode::getName).
                setHeader("Наборы тестов");
        ft.addColumn(TestStoryNode::getCaption);
        ft.setEmptyStateText("Не нашлось ни одного теста");

        ft.addSelectionListener(this::onPathSelectionChanged);
        return ft;
    }

    @PostConstruct
    private void Init() {
        TestStoryNode node = new TestStoryNode(rootPath);
        treeData.addRootItems(node);
        loadDir(node);
        treeDataProvider.refreshAll();
    }

    private void onPathSelectionChanged(SelectionEvent<Grid<TestStoryNode>, TestStoryNode> event) {
        if (event.getFirstSelectedItem().isEmpty()) {
            fileInfo.setValue(DEFAULT_INFO_TEXT);
        } else {
            TestStoryNode node = event.getFirstSelectedItem().get();
            fileInfo.setValue("Тесты " + node.getCaption());
            node.getTestStories().ifPresentOrElse(ts -> {
                        List<TestCase> caseList = testStoryReader.buildFromTestStories(ts);
                        testCaseForm.setTestCase(caseList.getFirst());
                    },
                    () -> {
                        testCaseForm.clear();
                    });
        }
    }

    private void loadDir(TestStoryNode node) {
        writeInfo(node.getCaption());
        // Подкаталоги
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(node.getPath(), Files::isDirectory)) {
            for (Path entry : stream) {
                TestStoryNode item = new TestStoryNode(entry);
                treeData.addItem(node, item);
                loadDir(item);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Файлы
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(node.getPath(), "*.yml")) {
            for (Path entry : stream) {
                try {
                    TestStories ts = testStoryReader.readYam(entry);
                    treeData.addItem(node, new TestStoryNode(entry, ts));
                } catch (IOException e) {
                    log.info(entry.toString() + " не корректный файл тестов");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeInfo(String s) {
        logPanel.writeLog(s);
        log.debug(s);
    }
}

package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.sidenav.SideNavVariant;
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

    private final ViewLogPanel logPanel = new ViewLogPanel();
    private final Button createBtn;

    private final SideNav sideNavTestTree = new SideNav();
    private final TextArea fileInfo = new TextArea("Подробнее");
    private final TestCaseForm testCaseForm = new TestCaseForm();
    @Autowired
    private TestStoryReader testStoryReader;


    private Path rootPath;

    TestSetView(@NonNull TestStoreProperties testStoreProperties) {

        rootPath = testStoreProperties.getOraTestsFullPath();
        HorizontalLayout fileLayout = new HorizontalLayout();

        fileLayout.add(sideNavTestTree);
        fileInfo.setWidthFull();
        fileLayout.add(fileInfo);
        fileLayout.setWidthFull();
        sideNavTestTree.setMinWidth("20em");


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

    private void addStoryTree(SideNavItem root, TestStoryNode node) {
        SideNavItem item;
        if (node.isFolder()) {
             item = new SideNavItem(node.getName());
             item.setPrefixComponent(VaadinIcon.FOLDER.create());
             item.setExpanded(false);
        } else {
            item = new SideNavItem(node.getName(), node.getName() ,VaadinIcon.DASHBOARD.create());
        }

        root.addItem(item);

        for (TestStoryNode child : node.getChildren()) {
            addStoryTree(item, child); // рекурсия = вложенные пункты меню
        }
    }

    private void loadTets() {
        TestStoryNode rootNode = testStoryReader.getTree();
        SideNavItem rootItem = new SideNavItem("Тесты");
        addStoryTree(rootItem, rootNode);
        sideNavTestTree.addItem(rootItem);
    }

    @PostConstruct
    private void Init() {
        loadTets();
    }




    private void writeInfo(String s) {
        logPanel.writeLog(s);
        log.debug(s);
    }
}

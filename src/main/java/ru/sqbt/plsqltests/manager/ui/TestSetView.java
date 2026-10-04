package ru.sqbt.plsqltests.manager.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoIcon;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.sovcombank.rbs.TestStoreProperties;
import ru.sovcombank.rbs.YamlConfig;
import ru.sqbt.plsqltests.base.ui.ViewLogPanel;
import ru.sqbt.plsqltests.base.ui.ViewTitle;
import ru.sqbt.plsqltests.manager.model.TestStoryNode;
import ru.sqbt.plsqltests.manager.model.TestStoryReader;

@Slf4j
@Route(value = "testset/:path?")
@PageTitle("Набор тестов")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Набор тестов")
@Import(YamlConfig.class)
class TestSetView extends VerticalLayout implements BeforeEnterObserver {
    private static final String DEFAULT_INFO_TEXT = "Выберите файл с тестами для работы";
    private final TestStoryReader testStoryReader;

    private final ViewLogPanel logPanel = new ViewLogPanel();
    private final Button createBtn =new Button("Выполнить выбранные");;

    private final SideNav sideNavTestTree = new SideNav();
    private final TextArea fileInfo = new TextArea("Подробнее");
    private final CaseGridView caseGridView = new CaseGridView();

    TestSetView(TestStoryReader testStoryReader) {
        this.testStoryReader = testStoryReader;
        add(new ViewTitle("Тесты из файла"));

        Scroller scroller = new Scroller(Scroller.ScrollDirection.BOTH);
        scroller.addThemeName("overflow-indicators");
        scroller.setMinWidth("20em");
        scroller.setContent(sideNavTestTree);

        SplitLayout fileLayout = new SplitLayout();
        fileLayout.setThemeVariant(SplitLayoutVariant.SMALL, true);
        fileLayout.addToPrimary(scroller);
        caseGridView.addSelectionListener(event -> {
           createBtn.setEnabled(event.getSource().getSelectedItems().size() > 0);
        });

        fileLayout.addToSecondary(caseGridView);
        fileLayout.setWidthFull();

        sideNavTestTree.addClassNames("file-tree-side-nav");

        setSizeFull();

        VerticalLayout mainForm = new VerticalLayout();
        mainForm.setSizeFull();
        mainForm.setMaxHeight("80%");
        fileLayout.setSizeFull();
        mainForm.add(fileLayout);
        mainForm.add(createToolBar());

        add(mainForm);
        add(logPanel);

        setFlexGrow(1, logPanel);
        setFlexGrow(7, mainForm);
    }

    private Component createToolBar() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addClassName("testview-tool-bar");
        layout.setMargin(false);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setWidthFull();

        createBtn.addThemeVariants(ButtonVariant.LUMO_SMALL,ButtonVariant.LUMO_PRIMARY);
        createBtn.setEnabled(false);
        createBtn.setIcon(LumoIcon.PLAY.create());
        createBtn.addClickListener(event -> {
            String msg = String.format("Будет выполнено %d тестов", caseGridView.getSelectedItems().size());
            Notification notification = Notification.show(msg, 2000, Notification.Position.BOTTOM_END);
        });
        layout.addToEnd (createBtn);
        return layout;
    }

    private void addStoryTree(SideNavItem root, TestStoryNode node) {
        SideNavItem item;
        if (node.isFolder()) {
            item = new SideNavItem(node.getName());
            item.setPrefixComponent(VaadinIcon.FOLDER_OPEN.create());
            item.setExpanded(false);
            item.setRouterIgnore(true);
        } else {
            item = new SideNavItem(node.getName(),
                    this.getClass(),
                    new RouteParameters("path", node.getName()),
                    LumoIcon.ORDERED_LIST.create()
            );
        }

        root.addItem(item);

        for (TestStoryNode child : node.getChildren()) {
            addStoryTree(item, child); // рекурсия -- вложенные пункты меню
        }
    }

    private void loadTets() {
        TestStoryNode rootNode = testStoryReader.getTree();
        SideNavItem rootItem = new SideNavItem("Тесты");
        rootItem.setExpanded(true);
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getRouteParameters().get("path").ifPresentOrElse(
                value -> {
                    testStoryReader.getStoriesByCode(value).ifPresentOrElse(
                            ts -> {
                                String s = ts.getCaption().isBlank() ? ts.getName() : ts.getCaption();
                                caseGridView.setItems(testStoryReader.buildFromTestStories(ts), s);
                            },
                           caseGridView::clear
                    );
                },
                caseGridView::clear
        );
    }
}

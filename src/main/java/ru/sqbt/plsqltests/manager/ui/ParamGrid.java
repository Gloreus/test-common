package ru.sqbt.plsqltests.manager.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.configuration.style.Style;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import ru.sovcombank.rbs.YamlConfig;
import ru.sovcombank.rbs.caseentity.TestCase;
import ru.sovcombank.rbs.core.DbParam;

import java.util.List;

@Slf4j
public class ParamGrid extends Composite<VerticalLayout> {
    private final Grid<DbParam> grid = new Grid<>(DbParam.class, false);

    public ParamGrid(String caption) {
        getContent().setPadding(false);
        getContent().setMargin(false);

        grid.addColumn(dbParam -> dbParam.getName()).setWidth("10em");
        grid.addColumn(dbParam -> dbParam.getParamType()).setWidth("8em");
        grid.addColumn(dbParam -> dbParam.getValue()).setAutoWidth(true);
        grid.setEmptyStateText("Нет параметров");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        Text header = new Text(caption);
        getContent().add(header, grid);
    }

    public void setItems(@NonNull  List<DbParam> params) {
        grid.setItems(params);
    }

}

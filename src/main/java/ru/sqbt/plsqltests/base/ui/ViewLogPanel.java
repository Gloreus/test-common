package ru.sqbt.plsqltests.base.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

@StyleSheet("view-log-panel.css")
public class ViewLogPanel extends Composite<TextArea> {
    public ViewLogPanel() {
        TextArea textArea = getContent();
        textArea.setMaxHeight("12em");
        textArea.setMaxRows(10);
        textArea.setHeight("12em");
        textArea.setMinHeight("8em");
        textArea.setReadOnly(true);
        textArea.setValue("Это лог работы");
        textArea.setWidthFull();
        textArea.setValueChangeMode(ValueChangeMode.LAZY);
    }

  public void writeLog(String message) {
        TextArea textArea = getContent();
        textArea.setValue(textArea.getValue() + "\n" + message);
        textArea.scrollToEnd();
  }
}

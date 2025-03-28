package de.bauersoft.views.menuBuilderNew.cluster.dialog;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextArea;
import de.bauersoft.data.entities.pattern.Pattern;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Consumer;

@Getter
public class DescriptionDialog extends Dialog
{
    private final TextArea textArea;

    public DescriptionDialog(Consumer<String> onSaveConsumer)
    {
        textArea = new TextArea();
        textArea.setPlaceholder("Variantenbeschreibung...");
        textArea.setMaxLength(10240);
        textArea.setHeight("calc(5 * var(--lumo-text-field-size))");
        textArea.setWidthFull();

        Button saveButton = new Button("Auswählen");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(event ->
        {
            if(onSaveConsumer != null)
                onSaveConsumer.accept(Objects.requireNonNullElse(textArea.getValue(), ""));

            this.close();
        });

        Button cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            this.close();
        });

        this.add(textArea);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();

        this.setWidth("30rem");
    }
}

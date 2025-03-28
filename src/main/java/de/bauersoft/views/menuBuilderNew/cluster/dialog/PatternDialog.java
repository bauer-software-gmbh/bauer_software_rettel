package de.bauersoft.views.menuBuilderNew.cluster.dialog;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.pattern.Pattern;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class PatternDialog extends Dialog
{
    private final ListDataProvider<Pattern> patternDataProvider;
    private final Consumer<Pattern> onPatternSelected;

    private final ComboBox<Pattern> patternComboBox;

    public PatternDialog(ListDataProvider<Pattern> patternDataProvider, Consumer<Pattern> onPatternSelected)
    {
        this.patternDataProvider = patternDataProvider;
        this.onPatternSelected = onPatternSelected;

        patternComboBox = new ComboBox<>("Ernährungsformen");
        patternComboBox.setWidthFull();
        patternComboBox.setAutofocus(true);
        patternComboBox.setItemLabelGenerator(Pattern::getName);
        patternComboBox.setItems(patternDataProvider);

        patternDataProvider.getItems()
                .stream()
                .findFirst()
                .ifPresent(pattern -> patternComboBox.setValue(pattern));

        Button selectButton = new Button("Auswählen");
        selectButton.addClickShortcut(Key.ENTER);
        selectButton.setMinWidth("150px");
        selectButton.setMaxWidth("180px");
        selectButton.addClickListener(event ->
        {
            Pattern value = patternComboBox.getValue();
            patternComboBox.setInvalid(value == null);

            if(patternComboBox.isInvalid()) return;

            if(onPatternSelected != null)
                onPatternSelected.accept(value);

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

        this.add(patternComboBox);
        this.getFooter().add(selectButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}

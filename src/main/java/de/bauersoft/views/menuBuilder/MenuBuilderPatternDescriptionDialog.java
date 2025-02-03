package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextArea;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.data.entities.variant.Variant;
import de.bauersoft.views.menuBuilder.cluster.MenuBuilderClusterManager;

import java.util.Objects;

public class MenuBuilderPatternDescriptionDialog extends Dialog
{

    private MenuBuilderClusterManager clusterManager;
    private Menu item;
    private Pattern pattern;

    private String oldDescription;
    private TextArea descriptionTextArea;

    private Button okButton;
    private Button cancelButton;

    private Variant variant;

    public MenuBuilderPatternDescriptionDialog(MenuBuilderClusterManager clusterManager, Variant variant)
    {
        Objects.requireNonNull(clusterManager, "clusterManager cannot be null");
        Objects.requireNonNull(variant, "variant cannot be null");

        this.clusterManager = clusterManager;
        this.variant = variant;

        this.setHeight("20em");
        this.setWidth("35em");

        this.item = clusterManager.getItem();
        this.pattern = pattern;

        descriptionTextArea = new TextArea();
        descriptionTextArea.setMaxLength(2048);
        descriptionTextArea.setSizeFull();
        descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

        okButton = new Button("Ok");
        okButton.addClickShortcut(Key.ENTER);
        okButton.addClickListener(event ->
        {
            oldDescription = descriptionTextArea.getValue();
            this.close();
        });

        cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            if(oldDescription != null)
                descriptionTextArea.setValue(oldDescription);

            this.close();
        });

        descriptionTextArea.setValue(Objects.requireNonNullElse(variant.getDescription(), ""));
        oldDescription = descriptionTextArea.getValue();

        this.add(descriptionTextArea);
        this.getFooter().add(cancelButton, okButton);
        this.setCloseOnOutsideClick(false);
    }

    public void saveDescription()
    {
        variant.setDescription(descriptionTextArea.getValue());
    }

    public TextArea getDescriptionTextArea()
    {
        return descriptionTextArea;
    }

    public Button getOkButton()
    {
        return okButton;
    }

    public Button getCancelButton()
    {
        return cancelButton;
    }
}

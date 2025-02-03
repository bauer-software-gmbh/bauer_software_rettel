package de.bauersoft.views.menuBuilder;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.views.menuBuilder.cluster.MenuBuilderClusterManager;

public class MenuBuilderPatternSelectorDialog extends Dialog
{
    private MenuBuilderClusterManager clusterManager;

    private ComboBox<Pattern> patternBox;

    private Button selectButton;
    private Button cancelButton;

    public MenuBuilderPatternSelectorDialog(MenuBuilderClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;

        patternBox = new ComboBox<>();

        patternBox.setItems(clusterManager.getPatternPoolDataProvider());
        if(!clusterManager.getPatternPool().isEmpty())
            patternBox.setValue(clusterManager.getPatternPool().getFirst());

        patternBox.setItemLabelGenerator(item -> item.getName());

        patternBox.addValueChangeListener(event ->
        {
            clusterManager.removePatternPool(event.getValue());

            if(event.getOldValue() != null)
                clusterManager.addPatternPool(event.getOldValue());
        });



        selectButton = new Button("Ok");
        selectButton.addClickShortcut(Key.ENTER);

        selectButton.addClickListener(event ->
        {
            if(patternBox.getValue() == null)
            {
                patternBox.setInvalid(true);
                return;
            }

            clusterManager.addPatternCluster(patternBox.getValue());
            clusterManager.updateClusters();

            clusterManager.removePatternPool(patternBox.getValue());

            this.close();
        });

        cancelButton = new Button("Abbruch");
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        cancelButton.addClickListener(event ->
        {
            this.close();
        });



        this.add(patternBox);
        this.getFooter().add(cancelButton, selectButton);
        this.open();
    }


}

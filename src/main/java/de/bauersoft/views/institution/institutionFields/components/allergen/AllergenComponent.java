package de.bauersoft.views.institution.institutionFields.components.allergen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.services.AllergenService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AllergenComponent extends VerticalLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;

    private final AllergenMapContainer allergenMapContainer;

    private InstitutionField institutionField;

    private final List<Allergen> allergenPool;
    private final ListDataProvider<Allergen> allergenListDataProvider;

    private final AllergenService allergenService;

    private List<AllergenRow> allergenRows;

    private final Button addButton;

    public AllergenComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, AllergenMapContainer allergenMapContainer)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;

        this.allergenMapContainer = allergenMapContainer;

        this.institutionField = institutionFieldDialog.getInstitutionField();

        this.allergenService = institutionDialog.getAllergenService();

        allergenPool = new ArrayList<>(allergenService.findAll());
        allergenListDataProvider = new ListDataProvider<>(allergenPool);

        allergenRows = new ArrayList<>();

        addButton = new Button("Allergen hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        this.add(addButton);

        addButton.addClickListener(event ->
        {
            AllergenContainer container = (AllergenContainer) allergenMapContainer.addIfAbsent(allergenMapContainer.nextMapper(), () ->
            {
                InstitutionAllergen institutionAllergen = new InstitutionAllergen();
                institutionAllergen.setInstitutionField(institutionField);

                return institutionAllergen;
            }, allergenContainer ->
            {
                ((AllergenContainer) allergenContainer).setIsNew(true);
            });

            AllergenRow allergenRow = new AllergenRow(container);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        });

        for(Container<InstitutionAllergen, Long> container : allergenMapContainer.getContainers())
        {
            if(!container.getState().view()) continue;

            AllergenRow allergenRow = new AllergenRow((AllergenContainer) container);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        }
    }

    private class AllergenRow extends HorizontalLayout
    {
        private AtomicReference<AllergenContainer> container;

        private Button removeButton;
        private MultiSelectComboBox<Allergen> comboBox;

        public AllergenRow(AllergenContainer container)
        {
            Objects.requireNonNull(container);

            this.container = new AtomicReference<>(container);

            removeButton = new Button(LineAwesomeIcon.MINUS_SOLID.create());
            removeButton.addClickListener(event ->
            {
                AllergenComponent.this.remove(this);
                allergenRows.remove(this);

                if(container.getState() != ContainerState.IGNORE)
                    container.setTempState(ContainerState.DELETE);
            });

            comboBox = new MultiSelectComboBox<>();
            comboBox.setItemLabelGenerator(item -> item.getName());
            comboBox.setItems(allergenListDataProvider);

            comboBox.setValue(container.getEntity().getAllergens());
            updateAllergenPool(new HashSet<>(), container.getEntity().getAllergens());

            comboBox.addValueChangeListener(event ->
            {
                container.setTempState(ContainerState.UPDATE);
                container.setTempAllergens(event.getValue());

                updateAllergenPool(event.getOldValue(), event.getValue());
            });

            this.add(removeButton, comboBox);
        }

        private void updateAllergenPool(Set<Allergen> oldValue, Set<Allergen> newValue)
        {
            oldValue.forEach(allergen -> allergenPool.add(allergen));
            newValue.forEach(allergen -> allergenPool.remove(allergen));

            allergenListDataProvider.refreshAll();
        }
    }
}

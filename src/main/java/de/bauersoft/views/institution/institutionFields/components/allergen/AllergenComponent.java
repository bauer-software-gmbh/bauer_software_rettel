package de.bauersoft.views.institution.institutionFields.components.allergen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Style;
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
            }, ContainerState.NEW);

            AllergenRow allergenRow = new AllergenRow(container);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        });

        for(Container<InstitutionAllergen, Long> container : allergenMapContainer.getContainers())
        {
            if(!container.getState().isVisible()) continue;

            AllergenRow allergenRow = new AllergenRow((AllergenContainer) container);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        }

        this.getStyle()
                .setWidth("50%")
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }

    private class AllergenRow extends HorizontalLayout
    {
        private AtomicReference<AllergenContainer> container;

        private final List<Allergen> allergenPool;
        private final ListDataProvider<Allergen> allergenListDataProvider;

        private Button removeButton;
        private MultiSelectComboBox<Allergen> comboBox;

        public AllergenRow(AllergenContainer container)
        {
            Objects.requireNonNull(container);

            this.container = new AtomicReference<>(container);

            allergenPool = new ArrayList<>(allergenService.findAll());
            allergenListDataProvider = new ListDataProvider<>(allergenPool);

            removeButton = new Button(LineAwesomeIcon.MINUS_SOLID.create());
            removeButton.addClickListener(event ->
            {
                AllergenComponent.this.remove(this);
                allergenRows.remove(this);

                updateAllergenPool(comboBox.getValue(), new HashSet<>());

                container.setTempState((container.getState() == ContainerState.NEW) ? ContainerState.NEW : ContainerState.DELETE);
            });

            comboBox = new MultiSelectComboBox<>();
            comboBox.setWidthFull();
            comboBox.setItemLabelGenerator(item -> item.getName());
            comboBox.setItems(allergenListDataProvider);

            comboBox.setValue(container.getEntity().getAllergens());
            updateAllergenPool(new HashSet<>(), container.getEntity().getAllergens());

            comboBox.addValueChangeListener(event ->
            {
                updateAllergenPool(event.getOldValue(), event.getValue());
                container.setTempAllergens(event.getValue());

                container.setTempState(ContainerState.UPDATE);
            });

            this.add(removeButton, comboBox);
            this.setWidthFull();
        }

        private void updateAllergenPool(Set<Allergen> oldValue, Set<Allergen> newValue)
        {
            oldValue.forEach(allergen -> allergenPool.add(allergen));
            newValue.forEach(allergen -> allergenPool.remove(allergen));

            allergenListDataProvider.refreshAll();
        }
    }
}

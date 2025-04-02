package de.bauersoft.views.institution.institutionFields.components.allergen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.autofilter.FilterDataProvider;
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

    private final AllergenService allergenService;
    private final InstitutionField item;

    private final AllergenMapContainer allergenMapContainer;

    private List<AllergenRow> allergenRows;

    private final Button addButton;

    public AllergenComponent(AllergenService allergenService, InstitutionField item, AllergenMapContainer allergenMapContainer)
    {
        this.allergenService = allergenService;
        this.item = item;

        this.allergenMapContainer = allergenMapContainer;

        allergenRows = new ArrayList<>();

        addButton = new Button("Allergen hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        this.add(addButton);

        addButton.addClickListener(event ->
        {
            AllergenContainer container = (AllergenContainer) allergenMapContainer.addIfAbsent(allergenMapContainer.nextMapper(), () ->
            {
                InstitutionAllergen institutionAllergen = new InstitutionAllergen();
                institutionAllergen.setInstitutionField(item);

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


        this.setMinHeight("30rem");
        this.setWidth("50%");
        this.getStyle()
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
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

                container.setTempState((container.getState() == ContainerState.NEW) ? ContainerState.NEW : ContainerState.DELETE);
            });

            comboBox = new MultiSelectComboBox<>();
            comboBox.setWidthFull();
            comboBox.setItemLabelGenerator(Allergen::getName);
            comboBox.setItems(query ->
            {
                return FilterDataProvider.lazyFilteredStream(allergenService, query, "name");
            });

            comboBox.setValue(container.getEntity().getAllergens());

            comboBox.addValueChangeListener(event ->
            {
                container.setTempAllergens(event.getValue());

                container.setTempState(ContainerState.UPDATE);
            });

            this.add(removeButton, comboBox);
            this.setWidthFull();
        }
    }
}

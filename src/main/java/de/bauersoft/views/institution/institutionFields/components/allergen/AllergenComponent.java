package de.bauersoft.views.institution.institutionFields.components.allergen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergenKey;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AllergenComponent extends VerticalLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;

    private final AllergenMapContainer allergenListContainer;

    private final List<Allergen> allergenPool;
    private final ListDataProvider<Allergen> allergenDataProvider;

    private final List<AllergenRow> allergenRows;
    private final Button addButton;

    public AllergenComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, AllergenMapContainer allergenListContainer)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionFieldDialog.getInstitutionField();
        this.allergenListContainer = allergenListContainer;

        allergenPool = new ArrayList<>(institutionDialog.getAllergenService().findAll());
        allergenDataProvider = new ListDataProvider<>(allergenPool);

        allergenRows = new ArrayList<>();

        addButton = new Button("Allergen Hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        addButton.addClickListener(event ->
        {
            AllergenRow allergenRow = new AllergenRow(null);
            allergenRows.add(allergenRow);

            this.add(allergenRow);
        });

        this.add(addButton);

        for(Container<InstitutionAllergen, InstitutionAllergenKey> container : allergenListContainer.getContainers())
        {
            if(!container.getState().view()) continue;
            AllergenRow allergenRow = new AllergenRow((AllergenContainer) container);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        }

        this.setWidthFull();
        this.getStyle()
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }

    public boolean validate()
    {
        boolean allValid = true;
        for(AllergenRow allergenRow : allergenRows)
        {
            if(!allergenRow.validate())
                allValid = false;
        }

        return allValid;
    }

    public class AllergenRow extends HorizontalLayout
    {
        private AtomicReference<AllergenContainer> currentContainer;

        private ComboBox<Allergen> allergenComboBox;
        private NumberField amountField;

        private Binder<ComboBox<Allergen>> allergenBinder;
        private Binder<NumberField> amountBinder;

        private Button removeButton;

        public AllergenRow(AllergenContainer allergenContainer)
        {
            this.currentContainer = new AtomicReference<>();
            this.currentContainer.set(allergenContainer);

            initiateComponents();

            AllergenContainer creationContainer = this.currentContainer.get();
            if(creationContainer != null)
            {
                allergenComboBox.setValue(creationContainer.getEntity().getAllergen());
                amountField.setValue(Objects.requireNonNullElse(creationContainer.getEntity().getAmount(), 0).doubleValue());
            }

            allergenComboBox.addValueChangeListener(event ->
            {
                Allergen oldValue = event.getOldValue();
                if(oldValue != null)
                {
                    allergenPool.add(oldValue);
                    AllergenContainer oldContainer = this.currentContainer.get();

                    if(oldContainer == null) return;
                    oldContainer.setTempState(ContainerState.DELETE);
                }

                Allergen value = event.getValue();
                if(value == null)
                {
                    allergenComboBox.setTooltipText("");

                    AllergenContainer currentContainer = this.currentContainer.get();
                    if(currentContainer == null) return;

                    currentContainer.setTempState(ContainerState.DELETE);

                }else //Not Null
                {
                    allergenPool.remove(value);
                    allergenComboBox.setTooltipText(value.getName());

                    AllergenContainer currentContainer = (AllergenContainer) allergenListContainer.addIfAbsent(value, () ->
                    {
                        InstitutionAllergen institutionAllergen = new InstitutionAllergen();
                        institutionAllergen.setId(new InstitutionAllergenKey(null, value.getId()));
                        institutionAllergen.setInstitutionField(institutionField);
                        institutionAllergen.setAllergen(value);

                        return institutionAllergen;
                    }, ContainerState.UPDATE);

                    currentContainer.setTempState(ContainerState.UPDATE);
                    this.currentContainer.set(currentContainer);

                    amountField.setValue(Double.valueOf(currentContainer.getTempAmount()));
                }

                allergenDataProvider.refreshAll();
            });

            amountField.addValueChangeListener(event ->
            {
                AllergenContainer currentContainer = this.currentContainer.get();
                if(currentContainer == null) return;

                currentContainer.setTempAmount(Objects.requireNonNullElse(event.getValue(), 0).intValue());
                currentContainer.setTempState(ContainerState.UPDATE);
            });

            removeButton.addClickListener(event ->
            {
                Allergen value = allergenComboBox.getValue();
                if(value != null)
                {
                    allergenPool.add(value);
                    allergenDataProvider.refreshAll();
                }

                AllergenComponent.this.remove(this);

                AllergenContainer currentContainer = this.currentContainer.get();
                if(currentContainer == null) return;

                currentContainer.setTempState(ContainerState.DELETE);
            });

            this.setWidthFull();
        }

        private void initiateComponents()
        {
            allergenComboBox = new ComboBox<>();
            allergenComboBox.setClearButtonVisible(true);
            allergenComboBox.setItemLabelGenerator(Allergen::getName);
            allergenComboBox.setItems(allergenDataProvider);
            allergenComboBox.setWidthFull();

            allergenBinder = new Binder<>();
            allergenBinder.forField(allergenComboBox)
                    .asRequired()
                    .bind(ComboBox::getValue, ComboBox::setValue);

            amountField = new NumberField();
            amountField.setMaxWidth("3em");
            amountField.setAllowedCharPattern("[0-9]");
            amountField.setMin(0);
            amountField.setMax(Integer.MAX_VALUE);

            amountBinder = new Binder<>();
            amountBinder.forField(amountField)
                    .asRequired()
                    .bind(NumberField::getValue, NumberField::setValue);

            removeButton = new Button(LineAwesomeIcon.MINUS_SOLID.create());

            this.add(removeButton, allergenComboBox, amountField);
        }

        public boolean validate()
        {
            allergenBinder.validate();
            amountBinder.validate();

            return allergenBinder.isValid() && amountBinder.isValid();
        }
    }
}

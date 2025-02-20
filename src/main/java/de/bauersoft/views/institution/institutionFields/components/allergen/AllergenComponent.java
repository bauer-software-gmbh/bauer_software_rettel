package de.bauersoft.views.institution.institutionFields.components.allergen;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.institution.InstitutionAllergen;
import de.bauersoft.data.entities.institution.InstitutionAllergenKey;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.container2.Container;
import de.bauersoft.views.institution.container2.ContainerMapper;
import de.bauersoft.views.institution.container2.ContainerState;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            if(container.getState().equals(ContainerState.DELETE)) continue;
            AllergenRow allergenRow = new AllergenRow((AllergenContainer) container);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        }

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
        private AtomicReference<AllergenContainer> allergenContainer;

        private ComboBox<Allergen> allergenComboBox;
        private NumberField amountField;

        private Binder<ComboBox<Allergen>> allergenBinder;
        private Binder<NumberField> amountBinder;

        private Button removeButton;

        public AllergenRow(AllergenContainer allergenContainer)
        {
            this.allergenContainer = new AtomicReference<>();
            this.allergenContainer.set(allergenContainer);

            initiateComponents();

            allergenComboBox.addValueChangeListener(event ->
            {
                Allergen value = event.getValue();
                if(value == null)
                {
                    allergenComboBox.setTooltipText(" ");
                    this.allergenContainer.set(null);

                }else
                {
                    allergenPool.remove(value);
                    allergenComboBox.setTooltipText(value.getName());

                    this.allergenContainer.set((AllergenContainer) allergenListContainer.addIfAbsent(value, () ->
                    {
                        InstitutionAllergen institutionAllergen = new InstitutionAllergen();
                        institutionAllergen.setId(new InstitutionAllergenKey());
                        institutionAllergen.setInstitutionField(institutionField);

                        return institutionAllergen;
                    }));

                    this.allergenContainer.get().setState(ContainerState.UPDATE);
                    this.allergenContainer.get().setTempState(ContainerState.UPDATE);
                    this.allergenContainer.get().setTempAllergen(value);
                    this.allergenContainer.get().setTempAmount(Objects.requireNonNullElse(amountField.getValue(), 0).intValue());
                }

                if(event.getOldValue() != null)
                    allergenPool.add(event.getOldValue());

                allergenDataProvider.refreshAll();
            });

            if(this.allergenContainer.get() != null)
                allergenComboBox.setValue(this.allergenContainer.get().getEntity().getAllergen());

            amountField.addValueChangeListener(event ->
            {
                if(this.allergenContainer.get() != null)
                    this.allergenContainer.get().setTempAmount(Objects.requireNonNullElse(event.getValue(), 0).intValue());
            });

            if(this.allergenContainer.get() != null)
                amountField.setValue(Objects.requireNonNullElse(this.allergenContainer.get().getEntity().getAmount(), 0).doubleValue());

            removeButton.addClickListener(event ->
            {
                if(allergenComboBox.getValue() != null)
                {
                    allergenPool.add(allergenComboBox.getValue());
                    allergenDataProvider.refreshAll();
                }

                AllergenComponent.this.remove(this);
                this.allergenContainer.get().setTempState(ContainerState.DELETE);
                this.allergenContainer.set(null);
            });
        }

//        public AllergenRow(AllergenContainer allergenContainer)
//        {
//            this.allergenContainer = allergenContainer;
//
//           initiateComponents();
//
//            allergenComboBox.addValueChangeListener(event ->
//            {
//                Allergen value = event.getValue();
//                if(value == null)
//                {
//                    allergenComboBox.setTooltipText("");
//                }else
//                {
//                    allergenPool.remove(value);
//                    allergenComboBox.setTooltipText(value.getName());
//
//                    allergenContainer = allergenListContainer.addIfAbsent(value, () ->
//                    {
//                        InstitutionAllergen institutionAllergen = new InstitutionAllergen();
//
//                        institutionAllergen.setId(new InstitutionAllergenKey());
//
//                        return institutionAllergen;
//                    });
//                }
//
//                if(event.getOldValue() != null)
//                    allergenPool.add(event.getOldValue());
//
//                allergenDataProvider.refreshAll();
//            });
//            allergenComboBox.setValue(allergenContainer.getEntity().getAllergen());
//
//
//
//            amountField.addValueChangeListener(event ->
//            {
//                allergenContainer.setTempAmount(Objects.requireNonNullElse(event.getValue(), 0).intValue());
//            });
//            amountField.setValue(Objects.requireNonNullElse(allergenContainer.getEntity().getAmount(), 0).doubleValue());
//
//
//
//            removeButton.addClickListener(event ->
//            {
//                if(allergenComboBox.getValue() != null)
//                {
//                    allergenPool.add(allergenComboBox.getValue());
//                    allergenDataProvider.refreshAll();
//                }
//
//                AllergenComponent.this.remove(this);
//            });
//        }

        private void initiateComponents()
        {
            allergenComboBox = new ComboBox<>();
            allergenComboBox.setClearButtonVisible(true);
            allergenComboBox.setItemLabelGenerator(Allergen::getName);
            allergenComboBox.setItems(allergenDataProvider);

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

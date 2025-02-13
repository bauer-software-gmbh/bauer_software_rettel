package de.bauersoft.views.institution.institutionFields.dialogLayer.components;

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
import de.bauersoft.views.institution.institutionFields.dialogLayer.InstitutionFieldDialog;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllergenComponent extends VerticalLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;

    private final List<Allergen> allergenPool;
    private final ListDataProvider<Allergen> allergenDataProvider;

    private final List<AllergenRow> allergenRows;
    private final Button addButton;

    public AllergenComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, InstitutionField institutionField)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionField;

        allergenPool = new ArrayList<>(institutionDialog.getAllergenService().findAll());
        allergenDataProvider = new ListDataProvider<>(allergenPool);

        allergenRows = new ArrayList<>();

        addButton = new Button("Allergen Hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        addButton.addClickListener(event ->
        {
            InstitutionAllergenKey key = new InstitutionAllergenKey();

            InstitutionAllergen institutionAllergen = new InstitutionAllergen();
            institutionAllergen.setId(key);
            institutionAllergen.setInstitutionField(institutionField);

            AllergenRow allergenRow = new AllergenRow(institutionAllergen);

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        });

        this.add(addButton);

        for(InstitutionAllergen institutionAllergen : institutionField.getInstitutionAllergens())
        {
            AllergenRow allergenRow = new AllergenRow(institutionAllergen);
            allergenPool.remove(institutionAllergen.getAllergen());

            allergenRows.add(allergenRow);
            this.add(allergenRow);
        }

        allergenDataProvider.refreshAll();

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

    public void accept()
    {
        allergenRows.forEach(AllergenRow::accept);
    }

    public class AllergenRow extends HorizontalLayout
    {
        private final InstitutionAllergen institutionAllergen;

        private final ComboBox<Allergen> allergenComboBox;
        private final NumberField amountField;

        private final Binder<ComboBox<Allergen>> allergenBinder;
        private final Binder<NumberField> amountBinder;

        private final Button removeButton;

        public AllergenRow(InstitutionAllergen institutionAllergen)
        {
            this.institutionAllergen = institutionAllergen;

            allergenComboBox = new ComboBox<>();
            allergenComboBox.setClearButtonVisible(true);
            allergenComboBox.setItems(allergenDataProvider);

            allergenComboBox.setItemLabelGenerator(Allergen::getName);

            allergenComboBox.addValueChangeListener(event ->
            {
                if(event.getValue() != null)
                {
                    allergenPool.remove(event.getValue());
                    allergenComboBox.setTooltipText(event.getValue().getName());

                }else
                {
                    allergenComboBox.setTooltipText("");
                }

                if(event.getOldValue() != null)
                {
                    allergenPool.add(event.getOldValue());
                }

                allergenDataProvider.refreshAll();
            });

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
            removeButton.addClickListener(event ->
            {
                if(allergenComboBox.getValue() != null)
                {
                    allergenPool.add(allergenComboBox.getValue());
                    allergenDataProvider.refreshAll();
                }

                allergenRows.remove(this);
                AllergenComponent.this.remove(this);
            });

            this.add(removeButton, allergenComboBox, amountField);
        }

        public boolean validate()
        {
            allergenBinder.validate();
            amountBinder.validate();

            return allergenBinder.isValid() && amountBinder.isValid();
        }

        public void accept()
        {
            if(allergenComboBox.getValue() == null) return;
            Objects.requireNonNull(institutionAllergen.getInstitutionField().getId(), "You must save the InstitutionField fist, so it gets an ID");

            institutionAllergen.getId().setInstitutionFieldId(institutionAllergen.getInstitutionField().getId());
            institutionAllergen.getId().setAllergenId(allergenComboBox.getValue().getId());
            institutionAllergen.setAllergen(allergenComboBox.getValue());

            institutionAllergen.setAmount((amountField.getValue().intValue()));

            institutionAllergen.getInstitutionField().getInstitutionAllergens().add(institutionAllergen);
        }
    }
}

package de.bauersoft.views.ingredient;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.additive.Additive;
import de.bauersoft.data.entities.allergen.Allergen;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.unit.Unit;
import de.bauersoft.data.providers.IngredientDataProvider;
import de.bauersoft.data.repositories.additive.AdditiveRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;
import de.bauersoft.services.AdditiveService;
import de.bauersoft.services.AllergenService;
import de.bauersoft.services.IngredientService;
import de.bauersoft.services.UnitService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

public class IngredientDialog extends Dialog
{

    private final FilterDataProvider<Ingredient, Long> filterDataProvider;
    private final IngredientService ingredientService;
    private final UnitService unitService;
    private final AllergenService allergenService;
    private final AdditiveService additiveService;
    private final Ingredient item;
    private final DialogState state;

    public IngredientDialog(FilterDataProvider<Ingredient, Long> filterDataProvider, IngredientService ingredientService, UnitService unitService, AllergenService allergenService, AdditiveService additiveService, Ingredient item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.ingredientService = ingredientService;
        this.unitService = unitService;
        this.allergenService = allergenService;
        this.additiveService = additiveService;
        this.item = item;
        this.state = state;

        this.setHeaderTitle(state.toString());

        Binder<Ingredient> binder = new Binder<>(Ingredient.class);
        binder.setBean(item);

        FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("50em");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(64);
        nameTextField.setRequired(true);
        nameTextField.setWidthFull();

        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setMaxLength(512);
        descriptionTextArea.setSizeFull();
        descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

        ComboBox<Unit> unitComboBox = new ComboBox<>();
        unitComboBox.setWidthFull();
        unitComboBox.setRequired(true);
        unitComboBox.setItemLabelGenerator(Unit::getName);
        unitComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyFilteredStream(unitService, query, "name");
        });

        MultiSelectComboBox<Allergen> allergenMultiSelectComboBox = new MultiSelectComboBox<>();
        allergenMultiSelectComboBox.setWidthFull();
        allergenMultiSelectComboBox.setItemLabelGenerator(Allergen::getName);
        allergenMultiSelectComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyFilteredStream(allergenService, query, "name");
        });

        MultiSelectComboBox<Additive> additiveMultiSelectComboBox = new MultiSelectComboBox<>();
        additiveMultiSelectComboBox.setWidthFull();
        additiveMultiSelectComboBox.setItemLabelGenerator(Additive::getName);
        additiveMultiSelectComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyFilteredStream(additiveService, query, "name");
        });

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "Beschreibung"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(unitComboBox, "Einheit"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(allergenMultiSelectComboBox, "Allergene"), 1);
        inputLayout.setColspan(inputLayout.addFormItem(additiveMultiSelectComboBox, "Zusatzstoffe"), 1);

		binder.forField(nameTextField).asRequired((value, context) ->
        {
            return (value != null && !value.isBlank())
                    ? ValidationResult.ok()
                    : ValidationResult.error("Name ist erforderlich");

        }).bind(Ingredient::getName, Ingredient::setName);

        binder.bind(descriptionTextArea, "description");

		binder.forField(unitComboBox).asRequired((value, context) ->
        {
            return (value != null)
                    ? ValidationResult.ok()
                    : ValidationResult.error("Einheit ist erforderlich");
        }).bind(Ingredient::getUnit, Ingredient::setUnit);

        binder.bind(allergenMultiSelectComboBox, "allergens");
        binder.bind(additiveMultiSelectComboBox, "additives");

        binder.readBean(item);

        Button saveButton = new Button("Speichern");
		saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(event ->
        {
			binder.writeBeanIfValid(item);
            if(binder.isValid())
            {
                try
                {
                    ingredientService.update(item);
                    filterDataProvider.refreshAll();

                    Notification.show("Daten wurden aktualisiert");
                    this.close();

                }catch(DataIntegrityViolationException error)
                {
                    Notification.show("Doppelter Eintrag", 5000, Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);

					error.printStackTrace();
                }
            }
        });

        Button cancelButton = new Button("Abbrechen");
		cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("180px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            filterDataProvider.refreshAll();
            this.close();
        });

        this.add(inputLayout);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}

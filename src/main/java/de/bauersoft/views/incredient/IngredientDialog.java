package de.bauersoft.views.incredient;

import org.springframework.dao.DataIntegrityViolationException;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import de.bauersoft.data.entities.Additive;
import de.bauersoft.data.entities.Allergen;
import de.bauersoft.data.entities.Ingredient;
import de.bauersoft.data.entities.Unit;
import de.bauersoft.data.providers.IngredientDataProvider;
import de.bauersoft.data.repositories.additive.AdditiveRepository;
import de.bauersoft.data.repositories.allergen.AllergenRepository;
import de.bauersoft.data.repositories.unit.UnitRepository;
import de.bauersoft.services.IngredientService;
import de.bauersoft.views.DialogState;

public class IngredientDialog extends Dialog {

	

	public IngredientDialog(IngredientService service,
			UnitRepository unitRepository,
			AllergenRepository allergenRepository,
			AdditiveRepository additiveRepository,
			IngredientDataProvider dataProvider, Ingredient item, DialogState state) {
		this.setHeaderTitle(state.toString());
		Binder<Ingredient> binder = new Binder<>(Ingredient.class);
		binder.setBean(item);
		FormLayout inputLayout = new FormLayout();
		inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		TextField nameTextField = new TextField();
		nameTextField.setMaxLength(64);
		nameTextField.setRequired(true);
		nameTextField.setMinWidth("20em");
		TextArea descriptionTextArea = new TextArea();
		descriptionTextArea.setMaxLength(512);
		descriptionTextArea.setSizeFull();
		descriptionTextArea.setMinHeight("calc(4* var(--lumo-text-field-size))");
		ComboBox<Unit> unitComboBox = new ComboBox<Unit>();
		unitComboBox.setItemLabelGenerator(unit -> unit.getName());
		unitComboBox.setItems(unitRepository.findAll());
		unitComboBox.setRequired(true);
		MultiSelectComboBox<Allergen> allergenMultiSelectComboBox = new MultiSelectComboBox<Allergen>();
		allergenMultiSelectComboBox.setItemLabelGenerator(allergen -> allergen.getName());
		allergenMultiSelectComboBox.setItems(allergenRepository.findAll());
		allergenMultiSelectComboBox.setWidthFull();
		MultiSelectComboBox<Additive> additiveMultiSelectComboBox = new MultiSelectComboBox<Additive>();
		additiveMultiSelectComboBox.setItemLabelGenerator(allergen -> allergen.getName());
		additiveMultiSelectComboBox.setItems(additiveRepository.findAll());
		additiveMultiSelectComboBox.setWidthFull();
		inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "name"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(descriptionTextArea, "description"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(unitComboBox, "unit"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(allergenMultiSelectComboBox, "allergen"), 1);
		inputLayout.setColspan(inputLayout.addFormItem(additiveMultiSelectComboBox, "additive"), 1);
		binder.bind(nameTextField, "name");
		binder.bind(descriptionTextArea, "description");
		binder.bind(unitComboBox, "unit");
		binder.bind(allergenMultiSelectComboBox, "allergens");
		binder.bind(additiveMultiSelectComboBox, "additives");
		binder.setBean(item);
		Button saveButton = new Button("save");
		saveButton.setMinWidth("150px");
		saveButton.setMaxWidth("180px");
		saveButton.addClickListener(event -> {
			if (binder.isValid()) {
				try {
					service.update(binder.getBean());
					dataProvider.refreshAll();
					Notification.show("Data updated");
					this.close();
				} catch (DataIntegrityViolationException error) {
					Notification.show("Duplicate entry", 5000, Position.MIDDLE)
							.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
			}
		});
		Button cancelButton = new Button("cancel");
		cancelButton.setMinWidth("150px");
		cancelButton.setMaxWidth("180px");
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		cancelButton.addClickListener(e -> {
			binder.removeBean();
			this.close();
		});
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
		inputLayout.setHeight("50vh");
		inputLayout.setMaxHeight("20em");
		Span spacer = new Span();
		spacer.setWidthFull();
		this.add(inputLayout);
		this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		this.setModal(true);
		this.open();
	}
}

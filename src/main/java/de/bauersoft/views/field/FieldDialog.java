package de.bauersoft.views.field;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.Multiplier;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.field.FieldMultiplier;
import de.bauersoft.data.entities.field.FieldMultiplierKey;
import de.bauersoft.data.providers.FieldDataProvider;
import de.bauersoft.data.repositories.course.CourseRepository;
import de.bauersoft.data.repositories.field.FieldMultiplierRepository;
import de.bauersoft.services.CourseService;
import de.bauersoft.services.FieldMultiplierService;
import de.bauersoft.services.FieldService;
import de.bauersoft.views.DialogState;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldDialog extends Dialog
{
    private FieldService fieldService;
    private FieldDataProvider fieldDataProvider;
    private FieldMultiplierService fieldMultiplierService;
    private FieldMultiplierRepository fieldMultiplierRepository;
    private CourseService courseService;
    private CourseRepository courseRepository;

    private Field item;

    private DialogState state;

    private LinkedHashMap<FieldMultiplier, NumberField> multipliersMap;

    private FlexLayout multipliersInputLayout;

    public FieldDialog(FieldService fieldService, FieldDataProvider fieldDataProvider, FieldMultiplierService fieldMultiplierService, CourseService courseService, Field item, DialogState state)
    {
        this.fieldService = fieldService;
        this.fieldDataProvider = fieldDataProvider;
        this.fieldMultiplierService = fieldMultiplierService;
        this.fieldMultiplierRepository = fieldMultiplierService.getRepository();
        this.courseService = courseService;
        this.courseRepository = courseService.getRepository();
        this.item = item;
        this.state = state;

		this.setHeaderTitle(state.toString());

        Binder<Field> binder = new Binder<>(Field.class);

        FormLayout inputLayout = new FormLayout();
		inputLayout.setWidth("50vw");
		inputLayout.setMaxWidth("50em");
//		inputLayout.setHeight("50vh");
//		inputLayout.setMaxHeight("18em");

        inputLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        TextField nameTextField = new TextField();
        nameTextField.setMaxLength(50);
        nameTextField.setRequired(true);
        nameTextField.setMinWidth("20em");

        inputLayout.setColspan(inputLayout.addFormItem(nameTextField, "Name"), 1);

        binder.forField(nameTextField).asRequired((value, context) ->
		{
			return (value != null && !value.isBlank())
					? ValidationResult.ok()
					: ValidationResult.error("Name ist erforderlich");
		}).bind("name");

        binder.setBean(item);

        multipliersInputLayout = new FlexLayout();
        multipliersInputLayout.setWidth("50vw");
        multipliersInputLayout.setMaxWidth("50em");
        multipliersInputLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);

        initFieldMultiplier();
        multipliersMap.values().forEach(textField -> multipliersInputLayout.add(textField));

        Button saveButton = new Button("Speichern");
		saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
			binder.validate();
            if(multipliersMap.values().stream().anyMatch(numberField -> numberField.isInvalid()))
                return;

            if(binder.isValid())
            {
                try
				{
					fieldService.update(binder.getBean());

                    fieldMultiplierRepository.saveAll(multipliersMap.keySet());

                    fieldDataProvider.refreshAll();
					Notification.show("Daten wurden aktualisiert");
					this.close();

				}catch(DataIntegrityViolationException error)
				{
					Notification.show("Doppelter Eintrag", 5000, Notification.Position.MIDDLE)
							.addThemeVariants(NotificationVariant.LUMO_ERROR);
				}
            }
        });

        Button cancelButton = new Button("Abbrechen");
		cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.setMinWidth("150px");
        cancelButton.setMaxWidth("200px");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(e ->
        {
            binder.removeBean();
            this.close();
        });

        Span spacer = new Span();
        spacer.setWidthFull();

        this.add(inputLayout, multipliersInputLayout);
        this.getFooter().add(new HorizontalLayout(spacer, saveButton, cancelButton));
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }

    public void initFieldMultiplier()
    {
        if(multipliersMap != null)
            return;

        List<FieldMultiplier> fieldMultipliers = fieldMultiplierRepository.findAllByFieldId(item.getId());
        multipliersMap = Stream.concat
                (
                        fieldMultipliers.stream().map(fieldMultiplier ->
                        {
                            //fieldMultiplier.setCourse(courseRepository.findById(fieldMultiplier.getId().getCourseId()).orElseThrow());

                            NumberField textField = new NumberField(fieldMultiplier.getCourse().getName());
                            //textField.setAllowedCharPattern("[0-9.,]");
                            textField.setMin(0);
                            textField.setMax(Double.MAX_VALUE);
                            textField.setTooltipText(fieldMultiplier.getCourse().getName());
                            textField.getElement().getStyle().set("margin", "5px");
                            textField.setWidth("calc(20% - 10px)");

                            if(fieldMultiplier.getMultiplier() < 0)
                                fieldMultiplier.setMultiplier(Multiplier.defaultMultiplier);

                            textField.setPlaceholder(String.valueOf(fieldMultiplier.getMultiplier()).replace(".", ","));

                            textField.addValueChangeListener(event ->
                            {
                                if(!textField.isEmpty())
                                    fieldMultiplier.setMultiplier(Double.valueOf(textField.getValue()));
                            });

                            return new AbstractMap.SimpleEntry<>(fieldMultiplier, textField);
                        }),
                        courseRepository.findAll()
                                .stream()
                                .filter(course -> fieldMultipliers.stream().noneMatch(fieldMultiplier -> fieldMultiplier.getId().getCourseId().equals(course.getId())))
                                .map(course ->
                                {
                                    FieldMultiplierKey key = new FieldMultiplierKey();
                                    key.setCourseId(course.getId());

                                    FieldMultiplier fieldMultiplier = new FieldMultiplier();
                                    fieldMultiplier.setId(key);
                                    fieldMultiplier.setField(item);
                                    fieldMultiplier.setCourse(course);

                                    fieldMultiplier.setMultiplier(1d);

                                    NumberField textField = new NumberField(course.getName());
                                    //textField.setAllowedCharPattern("[0-9.,]");
                                    textField.setMin(0);
                                    textField.setMax(Double.MAX_VALUE);
                                    textField.setTooltipText(course.getName());
                                    textField.getElement().getStyle().set("margin", "5px");
                                    textField.setWidth("calc(20% - 10px)");

                                    textField.setPlaceholder(String.valueOf(fieldMultiplier.getMultiplier()).replace(".", ","));
                                    textField.addValueChangeListener(event ->
                                    {
                                        if(!textField.isEmpty())
                                            fieldMultiplier.setMultiplier(Double.valueOf(textField.getValue()));
                                    });

                                    return new AbstractMap.SimpleEntry<>(fieldMultiplier, textField);
                                })

                ).sorted((entry1, entry2) ->
        {
            //return courseRepository.getByCourseId(entry1.getKey().getId().getCourseId()).getName()
            //		.compareTo(courseRepository.getByCourseId(entry2.getKey().getId().getCourseId()).getName());
            return entry1.getKey().getCourse().getName().compareTo(entry2.getKey().getCourse().getName());

        }).collect(Collectors.toMap
                (
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new
                ));
    }
}

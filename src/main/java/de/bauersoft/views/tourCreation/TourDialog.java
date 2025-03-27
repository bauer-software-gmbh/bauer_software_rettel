package de.bauersoft.views.tourCreation;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.ListDataProvider;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.tour.driver.Driver;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tour.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.tourCreation.tourInstitution.TourInstitutionComponent;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CssImport("./themes/rettels/components/vaadin-text-field.css")
public class TourDialog extends Dialog
{
    private static final DateTimeFormatter formatter;

    static
    {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    private final FilterDataProvider<Tour, Long> filterDataProvider;

    private final TourService tourService;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final VehicleDowntimeService vehicleDowntimeService;
    private final InstitutionService institutionService;
    private final TourInstitutionService tourInstitutionService;

    private final Tour item;
    private final DialogState state;

    private final List<Driver> unplannedDriversPool;
    private final ListDataProvider<Driver> unplannedDriversDataProvider;
    private final ListDataProvider<Driver> unplannedAllowedDriversDataProvider;

    public TourDialog(FilterDataProvider<Tour, Long> filterDataProvider, TourService tourService, DriverService driverService, VehicleService vehicleService, VehicleDowntimeService vehicleDowntimeService, InstitutionService institutionService, TourInstitutionService tourInstitutionService, Tour item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.tourService = tourService;
        this.driverService = driverService;
        this.vehicleDowntimeService = vehicleDowntimeService;
        this.vehicleService = vehicleService;
        this.institutionService = institutionService;
        this.tourInstitutionService = tourInstitutionService;
        this.item = item;
        this.state = state;

        this.unplannedDriversPool = driverService.findAllUnplannedDrivers(item.isHolidayMode());
        this.unplannedDriversDataProvider = new ListDataProvider<>(unplannedDriversPool);
        this.unplannedAllowedDriversDataProvider = new ListDataProvider<>(unplannedDriversPool);

        unplannedAllowedDriversDataProvider.addFilter(driver ->
        {
            return driver.canDriveTour(item);
        });

        Binder<Tour> binder = new Binder<>(Tour.class);

        TourInstitutionComponent tourInstitutionComponent = new TourInstitutionComponent(item, institutionService, tourInstitutionService);
        tourInstitutionComponent.setAvailableInstitutions(tourInstitutionService.findAllUnplannedInstitutions(item.isHolidayMode()));

        FormLayout  formLayout = new FormLayout();
        formLayout.setWidth("50em");
        //formLayout.setHeight("75em");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));



        HorizontalLayout tourLayout = new HorizontalLayout();
        tourLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        TextField nameField = new TextField("Tour-Name");
        nameField.setAutofocus(true);
        nameField.setRequired(true);
        nameField.setMaxLength(64);
        nameField.setWidth("20em");

        tourLayout.add(nameField);

        Checkbox holidayCheckbox = new Checkbox("Feiertagsmodus");
        holidayCheckbox.setEnabled(state == DialogState.NEW);
        holidayCheckbox.getStyle()
                .setWidth("2em")
                .setHeight("2em");

        tourLayout.add(holidayCheckbox);



        HorizontalLayout driverLayout = new HorizontalLayout();
        driverLayout.setWidthFull();

        ComboBox<Driver> driverComboBox = new ComboBox<>("Hauptfahrer");
        driverComboBox.setEnabled(state != DialogState.NEW);
        driverComboBox.setItems(unplannedAllowedDriversDataProvider);
        driverComboBox.setItemLabelGenerator(driver ->
        {
            User user = driver.getUser();
            return user.getName() + " " + user.getSurname();
        });

        DatePicker drivesUntilDatePicker = new DatePicker("Fährt bis");
        drivesUntilDatePicker.setEnabled(state != DialogState.NEW);
        driverLayout.add(driverComboBox, drivesUntilDatePicker);



        HorizontalLayout coDriverLayout = new HorizontalLayout();

        ComboBox<Driver> coDriverComboBox = new ComboBox<>("Beifahrer");
        coDriverComboBox.setEnabled(state != DialogState.NEW);
        coDriverComboBox.setClearButtonVisible(true);
        coDriverComboBox.setItems(unplannedDriversDataProvider);
        coDriverComboBox.setItemLabelGenerator(driver ->
        {
            User user = driver.getUser();
            return user.getName() + " " + user.getSurname();
        });

        DatePicker coDrivesUntilDatePicker = new DatePicker("Fährt bis");
        coDrivesUntilDatePicker.setEnabled(state != DialogState.NEW);
        coDriverLayout.add(coDriverComboBox, coDrivesUntilDatePicker);



        HorizontalLayout vehicleLayout = new HorizontalLayout();

        ComboBox<Vehicle> vehicleComboBox = new ComboBox<>("Fahrzeug");
        vehicleComboBox.setItems(vehicleService.findAllUnplannedVehicles(holidayCheckbox.getValue()));
        vehicleComboBox.setItemLabelGenerator(Vehicle::getLicensePlate);

        TextField nextDowntimeField = new TextField("Nächste Ausfallzeit");
        nextDowntimeField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        nextDowntimeField.setReadOnly(true);



        holidayCheckbox.addValueChangeListener(event ->
        {
            tourInstitutionComponent.setAvailableInstitutions(tourInstitutionService.findAllUnplannedInstitutions(event.getValue()));

            vehicleComboBox.setItems(vehicleService.findAllUnplannedVehicles(event.getValue()));
        });

        drivesUntilDatePicker.addValueChangeListener(event ->
        {
            if(LocalDate.now().isAfter(event.getValue()))
            {
                drivesUntilDatePicker.getStyle().setColor("red");
            }else drivesUntilDatePicker.getStyle().setColor("var(--lumo-body-text-color)");
        });

        driverComboBox.addValueChangeListener(event ->
        {
            Driver oldDriver = event.getOldValue();
            if(oldDriver != null)
                unplannedDriversPool.add(oldDriver);

            Driver driver = event.getValue();
            if(driver != null)
                unplannedDriversPool.remove(driver);

            unplannedDriversDataProvider.refreshAll();
            unplannedAllowedDriversDataProvider.refreshAll();
        });

        coDriverComboBox.addValueChangeListener(event ->
        {
            Driver oldCoDriver = event.getOldValue();
            if(oldCoDriver != null)
                unplannedDriversPool.add(oldCoDriver);

            Driver coDriver = event.getValue();
            if(coDriver != null)
                unplannedDriversPool.remove(coDriver);

            unplannedDriversDataProvider.refreshAll();
            unplannedAllowedDriversDataProvider.refreshAll();
        });

        coDrivesUntilDatePicker.addValueChangeListener(event ->
        {
            if(LocalDate.now().isAfter(event.getValue()))
            {
                coDrivesUntilDatePicker.getStyle().setColor("red");
            }else coDrivesUntilDatePicker.getStyle().setColor("var(--lumo-body-text-color)");
        });
        coDriverComboBox.addValueChangeListener(event ->
        {
            coDrivesUntilDatePicker.setRequired(event.getValue() != null);
        });

        vehicleComboBox.addValueChangeListener(event ->
        {
            if(event.getValue() == null)
            {
                nextDowntimeField.setValue("");
                nextDowntimeField.setTooltipText("");

                nextDowntimeField.removeClassName("vaadin-input-field-color-red");
                return;
            }

            Optional<VehicleDowntime> downtimeOptional = vehicleDowntimeService.getNextVehicleDowntime(event.getValue().getId());
            if(downtimeOptional.isPresent())
            {
                VehicleDowntime vehicleDowntime = downtimeOptional.get();
                nextDowntimeField.setTooltipText("Grund: " + vehicleDowntime.getHeader());

                if(vehicleDowntime.getEndDate() == null)
                {
                    nextDowntimeField.setValue(formatter.format(vehicleDowntime.getStartDate()).toString());

                }else nextDowntimeField.setValue(formatter.format(vehicleDowntime.getStartDate()) + "-" + formatter.format(vehicleDowntime.getEndDate()));

                if(TourView.isVehicleOff(LocalDate.now(), vehicleDowntime.getStartDate(), vehicleDowntime.getEndDate()))
                {
                    nextDowntimeField.addClassName("vaadin-input-field-color-red");

                }else nextDowntimeField.removeClassName("vaadin-input-field-color-red");

            }else
            {
                nextDowntimeField.setValue("Keine");
                nextDowntimeField.setTooltipText(":)");

                nextDowntimeField.removeClassName("vaadin-input-field-color-red");
            }

        });

        vehicleLayout.add(vehicleComboBox, nextDowntimeField);

        formLayout.setColspan(formLayout.addFormItem(tourLayout, "Tour"), 1);
        formLayout.setColspan(formLayout.addFormItem(driverLayout, "Fahrer"), 1);
        formLayout.setColspan(formLayout.addFormItem(coDriverLayout, "Beifahrer"), 1);
        formLayout.setColspan(formLayout.addFormItem(vehicleLayout, "Fahrzeug"), 1);

        binder.forField(nameField).asRequired().bind(Tour::getName, Tour::setName);
        binder.forField(holidayCheckbox).bind(Tour::isHolidayMode, Tour::setHolidayMode);

        binder.forField(driverComboBox).withValidator((value, context) ->
        {
            if(state == DialogState.NEW)
                return ValidationResult.ok();

            return (value == null)
                    ? ValidationResult.error("Die Tour braucht einen Fahrer")
                    : (value.equals(coDriverComboBox.getValue()))
                    ? ValidationResult.error("Der Hauptfahrer kann nicht gleichzeitig Beifahrer sein")
                    : ValidationResult.ok();

        }).bind(Tour::getDriver, Tour::setDriver);

        binder.forField(drivesUntilDatePicker)
                .withValidator((value, context) ->
        {
            if(state == DialogState.NEW)
                return ValidationResult.ok();

            return (value == null)
                    ? ValidationResult.error("")
                    : (value.isBefore(LocalDate.now()))
                    ? ValidationResult.error("Das Enddatum muss in der Zukunft liegen")
                    : ValidationResult.ok();

        }).bind(Tour::getDrivesUntil, Tour::setDrivesUntil);

        binder.forField(coDriverComboBox).withValidator((value, context) ->
        {
            return (value == null)
                    ? ValidationResult.ok()
                    : (value.equals(driverComboBox.getValue()))
                    ? ValidationResult.error("Der Beifahrer kann nicht gleichzeitig Hauptfahrer sein")
                    : ValidationResult.ok();
        }).bind(Tour::getCoDriver, Tour::setCoDriver);

        binder.forField(coDrivesUntilDatePicker).withValidator((value, context) ->
        {
            return (coDriverComboBox.getValue() == null)
                    ? ValidationResult.ok()
                    : (value == null)
                    ? ValidationResult.error("")
                    : (value.isBefore(LocalDate.now()))
                    ? ValidationResult.error("Das Enddatum muss in der Zukunft liegen")
                    : ValidationResult.ok();

        }).bind(Tour::getCoDrivesUntil, Tour::setCoDrivesUntil);
        binder.forField(vehicleComboBox).asRequired().bind(Tour::getVehicle, Tour::setVehicle);

        binder.readBean(item);

        Button saveButton = new Button("Speichern");
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.setMinWidth("150px");
        saveButton.setMaxWidth("180px");
        saveButton.addClickListener(e ->
        {
            binder.writeBeanIfValid(item);
            if(binder.isValid())
            {
                try
                {
                    tourService.update(item);

                    tourInstitutionComponent.getMapContainer()
                            .acceptTemporaries()
                            .evaluate(container ->
                            {
                                container.getId().setTourId(item.getId());
                            }).run(tourInstitutionService);

                    filterDataProvider.refreshAll();

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
            filterDataProvider.refreshAll();
            this.close();
        });

        this.add(formLayout, tourInstitutionComponent);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}

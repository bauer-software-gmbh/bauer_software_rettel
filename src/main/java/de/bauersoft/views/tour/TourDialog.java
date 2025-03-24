package de.bauersoft.views.tour;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.tourPlanning.vehicle.Vehicle;
import de.bauersoft.data.entities.tourPlanning.vehicle.VehicleDowntime;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tourPlanning.*;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.tour.tourInstitution.TourInstitutionComponent;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TourDialog extends Dialog
{
    private static final DateTimeFormatter formatter;

    static
    {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    private final FilterDataProvider<Tour, Long> filterDataProvider;
    private final FilterDataProvider<Driver, Long> driverFilterDataProvider;

    private final TourService tourService;
    private final DriverService driverService;
    private final VehicleService vehicleService;
    private final VehicleDowntimeService vehicleDowntimeService;
    private final InstitutionService institutionService;
    private final TourInstitutionService tourInstitutionService;

    private final Tour item;
    private final DialogState state;

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

        driverFilterDataProvider = new FilterDataProvider<>(driverService);

        Binder<Tour> binder = new Binder<>(Tour.class);

        FormLayout  formLayout = new FormLayout();
        formLayout.setWidth("50em");
        //formLayout.setHeight("75em");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField nameField = new TextField();
        nameField.setAutofocus(true);
        nameField.setRequired(true);
        nameField.setMaxLength(64);
        nameField.setWidth("20em");

        HorizontalLayout driverLayout = new HorizontalLayout();
        driverLayout.setWidthFull();
//        Filter<Driver> filter = new Filter<Driver>("driveableTours", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            return criteriaBuilder.equal(path.get("id").as(Long.class), item.getId());
//        }).setIgnoreFilterInput(true);
//
//        driverFilterDataProvider.addFilter(filter);
//
//        Filter<Driver> filter2 = new Filter<Driver>("user", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            return criteriaBuilder.like(path.get("name").as(String.class), filterInput + "%");
//        });
//
//        driverFilterDataProvider.addFilter(filter2);
        ComboBox<Driver> driverComboBox = new ComboBox<>("Hauptfahrer");
//        driverComboBox.setDataProvider((filterText, offset, limit) ->
//        {
//            Notification.show("Filter: " + filterText);
//            filter2.setFilterInput(filterText);
//
//            Pageable pageable = PageRequest.of(offset / limit, limit);
//
//            return driverService.getRepository().findAll(driverFilterDataProvider.buildFilter(), pageable).stream();
//        }, query ->
//        {
//            return (int) driverService.getRepository().count(driverFilterDataProvider.buildFilter());
//        });
        driverComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyStream(driverService, query);
        }, query -> (int) driverService.count());
        driverComboBox.setItemLabelGenerator(driver ->
        {
            User user = driver.getUser();
            return user.getName() + " " + user.getSurname();
        });

        DatePicker drivesUntilDatePicker = new DatePicker("Fährt bis");

        driverLayout.add(driverComboBox, drivesUntilDatePicker);

        HorizontalLayout coDriverLayout = new HorizontalLayout();

        ComboBox<Driver> coDriverComboBox = new ComboBox<>("Beifahrer");
        coDriverComboBox.setClearButtonVisible(true);
        coDriverComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyStream(driverService, query);
        }, query -> (int) driverService.count());
        coDriverComboBox.setItemLabelGenerator(driver ->
        {
            User user = driver.getUser();
            return user.getName() + " " + user.getSurname();
        });

        DatePicker coDrivesUntilDatePicker = new DatePicker("Fährt bis");

        coDriverComboBox.addValueChangeListener(event ->
        {
            coDrivesUntilDatePicker.setRequired(event.getValue() != null);
        });

        coDriverLayout.add(coDriverComboBox, coDrivesUntilDatePicker);

        HorizontalLayout vehicleLayout = new HorizontalLayout();

        ComboBox<Vehicle> vehicleComboBox = new ComboBox<>("Fahrzeug");
        vehicleComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyStream(vehicleService, query);

        }, query -> (int) vehicleService.count());
        vehicleComboBox.setItemLabelGenerator(Vehicle::getLicensePlate);

        TextField nextDowntimeField = new TextField("Nächste Ausfallzeit");
        nextDowntimeField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
        nextDowntimeField.setReadOnly(true);

        vehicleComboBox.addValueChangeListener(event ->
        {
            Optional<VehicleDowntime> downtimeOptional = vehicleDowntimeService.getNextVehicleDowntime(event.getValue().getId());
            if(downtimeOptional.isPresent())
            {
                VehicleDowntime vehicleDowntime = downtimeOptional.get();
                nextDowntimeField.setTooltipText("Grund: " + vehicleDowntime.getHeader());

                if(vehicleDowntime.getEndDate() == null)
                {
                    nextDowntimeField.setValue(formatter.format(vehicleDowntime.getStartDate()).toString());

                }else nextDowntimeField.setValue(formatter.format(vehicleDowntime.getStartDate()) + "-" + formatter.format(vehicleDowntime.getEndDate()));

            }else
            {
                nextDowntimeField.setValue("Keine");
                nextDowntimeField.setTooltipText(":)");
            }

        });

        vehicleLayout.add(vehicleComboBox, nextDowntimeField);

        formLayout.setColspan(formLayout.addFormItem(nameField, "Tour-Name"), 1);
        formLayout.setColspan(formLayout.addFormItem(driverLayout, "Fahrer"), 1);
        formLayout.setColspan(formLayout.addFormItem(coDriverLayout, "Beifahrer"), 1);
        formLayout.setColspan(formLayout.addFormItem(vehicleLayout, "Fahrzeug"), 1);

        binder.forField(nameField).asRequired().bind(Tour::getName, Tour::setName);
        binder.forField(driverComboBox).asRequired().bind(Tour::getDriver, Tour::setDriver);
        binder.forField(drivesUntilDatePicker)
                .asRequired()
                .withValidator((value, context) ->
        {
            return (value == null)
                    ? ValidationResult.error("")
                    : (value.isBefore(LocalDate.now()))
                    ? ValidationResult.error("Das Enddatum muss in der Zukunft liegen")
                    : ValidationResult.ok();

        }).bind(Tour::getDrivesUntil, Tour::setDrivesUntil);

        binder.forField(coDriverComboBox).bind(Tour::getCoDriver, Tour::setCoDriver);
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

        TourInstitutionComponent tourInstitutionComponent = new TourInstitutionComponent(item, institutionService, tourInstitutionService);

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

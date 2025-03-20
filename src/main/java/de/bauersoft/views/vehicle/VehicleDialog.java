package de.bauersoft.views.vehicle;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.tourPlanning.vehicle.Vehicle;
import de.bauersoft.data.entities.tourPlanning.vehicle.VehicleDowntime;
import de.bauersoft.services.tourPlanning.DriverService;
import de.bauersoft.services.tourPlanning.VehicleDowntimeService;
import de.bauersoft.services.tourPlanning.VehicleService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.vehicle.downtimes.DowntimeComponent;
import de.bauersoft.views.vehicle.downtimes.DowntimeMapContainer;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

@Getter
public class VehicleDialog extends Dialog
{
    private final FilterDataProvider<Vehicle, Long> filterDataProvider;

    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final VehicleDowntimeService vehicleDowntimeService;

    private final Vehicle item;
    private final DialogState state;

    private final DowntimeMapContainer downtimeMapContainer;

    public VehicleDialog(FilterDataProvider<Vehicle, Long> filterDataProvider, VehicleService vehicleService, DriverService driverService, VehicleDowntimeService vehicleDowntimeService, Vehicle item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.vehicleService = vehicleService;
        this.driverService = driverService;
        this.vehicleDowntimeService = vehicleDowntimeService;
        this.item = item;
        this.state = state;

        downtimeMapContainer = new DowntimeMapContainer();
        for(VehicleDowntime vehicleDowntime : vehicleDowntimeService.findAllByVehicle_Id(item.getId()))
        {
            downtimeMapContainer.addContainer(downtimeMapContainer.nextMapper(), vehicleDowntime, ContainerState.SHOW);
        }

        this.setHeaderTitle(state.toString());

        Binder<Vehicle> binder = new Binder<>(Vehicle.class);

        this.setWidth("60em");
        this.setMaxWidth("50vw");
        this.setHeight("54em");
        this.setMaxHeight("80vh");

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        TextField licencePlateField = new TextField();
        licencePlateField.setMaxLength(16);
        licencePlateField.setAutofocus(true);
        licencePlateField.setRequired(true);
        licencePlateField.setWidth("20em");

        TextArea typeDescriptionArea = new TextArea();
        typeDescriptionArea.setMaxLength(1024);
        typeDescriptionArea.setWidthFull();
        typeDescriptionArea.setMinHeight("calc(4* var(--lumo-text-field-size))");

        DowntimeComponent downtimeComponent = new DowntimeComponent(vehicleDowntimeService, item, downtimeMapContainer);

        formLayout.setColspan(formLayout.addFormItem(licencePlateField, "Nummernschild"), 1);
        formLayout.setColspan(formLayout.addFormItem(typeDescriptionArea, "Typen Beschreibung"), 3);
        formLayout.setColspan(formLayout.addFormItem(downtimeComponent, "Ausfallzeiten"), 3);

        binder.forField(licencePlateField).asRequired().bind(Vehicle::getLicensePlate,  Vehicle::setLicensePlate);
        binder.forField(typeDescriptionArea).bind(Vehicle::getTypeDescription, Vehicle::setTypeDescription);

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
                    vehicleService.update(item);

                    downtimeMapContainer.acceptTemporaries();
                    downtimeMapContainer.run(vehicleDowntimeService);

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

        this.add(formLayout);
        this.getFooter().add(saveButton, cancelButton);
        this.setCloseOnEsc(false);
        this.setCloseOnOutsideClick(false);
        this.setModal(true);
        this.open();
    }
}

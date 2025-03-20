package de.bauersoft.views.driver;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.UserService;
import de.bauersoft.services.tourPlanning.DriverService;
import de.bauersoft.services.tourPlanning.TourService;
import de.bauersoft.views.DialogState;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

@Getter
public class DriverDialog extends Dialog
{
    private final FilterDataProvider<Driver, Long> filterDataProvider;

    private final DriverService driverService;
    private final UserService userService;
    private final TourService tourService;
    private final Driver item;
    private final DialogState state;

    public DriverDialog(FilterDataProvider<Driver, Long> filterDataProvider, DriverService driverService, UserService userService, TourService tourService, Driver item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.driverService = driverService;
        this.userService = userService;
        this.tourService = tourService;
        this.item = item;
        this.state = state;

        this.setHeaderTitle(state.toString());

        Binder<Driver> binder = new Binder<>(Driver.class);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        ComboBox<User> userComboBox = new ComboBox<>();
        userComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyStream(userService, query);
        }, query -> (int) userService.count());
        userComboBox.setItemLabelGenerator(User::getName);
        userComboBox.setWidth("20em");

        MultiSelectComboBox<Tour> tourMultiSelectComboBox = new MultiSelectComboBox<>();
        tourMultiSelectComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyStream(tourService, query);
        },query -> (int) tourService.count());
        tourMultiSelectComboBox.setItemLabelGenerator(Tour::getName);
        tourMultiSelectComboBox.setWidth("20em");

        formLayout.setColspan(formLayout.addFormItem(userComboBox, "Benutzer"), 1);
        formLayout.setColspan(formLayout.addFormItem(tourMultiSelectComboBox, "Fahrbare Touren"), 1);

        binder.forField(userComboBox).withValidator((value, context) ->
        {
            return (item.getUser().getId() != value.getId() && driverService.existsDriverByUser_Id(value.getId())) ?
                    ValidationResult.error("Dieser Benutzer ist bereits vergeben.") :
                    ValidationResult.ok();

        }).bind(Driver::getUser, Driver::setUser);

        binder.forField(tourMultiSelectComboBox).bind(Driver::getDriveableTours, Driver::setDriveableTours);

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
                    driverService.update(item);
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

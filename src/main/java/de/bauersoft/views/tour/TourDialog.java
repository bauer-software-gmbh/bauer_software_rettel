package de.bauersoft.views.tour;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.data.entities.tourPlanning.driver.Driver;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.user.User;
import de.bauersoft.services.tourPlanning.DriverService;
import de.bauersoft.services.tourPlanning.TourService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.driver.DriverView;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.swing.*;
import java.util.stream.Collectors;

public class TourDialog extends Dialog
{
    private final FilterDataProvider<Tour, Long> filterDataProvider;
    private final FilterDataProvider<Driver, Long> driverFilterDataProvider;

    private final TourService tourService;
    private final DriverService driverService;

    private final Tour item;
    private final DialogState state;

    public TourDialog(FilterDataProvider<Tour, Long> filterDataProvider, TourService tourService, DriverService driverService, Tour item, DialogState state)
    {
        this.filterDataProvider = filterDataProvider;
        this.tourService = tourService;
        this.driverService = driverService;
        this.item = item;
        this.state = state;

        driverFilterDataProvider = new FilterDataProvider<>(driverService);

        Binder<Tour> binder = new Binder<>(Tour.class);

        FormLayout  formLayout = new FormLayout();
        formLayout.setWidth("50em");
        formLayout.setHeight("75em");
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

        ComboBox<Driver> coDriverComboBox = new ComboBox<>("Beifahrer");
        coDriverComboBox.setItems(query ->
        {
            return FilterDataProvider.lazyStream(driverService, query);
        }, query -> (int) driverService.count());

        coDriverComboBox.setItemLabelGenerator(driver ->
        {
            User user = driver.getUser();
            return user.getName() + " " + user.getSurname();
        });

        driverLayout.add(driverComboBox, coDriverComboBox);

        formLayout.setColspan(formLayout.addFormItem(nameField, "Tour-Name"), 1);
        formLayout.setColspan(formLayout.addFormItem(driverLayout, "Insassen"), 1);

        binder.forField(nameField).asRequired("Die Tour braucht einen Namen").bind(Tour::getName, Tour::setName);
        binder.forField(driverComboBox).asRequired("Die Tour braucht eine Fahrer").bind(Tour::getDriver, Tour::setDriver);

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

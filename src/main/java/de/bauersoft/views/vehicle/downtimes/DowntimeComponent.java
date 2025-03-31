package de.bauersoft.views.vehicle.downtimes;

import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.tour.vehicle.Vehicle;
import de.bauersoft.data.entities.tour.vehicle.VehicleDowntime;
import de.bauersoft.services.tour.VehicleDowntimeService;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
public class DowntimeComponent extends VerticalLayout
{
    public static final EnhancedDateRangePicker.DatePickerI18n datePickerI18n;
    public static final DateTimeFormatter dateTimeFormatter;

    static
    {
        datePickerI18n = new EnhancedDateRangePicker.DatePickerI18n();
        datePickerI18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(0);

        dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ROOT);
    }

    private final VehicleDowntimeService downtimeService;

    private final Vehicle item;

    private final DowntimeMapContainer downtimeMapContainer;

    private final List<DowntimeRow> downtimeRows;

    private final Button addButton;

    public DowntimeComponent(VehicleDowntimeService downtimeService, Vehicle item, DowntimeMapContainer downtimeMapContainer)
    {
        this.downtimeService = downtimeService;
        this.item = item;
        this.downtimeMapContainer = downtimeMapContainer;

        downtimeRows = new ArrayList<>();

        addButton = new Button("Neue Ausfallzeit", LineAwesomeIcon.PLUS_SOLID.create());
        this.add(addButton);

        addButton.addClickListener(event ->
        {
            DowntimeContainer downtimeContainer = (DowntimeContainer) downtimeMapContainer.addIfAbsent(downtimeMapContainer.nextMapper(), () ->
            {
                VehicleDowntime vehicleDowntime = new VehicleDowntime();
                vehicleDowntime.setVehicle(item);

                return vehicleDowntime;

            }, ContainerState.NEW);

            DowntimeRow downtimeRow = new DowntimeRow(downtimeContainer);
            downtimeRows.add(downtimeRow);

            this.add(downtimeRow);
        });

        for(Container<VehicleDowntime, Long> container : downtimeMapContainer.getContainers())
        {
            DowntimeRow downtimeRow = new DowntimeRow((DowntimeContainer)  container);
            downtimeRows.add(downtimeRow);

            this.add(downtimeRow);
        }

        this.setPadding(false);
    }

    private class DowntimeRow extends HorizontalLayout
    {
        private final DowntimeContainer container;

        private final Button deleteButton;

        private final TextField headerField;
        private final EnhancedDateRangePicker dateRangePicker;
        private final VerticalLayout datePickerLayout;

        public DowntimeRow(DowntimeContainer container)
        {
            this.container = container;

            deleteButton = new Button(LineAwesomeIcon.MINUS_SOLID.create());
            deleteButton.addClickListener(event ->
            {
                DowntimeComponent.this.remove(this);
                downtimeRows.remove(this);
                container.setTempState((container.getState() == ContainerState.NEW) ? ContainerState.NEW : ContainerState.DELETE);
            });

            headerField = new TextField();
            headerField.setPlaceholder("TÜV, Service, o. s. ä.");
            headerField.setMaxLength(64);
            headerField.setWidth("20em");

            headerField.setValue(Objects.requireNonNullElse(container.getEntity().getHeader(), ""));

            headerField.addValueChangeListener(event ->
            {
                container.setTempState(ContainerState.UPDATE);
                container.setTempHeader(event.getValue());
            });

            dateRangePicker = new EnhancedDateRangePicker();
            dateRangePicker.setLocale(Locale.GERMAN);
            dateRangePicker.setPattern("dd.MM.yyyy");
            dateRangePicker.setI18n(datePickerI18n);
            dateRangePicker.setWidth("20em");

            dateRangePicker.setValue(new DateRange(container.getEntity().getStartDate(), container.getEntity().getEndDate()));

            dateRangePicker.addValueChangeListener(event ->
            {
                container.setTempState(ContainerState.UPDATE);
                container.setTempStartDate(event.getValue().getStartDate());
                container.setTempEndDate(event.getValue().getEndDate());
            });

            datePickerLayout = new VerticalLayout(headerField, dateRangePicker);
            datePickerLayout.setAlignItems(Alignment.START);
            datePickerLayout.setPadding(false);
            datePickerLayout.setSpacing(false);
            datePickerLayout.setWidthFull();

            this.add(deleteButton, datePickerLayout);
            this.setAlignItems(Alignment.END);
            this.setWidthFull();
        }
    }

}

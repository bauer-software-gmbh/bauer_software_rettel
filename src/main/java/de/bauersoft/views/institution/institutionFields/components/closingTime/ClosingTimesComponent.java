package de.bauersoft.views.institution.institutionFields.components.closingTime;

import com.vaadin.componentfactory.DateRange;
import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.bauersoft.components.container.Container;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.DayOfWeek;
import java.util.*;

public class ClosingTimesComponent extends VerticalLayout
{
    public static final EnhancedDateRangePicker.DatePickerI18n i18n;

    static
    {
        i18n = new EnhancedDateRangePicker.DatePickerI18n();
        i18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(DayOfWeek.MONDAY.getValue());
    }

    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;
    private final ClosingTimesMapContainer closingTimesMapContainer;

    private final List<ClosingTimeLine> closingTimeLines;
    private final Button addButton;

    public ClosingTimesComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, ClosingTimesMapContainer closingTimesMapContainer)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionFieldDialog.getInstitutionField();
        this.closingTimesMapContainer = closingTimesMapContainer;

        closingTimeLines = new ArrayList<>();

        addButton = new Button("Schließzeit hinzufügen", LineAwesomeIcon.PLUS_SOLID.create());
        this.add(addButton);

        addButton.addClickListener(event ->
        {
            int key = closingTimesMapContainer.getNextKey();
            ClosingTimesContainer closingTimesContainer = (ClosingTimesContainer) closingTimesMapContainer.addIfAbsent(key, () ->
            {
                InstitutionClosingTime institutionClosingTime = new InstitutionClosingTime();
                institutionClosingTime.setInstitutionField(institutionField);

                return institutionClosingTime;
            }, ContainerState.IGNORE);
            closingTimesContainer.setKey(key);

            ClosingTimeLine closingTimeLine = new ClosingTimeLine(closingTimesContainer);
            closingTimeLines.add(closingTimeLine);

            this.add(closingTimeLine);
        });

        for(Container<InstitutionClosingTime, Long> container : closingTimesMapContainer.getContainers())
        {
            if(container.getState().equals(ContainerState.DELETE) || container.getState().equals(ContainerState.IGNORE)) continue;
            ClosingTimeLine closingTimeLine = new ClosingTimeLine((ClosingTimesContainer) container);

            closingTimeLines.add(closingTimeLine);
            this.add(closingTimeLine);
        }

        this.setWidthFull();
        this.getStyle()
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }

    public class ClosingTimeLine extends HorizontalLayout
    {
        private final ClosingTimesContainer closingTimesContainer;

        private final TextField headerField;

        private final EnhancedDateRangePicker dateRangePicker;

        private final Button removeButton;

        public ClosingTimeLine(ClosingTimesContainer closingTimesContainer)
        {
            this.closingTimesContainer = closingTimesContainer;

            headerField = new TextField();
            headerField.setWidthFull();
            headerField.getStyle()
                    .set("--lumo-text-field-size", "var(--lumo-size-xs)");

            headerField.setPlaceholder("Sommerferien, Winterferien, o. s. ä.");

            headerField.addValueChangeListener(event ->
            {
                closingTimesContainer.setTempHeader(event.getValue());
                closingTimesContainer.setTempState(ContainerState.UPDATE);
            });

            headerField.setValue(Objects.requireNonNullElse(closingTimesContainer.getEntity().getHeader(), "").trim());

            dateRangePicker = new EnhancedDateRangePicker();
            dateRangePicker.setLocale(Locale.GERMAN);
            dateRangePicker.setI18n(i18n);
            dateRangePicker.setWidthFull();

            dateRangePicker.addValueChangeListener(event ->
            {
                closingTimesContainer.setTempStartDate(event.getValue().getStartDate());
                closingTimesContainer.setTempEndDate(event.getValue().getEndDate());

                closingTimesContainer.setTempState(ContainerState.UPDATE);
            });

            dateRangePicker.setValue(new DateRange(closingTimesContainer.getEntity().getStartDate(), closingTimesContainer.getEntity().getEndDate()));

            removeButton = new Button(LineAwesomeIcon.MINUS_SOLID.create());
            removeButton.addClickListener(event ->
            {
                closingTimesContainer.setTempState(ContainerState.DELETE);

                ClosingTimesComponent.this.remove(this);
            });

            VerticalLayout controls = new VerticalLayout(headerField, dateRangePicker);
            controls.getStyle()
                    .setMargin("0px")
                    .setPadding("0px")
                    .set("gap", "0px");

            controls.setWidthFull();

            this.add(removeButton, controls);
            this.setAlignItems(Alignment.END);
            this.setWidthFull();
            this.getStyle()
                    .setMargin("0px")
                    .setPadding("0px")
                    .set("gap", "var(--lumo-space-m)");
        }
    }
}
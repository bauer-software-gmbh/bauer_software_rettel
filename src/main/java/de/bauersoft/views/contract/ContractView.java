package de.bauersoft.views.contract;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofilter.grid.SortType;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PageTitle("Vertragslaufzeiten")
@Route(value = "contract", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ContractView extends Div
{
    private static final DatePicker.DatePickerI18n datePickerI18n;
    private static final DateTimeFormatter formatter;

    static
    {
        datePickerI18n = new DatePicker.DatePickerI18n()
                .setDateFormat("dd.MM.yyyy")
                .setToday("Heute")
                .setCancel("Abbruch")
                .setFirstDayOfWeek(1)
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"));

        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    private InstitutionService institutionService;

    private final FilterDataProvider<Institution, Long> filterDataProvider;
    private final AutofilterGrid<Institution, Long> grid;

    public ContractView(InstitutionService institutionService)
    {
        this.institutionService = institutionService;

        setClassName("content");

        filterDataProvider = new FilterDataProvider<>(institutionService);;
        grid = new AutofilterGrid<>(filterDataProvider);

        grid.setWidthFull();
        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn("name", "Name", Institution::getName, false);
        grid.addComponentColumn("contractStart", "Vertragsbeginn", institution ->
        {
            DatePicker datePicker = new DatePicker();
            datePicker.setI18n(datePickerI18n);
            datePicker.setValue(institution.getContractStart());

            datePicker.addValueChangeListener(event ->
            {
                institution.setContractStart(event.getValue());

            });

            return datePicker;
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.function("DATE_FORMAT", String.class, path, criteriaBuilder.literal("%d.%m.%Y")),
                    "%" + filterInput + "%"
            );
        }, SortType.AMOUNT);

        grid.addComponentColumn("contractEnd", "Vertragsende", institution ->
        {
            DatePicker datePicker = new DatePicker();
            datePicker.setI18n(datePickerI18n);
            datePicker.setValue(institution.getContractEnd());

            datePicker.addValueChangeListener(event ->
            {
                institution.setContractEnd(event.getValue());
            });

            return datePicker;
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.function("DATE_FORMAT", String.class, path, criteriaBuilder.literal("%d.%m.%Y")),
                    "%" + filterInput + "%"
            );
        }, SortType.AMOUNT);

        NativeLabel label = new NativeLabel("\u200B");
        label.getStyle()
                .set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-m)");

        Button cancelButton = new Button("Abbrechen");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addClickListener(event ->
        {
            filterDataProvider.refreshAll();
        });

        VerticalLayout headerLayout = new VerticalLayout(label, cancelButton);
        headerLayout.getThemeList().clear();
        headerLayout.getThemeList().add("spacing-xs");

        grid.addComponentColumn(institution ->
        {
            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button saveButton = new Button("Speichern");
            saveButton.setWidthFull();
            saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            saveButton.addClickListener(event ->
            {
                institutionService.update(institution);
                Notification.show("Daten wurden aktualisiert");
            });

            buttonLayout.add(saveButton);
            return buttonLayout;
        }).setHeader(headerLayout).setKey("options");

        this.add(grid);
    }

}

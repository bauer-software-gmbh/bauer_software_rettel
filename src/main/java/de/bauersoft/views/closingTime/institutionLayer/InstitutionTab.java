package de.bauersoft.views.closingTime.institutionLayer;

import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.autofiltergrid.AutofilterGrid;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.closingTime.institutionLayer.dialogLayer.ClosingTimeDialog;
import de.bauersoft.views.closingTime.ClosingTimeManager;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Getter
public class InstitutionTab extends Div
{
    public static final EnhancedDateRangePicker.DatePickerI18n i18n;
    private static final DateTimeFormatter formatter;

    static
    {
        i18n = new EnhancedDateRangePicker.DatePickerI18n();
        i18n.setWeekdays(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"))
                .setWeekdaysShort(Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"))
                .setMonthNames(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"))
                .setToday("Heute")
                .setCancel("Abbrechen")
                .setFirstDayOfWeek(DayOfWeek.MONDAY.getValue());

        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
    }

    private final ClosingTimeManager closingTimeManager;
    private final InstitutionTabSheet institutionTabSheet;
    private final Institution institution;

    private final InstitutionClosingTimeService closingTimeService;

    private final Tab tab;

    private HorizontalLayout buttonLayout;
    private Button addButton;
    private final AutofilterGrid<InstitutionClosingTime> grid;

    public InstitutionTab(ClosingTimeManager closingTimeManager, InstitutionTabSheet institutionTabSheet, Institution institution)
    {
        this.closingTimeManager = closingTimeManager;
        this.institutionTabSheet = institutionTabSheet;
        this.institution = institution;

        closingTimeService = closingTimeManager.getClosingTimeService();

        tab = new Tab(institution.getName());

                buttonLayout = new HorizontalLayout();
        buttonLayout.getStyle()
                .setJustifyContent(Style.JustifyContent.CENTER);

        addButton = new Button("Schließzeitraum hinzufügen");
        addButton.getStyle()
                .setFontSize("var(--lumo-font-size-xl)")
                .setBorder("1px solid grey");

        buttonLayout.add(addButton);

        grid = new AutofilterGrid<>(closingTimeService.getRepository());
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        //grid.getFilterInput().setSpecification((root, query, criteriaBuilder) -> root.get("institution").get("id").in(1));

        grid.addComponentColumn("Löschen", "4em", institutionClosingTime ->
        {
            Button button = new Button(LineAwesomeIcon.TRASH_SOLID.create());
            button.setWidth("4em");



            return button;
        });

        grid.addColumn("header", "Beschreibung", InstitutionClosingTime::getHeader, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)), "%" + filterInput.toLowerCase() + "%");
        });

        grid.addColumn("startDate", "Startdatum", institutionClosingTime ->
        {
            return institutionClosingTime.getStartDate().format(formatter).toString();
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(
                    criteriaBuilder.function("DATE_FORMAT", String.class, path, criteriaBuilder.literal("%d.%m.%Y")),
                    "%" + filterInput + "%"
            );
        });

        grid.addColumn("endDate", "Enddatum", institutionClosingTime ->
        {
            return institutionClosingTime.getEndDate().format(formatter).toString();
        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(
                    criteriaBuilder.function("DATE_FORMAT", String.class, path, criteriaBuilder.literal("%d.%m.%Y")),
                    "%" + filterInput + "%"
            );
        });

        grid.AutofilterGridContextMenu()
                .enableGridContextMenu()
                        .enableAddItem("Neue Schließzeit", event ->
                        {

                        }).enableDeleteItem("Löschen", event ->
                        {

                        });

        grid.addItemDoubleClickListener(event ->
        {
            new ClosingTimeDialog(closingTimeManager, this, event.getItem(), DialogState.EDIT);
        });

        this.add(buttonLayout, grid);

        this.setHeightFull();
        this.setWidthFull();
    }
}

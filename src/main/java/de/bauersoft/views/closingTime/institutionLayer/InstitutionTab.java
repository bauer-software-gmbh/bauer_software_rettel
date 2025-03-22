package de.bauersoft.views.closingTime.institutionLayer;

import com.vaadin.componentfactory.EnhancedDateRangePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.services.InstitutionClosingTimeService;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.views.DialogState;
import de.bauersoft.views.closingTime.ClosingTimeManager;
import de.bauersoft.views.closingTime.institutionLayer.dialogLayer.ClosingTimeDialog;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

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

    private final ClosingTimeManager manager;
    private final InstitutionTabSheet institutionTabSheet;
    private final Institution institution;

    private final InstitutionClosingTimeService closingTimeService;
    private final InstitutionService institutionService;

    private final FilterDataProvider<InstitutionClosingTime, Long> filterDataProvider;

    private final Tab tab;

    private Button addButton;
    private HorizontalLayout buttonLayout;

    private AutofilterGrid<InstitutionClosingTime, Long> grid;

    public InstitutionTab(ClosingTimeManager manager,
                          InstitutionTabSheet institutionTabSheet,
                          Institution institution)
    {
        this.manager = manager;
        this.institutionTabSheet = institutionTabSheet;
        this.institution = institution;

        closingTimeService = manager.getClosingTimeService();
        institutionService = manager.getInstitutionService();

        filterDataProvider = new FilterDataProvider<>(closingTimeService);

        tab = new Tab(institution.getName());

        addButton = new Button("Schließzeitraum hinzufügen");
        addButton.getStyle()
                .setFontSize("var(--lumo-font-size-xl)")
                .setBorder("1px solid grey");

        addButton.addClickListener(event ->
        {
            new ClosingTimeDialog(manager, this, new InstitutionClosingTime(), DialogState.NEW);
        });
        buttonLayout = new HorizontalLayout(addButton);
        buttonLayout.getStyle()
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setAlignItems(Style.AlignItems.CENTER);

        grid = new AutofilterGrid<>(filterDataProvider);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setSizeFull();

        grid.addFilter(new Filter<InstitutionClosingTime>("institution", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(path.get("id"));
            inClause.value(institution.getId());

            return inClause;
        }).setIgnoreFilterInput(true));

        grid.addComponentColumn("Löschen", "4em", institutionClosingTime ->
        {
            Button button = new Button(LineAwesomeIcon.TRASH_SOLID.create());
            button.setWidth("4em");

            button.addClickListener(event ->
            {
                this.deleteItem(institutionClosingTime);
            });

            return button;
        });

//        grid.addFilter(new Filter<InstitutionClosingTime>("startDate", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            criteriaQuery.orderBy(criteriaBuilder.asc(path));
//            return criteriaBuilder.conjunction();
//        }).setIgnoreFilterInput(true));

//        grid.addFilter(new Filter<InstitutionClosingTime>("endDate", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
//        {
//            criteriaQuery.orderBy(criteriaBuilder.asc(path));
//            return criteriaBuilder.conjunction();
//        }).setIgnoreFilterInput(true));

        grid.addColumn("header", "Beschreibung", InstitutionClosingTime::getHeader, s -> "%" + s + "%", false);

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
            return (institutionClosingTime.getEndDate() == null) ?
                    institutionClosingTime.getStartDate().format(formatter) :
                    institutionClosingTime.getEndDate().format(formatter);

        }, (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Expression<?> date = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.isNull(path), root.get("startDate").as(LocalDate.class))
                    .otherwise(path);

            return criteriaBuilder.like(criteriaBuilder.function("DATE_FORMAT", String.class, date, criteriaBuilder.literal("%d.%m.%Y")),
                    "%" + filterInput + "%"
            );
        });

        grid.AutofilterGridContextMenu()
                .enableGridContextMenu()
                        .enableAddItem("Neuer Schließzeitraum", event ->
                        {
                            new ClosingTimeDialog(manager, this, new InstitutionClosingTime(), DialogState.NEW);

                        }).enableDeleteItem("Löschen", event ->
                        {
                            deleteItem(event.getItem().get());
                        });

        grid.addItemDoubleClickListener(event ->
        {
            if(Objects.requireNonNullElse(event.getColumn().getHeaderText(), "").equals("Löschen")) return;
            new ClosingTimeDialog(manager, this, event.getItem(), DialogState.EDIT);
        });

        this.add(buttonLayout, grid);

        this.setHeightFull();
        this.setWidthFull();
    }

    private void deleteItem(InstitutionClosingTime institutionClosingTime)
    {
        this.closingTimeService.delete(institutionClosingTime);
        filterDataProvider.refreshAll();
    }
}

package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.Day;
import de.bauersoft.data.entities.Menu;
import de.bauersoft.data.entities.Week;
import de.bauersoft.data.filters.SerializableFilter;
import de.bauersoft.services.DayService;
import de.bauersoft.services.MenuService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Stream;

@Service
public class OffersDataProvider implements DataProvider<Week, Void> {

    private List<SerializableFilter<Day,?>> filter;
    private final List<DataProviderListener<Week>> listeners = new ArrayList<>();
    private final DayService dayService;
    private final MenuService menuService;
    private int size = 0 ;
    private List<Week> weeks = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;

    public OffersDataProvider(DayService dayService, MenuService menuService) {this.dayService = dayService;
        this.menuService = menuService;
    }


    @Override
    public boolean isInMemory() {
        return true;
    }

    // gegen datenbank prüfen
    @Override
    public int size(Query<Week, Void> query) {
        return this.size; //  this.service.count(filter);
    }

    // gegen datenbank prüfen
    // query.XXX müssen einmal aufgerufen werden
    @Override
    public Stream<Week> fetch(Query<Week, Void> query) {
        return  this.weeks.stream().skip(query.getOffset()).limit(query.getLimit());//this.service.fetchAll(filter, query.getSortOrders()).stream().skip(query.getOffset()).limit(query.getLimit());
    }

    // nicht nötig
    @Override
    public void refreshItem(Week item) {

    }

    @Override
    public void refreshAll() {
        if (startDate == null || endDate == null) { // Null guard
            return;
        }
        weeks.forEach(Week::clearDays);
        this.dayService.fetchAll(startDate, endDate).stream().sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate())).forEach(day->
                weeks.stream().filter(w-> w.getKw() == day.getWeek().getKw()).findFirst().ifPresent(week-> {
                    week.addDay(day);
                    List<Menu> menus =this.dayService.getMenusByDayId(day.getId());
                    day.setMenus(menus);
                })
        );

        DataChangeEvent<Week> event = new DataChangeEvent<>(this);
        listeners.forEach(listener-> listener.onDataChange(event));
    }


    @Override
    public Registration addDataProviderListener(DataProviderListener<Week> listener) {
        return Registration.addAndRemove(listeners, listener);
    }

    public void setDateRange(LocalDate startDate, LocalDate endDate){
        Objects.requireNonNull(startDate);
        Objects.requireNonNull(endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        if(endDate.isAfter(startDate)) {
            TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

            int endWeek = endDate.get(woy);
            int startWeek = startDate.get(woy);
            // this.size = (endWeek - startWeek) + 1;
            this.size = (int) ChronoUnit.WEEKS.between(startDate, endDate) + 1;


            int maxWeeks =(DayOfWeek.THURSDAY.equals(LocalDate.of(startDate.getYear(),1,1).getDayOfWeek()) ||
                            DayOfWeek.THURSDAY.equals(LocalDate.of(startDate.getYear(),12,31).getDayOfWeek())? 53 : 52) ;
            if (this.size < 0) {
                this.size = maxWeeks+ this.size;
            }

            weeks = new ArrayList<>(this.size);
            for (int index = 0; index < size; index++) {
                Week newWeek = new Week(startDate.plusWeeks(index));
                if(( index + startWeek) > maxWeeks ) {
                    newWeek.setKw( (index + startWeek) - maxWeeks);
                }
                else {
                    newWeek.setKw(index + startWeek);
                }

                weeks.add(newWeek);
            }
        }
        refreshAll();
    }

    public void deleteMenuFromDay(Long dayId, Long menuId) {
        // Hole den Day anhand der dayId
        Optional<Day> optionalDay = this.dayService.findById(dayId);
        if (optionalDay.isEmpty()) {
            throw new IllegalArgumentException("Day with ID " + dayId + " not found");
        }
        Day day = optionalDay.get();

        // Finde das Menu in der Liste des Day
        Menu menuToDelete = day.getMenus().stream()
                .filter(menu -> menu.getId().equals(menuId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Menu with ID " + menuId + " not found in Day with ID " + dayId));

        // Entferne das Menu aus der Liste des Day
        day.getMenus().remove(menuToDelete);

        // Änderungen persistieren
        this.dayService.saveDay(day);
        this.menuService.saveMenu(menuToDelete);

        if(day.getMenus().isEmpty()){
            dayService.deleteById(dayId);
        }

        refreshAll();
    }
}

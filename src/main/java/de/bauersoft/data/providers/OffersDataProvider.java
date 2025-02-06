package de.bauersoft.data.providers;

import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.shared.Registration;
import de.bauersoft.data.entities.menu.Menu;
import de.bauersoft.data.entities.offer.Offer;
import de.bauersoft.services.MenuService;
import de.bauersoft.services.offer.OfferService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Stream;

import static org.atmosphere.annotation.AnnotationUtil.logger;

@Service
public class OffersDataProvider implements DataProvider<LocalDate, Void> {

    private final OfferService offerService;
    private final MenuService menuService;
    private int size = 0;
    private List<LocalDate> weeks = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private Long fieldId;
    private final List<DataProviderListener<LocalDate>> listeners = new ArrayList<>();

    public OffersDataProvider(OfferService offerService, MenuService menuService) {
        this.offerService = offerService;
        this.menuService = menuService;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query<LocalDate, Void> query) {
        return this.size;
    }

    @Override
    public Stream<LocalDate> fetch(Query<LocalDate, Void> query) {
        return this.weeks.stream().skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public void refreshItem(LocalDate item) {
        // No implementation needed
    }

    @Override
    public void refreshAll() {
        if (startDate == null || endDate == null || fieldId == null) {
            return;
        }

        List<Offer> offers = this.offerService.getOffersBetweenDates(startDate, endDate, fieldId);

        DataChangeEvent<LocalDate> event = new DataChangeEvent<>(this);
        listeners.forEach(listener -> listener.onDataChange(event));
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<LocalDate> listener) {
        return Registration.addAndRemove(listeners, listener);
    }

    public void setDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("setDateRange aufgerufen mit: {} - {}", startDate, endDate);
        Objects.requireNonNull(startDate);
        Objects.requireNonNull(endDate);
        this.startDate = startDate;
        this.endDate = endDate;
        if (endDate.isAfter(startDate)) {
            TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
            int startWeek = startDate.get(woy);
            this.size = (int) ChronoUnit.WEEKS.between(startDate, endDate) + 1;

            int maxWeeks = (DayOfWeek.THURSDAY.equals(LocalDate.of(startDate.getYear(), 1, 1).getDayOfWeek()) ||
                    DayOfWeek.THURSDAY.equals(LocalDate.of(startDate.getYear(), 12, 31).getDayOfWeek()) ? 53 : 52);
            if (this.size < 0) {
                this.size = maxWeeks + this.size;
            }

            weeks = new ArrayList<>(this.size);
            for (int index = 0; index < size; index++) {
                LocalDate newWeek = startDate.plusWeeks(index);
                weeks.add(newWeek);
            }
        }
        refreshAll();
    }

    public void deleteMenuFromOffer(Long offerId, Long menuId) {
        Optional<Offer> optionalOffer = this.offerService.get(offerId);
        if (optionalOffer.isEmpty()) {
            throw new IllegalArgumentException("Offer with ID " + offerId + " not found");
        }
        Offer offer = optionalOffer.get();

        Menu menuToDelete = offer.getMenus().stream()
                .filter(menu -> menu.getId().equals(menuId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Menu with ID " + menuId + " not found in Offer with ID " + offerId));

        offer.getMenus().remove(menuToDelete);
        this.offerService.update(offer);
        this.menuService.update(menuToDelete);

        if (offer.getMenus().isEmpty()) {
            offerService.delete(offerId);
        }

        refreshAll();
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
        refreshAll();
    }
}
package de.bauersoft.views.tourCreation.tourInstitution;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerVariant;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.bauersoft.components.autofilter.Filter;
import de.bauersoft.components.autofilter.FilterDataProvider;
import de.bauersoft.components.autofilter.grid.AutofilterGrid;
import de.bauersoft.components.autofilter.grid.SortType;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tour.tour.Tour;
import de.bauersoft.data.entities.tour.tour.TourInstitution;
import de.bauersoft.data.entities.tour.tour.TourInstitutionKey;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tour.TourInstitutionService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.swing.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Getter
public class TourInstitutionComponent extends HorizontalLayout
{
    private final Tour item;

    private final InstitutionService institutionService;
    private final TourInstitutionService tourInstitutionService;
    private final TourInstitutionMapContainer mapContainer;

    private final FilterDataProvider<Institution, Long> institutionDataProvider;
    private Filter<Institution> tourInstitutionFilter;
    private final Filter<Institution> institutionFilter;
    private final Filter<Institution> institutionNameFilter;

    private final TourInstitutionGrid tourInstitutionGrid;
    private final InstitutionGrid institutionGrid;

    public TourInstitutionComponent(Tour item, InstitutionService institutionService, TourInstitutionService tourInstitutionService)
    {
        this.item = item;
        this.institutionService = institutionService;
        this.tourInstitutionService = tourInstitutionService;

        mapContainer = new TourInstitutionMapContainer();
        for(TourInstitution tourInstitution : item.getInstitutions())
        {
            ((TourInstitutionContainer) mapContainer.addContainer(tourInstitution.getInstitution(), tourInstitution, ContainerState.SHOW))
                    .setGridItem(true);
        }

        institutionDataProvider = new FilterDataProvider<>(institutionService);

        tourInstitutionFilter = buildTourInstitutionsFilter(item.isHolidayMode());

        institutionFilter = new Filter<Institution>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            List<Institution> doNotShow = new ArrayList<>(
                    getItems().get(true)
                            .stream()
                            .map(container -> container.getEntity().getInstitution())
                            .collect(Collectors.toList())
            );

            if(!doNotShow.isEmpty())
            {
                CriteriaBuilder.In<Long> inClause = criteriaBuilder.in(root.get("id"));
                for(Institution institution : doNotShow)
                    inClause.value(institution.getId());

                return criteriaBuilder.not(inClause);
            }

            return criteriaBuilder.conjunction();
        }).setIgnoreFilterInput(true);

        institutionNameFilter = new Filter<>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + filterInput.toLowerCase() + "%");
        });

        institutionDataProvider.addFilters(tourInstitutionFilter, institutionFilter, institutionNameFilter);

        tourInstitutionGrid = new TourInstitutionGrid();
        institutionGrid = new InstitutionGrid();

        updateView();

        this.add(tourInstitutionGrid, institutionGrid);
        this.setHeight("25rem");
        this.getStyle()
                .setMarginTop("var(--lumo-space-m)");
    }

    private class TourInstitutionGrid extends Grid<TourInstitutionContainer>
    {
        private final TextField institutionFilter;
        private final TextField timeFilter;

        public TourInstitutionGrid()
        {
            DropTarget<Grid<TourInstitutionContainer>> dropTarget = DropTarget.create(this);
            dropTarget.addDropListener(event ->
            {
                event.getDragData().ifPresent(o ->
                {
                    if(!(o instanceof Institution institution)) return;

                    TourInstitutionContainer container = (TourInstitutionContainer) mapContainer.addIfAbsent(institution, () ->
                    {
                        TourInstitution tourInstitution = new TourInstitution();
                        tourInstitution.setId(new TourInstitutionKey(null, institution.getId()));
                        tourInstitution.setTour(item);
                        tourInstitution.setInstitution(institution);

                        return tourInstitution;
                    }, ContainerState.NEW);

                    container.setTempState(ContainerState.UPDATE);
                    container.setGridItem(true);

                    updateView();
                });
            });

            this.addComponentColumn(container ->
            {
                SvgIcon trash = LineAwesomeIcon.TRASH_SOLID.create();

                trash.addClickListener(event ->
                {
                    if(container.getState() == ContainerState.NEW)
                    {
                        container.setTempState(ContainerState.HIDE);
                    }else container.setTempState(ContainerState.DELETE);

                    container.setGridItem(false);
                    updateView();
                });

                return trash;
            }).setHeader(LineAwesomeIcon.TRASH_SOLID.create())
                    .setWidth("3em").setAutoWidth(false).setFlexGrow(0);


            institutionFilter = new TextField();
            institutionFilter.setWidth("99%");
            institutionFilter.setPlaceholder("Institution...");
            institutionFilter.setValueChangeMode(ValueChangeMode.EAGER);

            this.addColumn(container ->
            {
                return container.getEntity().getInstitution().getName();
            }).setTooltipGenerator(container ->
            {
                return container.getEntity().getInstitution().getName();
            }).setHeader(institutionFilter)
                    .setKey("institution")
                    .setSortable(true)
                    .setComparator(Comparator.comparing(container -> container.getEntity().getInstitution().getName()));

            timeFilter = new TextField();
            timeFilter.setWidth("99%");
            timeFilter.setPlaceholder("vsl. ankunft");
            timeFilter.setValueChangeMode(ValueChangeMode.EAGER);

            this.addColumn(new ComponentRenderer<>(container ->
            {
                TimePicker timePicker = new TimePicker();
                timePicker.setWidth("99%");
                timePicker.addThemeVariants(TimePickerVariant.LUMO_ALIGN_CENTER);
                timePicker.setValue(container.getTempExpectedArrivalTime());

                timePicker.addValueChangeListener(event ->
                {
                    container.setTempExpectedArrivalTime(event.getValue());
                    container.setTempState(ContainerState.UPDATE);

                    updateFilters();
                });

                return timePicker;

            })).setHeader(timeFilter)
                    .setSortable(true)
                    .setComparator(Comparator.comparing(TourInstitutionContainer::getTempExpectedArrivalTime).reversed());



            institutionFilter.addValueChangeListener(event ->
            {
                this.setItems(
                        getItems().get(true)
                                .stream()
                                .filter(container ->
                                {
                                    return container.getTempExpectedArrivalTime().toString().contains(timeFilter.getValue().toLowerCase())
                                            && container.getEntity().getInstitution().getName().toLowerCase().startsWith(event.getValue().toLowerCase());

                                })
                                .collect(Collectors.toList())
                );
            });

            timeFilter.addValueChangeListener(event ->
            {
                this.setItems(
                        getItems().get(true)
                                .stream()
                                .filter(container ->
                                {
                                    return container.getTempExpectedArrivalTime().toString().contains(event.getValue().toLowerCase())
                                            && container.getEntity().getInstitution().getName().toLowerCase().startsWith(institutionFilter.getValue().toLowerCase());

                                })
                                .collect(Collectors.toList())
                );
            });


            this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            this.setWidth("99%");
            this.setHeightFull();
        }

        public TourInstitutionGrid updateFilters()
        {
            this.setItems(
                    getItems().get(true)
                            .stream()
                            .filter(container ->
                            {
                                return container.getTempExpectedArrivalTime().toString().contains(timeFilter.getValue().toLowerCase())
                                        && container.getEntity().getInstitution().getName().toLowerCase().startsWith(institutionFilter.getValue().toLowerCase());
                            })
                            .collect(Collectors.toList())
            );

            return this;
        }
    }

    private class InstitutionGrid extends Grid<Institution>
    {
        private SortOrder sortOrder;

        private final HorizontalLayout header;
        private final TextField filterHeader;
        private final Button sortButton;

        public InstitutionGrid()
        {
            sortOrder = SortOrder.UNSORTED;

            header = new HorizontalLayout();

            filterHeader = new TextField();
            filterHeader.setPlaceholder("Suchen...");
            filterHeader.setValueChangeMode(ValueChangeMode.LAZY);
            filterHeader.getStyle().setWidth("99%");

            filterHeader.addValueChangeListener(event ->
            {
                institutionNameFilter.setFilterInput(event.getValue());
                institutionDataProvider.refreshAll();
            });

            sortButton = new Button(LineAwesomeIcon.SORT_SOLID.create());
            sortButton.addClickListener(event ->
            {
                sortOrder = nextSortOrder(sortOrder);
                sortButton.setIcon(SortType.ALPHA.get(sortOrder).create());

                institutionDataProvider.applyFilters("name", sortOrder);
            });

            sortButton.click();

            header.add(filterHeader, sortButton);

            this.setDataProvider(institutionDataProvider.getFilterDataProvider());
            this.addColumn(new ComponentRenderer<>(institution ->
            {
                TextField showField = new TextField();
                showField.setWidth("99%");
                showField.setValue(institution.getName());
                showField.setReadOnly(true);

                DragSource dragSource = DragSource.create(showField);
                dragSource.addDragStartListener(event ->
                {
                    dragSource.setDragData(institution);
                });

                return showField;
            })).setHeader(header);

            this.setWidth("99%");
            this.setHeightFull();
        }

        public static SortOrder nextSortOrder(SortOrder currentSortOrder)
        {
            switch(currentSortOrder)
            {
                case ASCENDING:
                    return SortOrder.DESCENDING;

                case DESCENDING:
                    return SortOrder.UNSORTED;

                case UNSORTED:
                    return SortOrder.ASCENDING;
            }

            return SortOrder.UNSORTED;
        }
    }

    public Map<Boolean, List<TourInstitutionContainer>> getItems()
    {
        return mapContainer.getContainers()
                .stream()
                .map(container -> (TourInstitutionContainer) container)
                .collect(Collectors.partitioningBy(TourInstitutionContainer::isGridItem, Collectors.collectingAndThen(Collectors.toList(), list ->
                {
                    list.sort(Comparator.comparing(TourInstitutionContainer::getTempExpectedArrivalTime).reversed());
                    return list;
                })));
    }

    public TourInstitutionComponent updateView()
    {
        Map<Boolean, List<TourInstitutionContainer>> items = getItems();

        tourInstitutionGrid.setItems(
                items.get(true)
                        .stream()
                        .sorted(Comparator.comparing(TourInstitutionContainer::getTempExpectedArrivalTime))
                        .collect(Collectors.toList())
        );

        tourInstitutionGrid.updateFilters();
        institutionDataProvider.refreshAll();

        return this;
    }

    public TourInstitutionComponent setHolidayMode(boolean holidayMode)
    {
        if(tourInstitutionGrid != null)
            tourInstitutionGrid.setItems(new ArrayList<>());

        if(mapContainer != null)
            mapContainer.clear();

        if(tourInstitutionFilter == null)
        {
            tourInstitutionFilter = buildTourInstitutionsFilter(holidayMode);

        }else tourInstitutionFilter.setFilterFunction(buildTourInstitutionsFilter(holidayMode).getFilterFunction());

        institutionDataProvider.refreshAll();

        return this;
    }

    private Filter<Institution> buildTourInstitutionsFilter(boolean holidayMode)
    {
        return new Filter<Institution>("name", (root, path, criteriaQuery, criteriaBuilder, parent, filterInput) ->
        {
            Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
            Root<TourInstitution> tourInstitution = subquery.from(TourInstitution.class);
            subquery.select(tourInstitution.get("institution").get("id"))
                    .where(criteriaBuilder.equal(tourInstitution.get("tour").get("holidayMode"), holidayMode));

            return criteriaBuilder.not(root.get("id").in(subquery));
        }).setIgnoreFilterInput(true);
    }

//    public TourInstitutionComponent setAvailableInstitutions(List<Institution> institutions)
//    {
//        mapContainer.getContainers()
//                .stream()
//                .map(container -> (TourInstitutionContainer) container)
//                .filter(tourInstitutionContainer -> tourInstitutionContainer.getState() != ContainerState.SHOW)
//                .forEach(tourInstitutionContainer ->
//                {
//                    mapContainer.removeContainer(tourInstitutionContainer.getEntity().getInstitution());
//                });
//
//        for(Institution institution : institutions)
//        {
//            mapContainer.addIfAbsent(institution, () ->
//            {
//                TourInstitution tourInstitution = new TourInstitution();
//                tourInstitution.setId(new TourInstitutionKey(null, institution.getId()));
//                tourInstitution.setTour(item);
//                tourInstitution.setInstitution(institution);
//
//                return tourInstitution;
//
//            }, ContainerState.NEW);
//        }
//
//        updateView();
//
//        return this;
//    }
}

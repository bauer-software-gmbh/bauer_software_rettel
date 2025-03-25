package de.bauersoft.views.tour.tourInstitution;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerVariant;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.tourPlanning.tour.Tour;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitution;
import de.bauersoft.data.entities.tourPlanning.tour.TourInstitutionKey;
import de.bauersoft.services.InstitutionService;
import de.bauersoft.services.tourPlanning.TourInstitutionService;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class TourInstitutionComponent extends HorizontalLayout
{
    private final Tour item;

    private final InstitutionService institutionService;
    private final TourInstitutionService tourInstitutionService;
    private final TourInstitutionMapContainer mapContainer;

    private final InstitutionGrid institutionGrid;
    private DropTarget<Grid<TourInstitutionContainer>> dropTarget;
    private final InstitutionList institutionList;

    public TourInstitutionComponent(Tour item, InstitutionService institutionService, TourInstitutionService tourInstitutionService)
    {
        this.item = item;
        this.institutionService = institutionService;
        this.tourInstitutionService = tourInstitutionService;

        mapContainer = new TourInstitutionMapContainer();
        for(TourInstitution tourInstitution : tourInstitutionService.findAllByTour_Id(item.getId()))
        {
            ((TourInstitutionContainer) mapContainer.addContainer(tourInstitution.getInstitution(), tourInstitution, ContainerState.SHOW))
                    .setGridItem(true);
        }

        for(Institution institution : institutionService.findAll())
        {
            mapContainer.addIfAbsent(institution, () ->
            {
                TourInstitution tourInstitution = new TourInstitution();
                tourInstitution.setId(new TourInstitutionKey(null, institution.getId()));
                tourInstitution.setTour(item);
                tourInstitution.setInstitution(institution);

                return tourInstitution;

            }, ContainerState.NEW);
        }

        institutionGrid = new InstitutionGrid();
        institutionList = new InstitutionList();

        updateView();

        this.add(institutionGrid, institutionList);
        this.setHeightFull();
        this.getStyle()
                .setMarginTop("var(--lumo-space-m)");
    }

    private class InstitutionGrid extends Grid<TourInstitutionContainer>
    {
        public InstitutionGrid()
        {
            this.addComponentColumn(container ->
            {
                SvgIcon trash = LineAwesomeIcon.TRASH_SOLID.create();

                trash.addClickListener(event ->
                {
                    if(container.getTempState() == ContainerState.NEW)
                    {
                        container.setTempState(ContainerState.HIDE);
                    }else container.setTempState(ContainerState.DELETE);

                    container.setGridItem(false);
                    updateView();
                });

                return trash;
            }).setHeader(LineAwesomeIcon.TRASH_SOLID.create()).setWidth("3em").setAutoWidth(false).setFlexGrow(0);

            this.addColumn(container ->
            {
                return container.getEntity().getInstitution().getName();
            }).setTooltipGenerator(container ->
            {
                return container.getEntity().getInstitution().getName();
            }).setHeader("Institution");

            this.addComponentColumn(container ->
            {
                TimePicker timePicker = new TimePicker();
                timePicker.setWidth("5.5em");
                timePicker.addThemeVariants(TimePickerVariant.LUMO_ALIGN_CENTER);
                timePicker.setValue(container.getTempExpectedArrivalTime());

                timePicker.addValueChangeListener(event ->
                {
                    container.setTempExpectedArrivalTime(event.getValue());
                    container.setTempState(ContainerState.UPDATE);

                    updateView();
                });

                return timePicker;

            }).setHeader("vsl. ankunft");

            dropTarget = DropTarget.create(this);
            dropTarget.addDropListener(event ->
            {
                event.getDragData().ifPresent(o ->
                {
                    if(!(o instanceof TourInstitutionContainer container)) return;

                    container.setTempExpectedArrivalTime(LocalTime.of(0, 0));
                    container.setTempState(ContainerState.UPDATE);
                    container.setGridItem(true);

                    updateView();
                });
            });

            this.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            this.setWidth("99%");
            this.setHeightFull();
        }
    }

    @Getter
    private class InstitutionList extends VerticalLayout
    {
        private final VirtualList<TourInstitutionContainer> virtualList;
        private final TextField filterField;

        public InstitutionList()
        {
            virtualList = new VirtualList<>();
            virtualList.setRenderer(new ComponentRenderer<>(container ->
            {
                TextField showField = new TextField();
                showField.setWidth("99%");
                showField.setValue(container.getEntity().getInstitution().getName());
                showField.setReadOnly(true);

                DragSource dragSource = DragSource.create(showField);
                dragSource.addDragStartListener(event ->
                {
                    dragSource.setDragData(container);
                });

                return showField;
            }));

            filterField = new TextField();
            filterField.setPlaceholder("Suchen...");
            filterField.setValueChangeMode(ValueChangeMode.EAGER);
            filterField.getStyle()
                    .setWidth("99%")
                    .setPaddingTop("0px");

            filterField.addValueChangeListener(event ->
            {
                virtualList.setItems(
                        getItems().get(false)
                                .stream()
                                .filter(container -> container.getEntity().getInstitution().getName().toLowerCase().startsWith(event.getValue().toLowerCase()))
                                .collect(Collectors.toList())
                );
            });

            this.add(filterField, virtualList);
            this.setWidthFull();
            this.setHeightFull();
            this.setPadding(false);
        }

        public InstitutionList updateFilter()
        {
            String value = filterField.getValue();
            filterField.setValue("");
            filterField.setValue(Objects.requireNonNullElse(value, ""));

            return this;
        }
    }

    public Map<Boolean, List<TourInstitutionContainer>> getItems()
    {
        return mapContainer.getContainers()
                .stream()
                .map(container -> (TourInstitutionContainer) container)
                .collect(Collectors.partitioningBy(TourInstitutionContainer::isGridItem));
    }

    public TourInstitutionComponent updateView()
    {
        Map<Boolean, List<TourInstitutionContainer>> items = getItems();

        institutionGrid.setItems(
                items.get(true)
                        .stream()
                        .sorted(Comparator.comparing(TourInstitutionContainer::getTempExpectedArrivalTime))
                        .collect(Collectors.toList())
        );
        institutionList.getVirtualList().setItems(items.get(false));

        institutionList.updateFilter();

        return this;
    }
}

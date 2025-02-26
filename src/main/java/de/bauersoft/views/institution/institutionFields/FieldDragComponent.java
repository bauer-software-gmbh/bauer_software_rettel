package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institutionClosingTime.InstitutionClosingTime;
import de.bauersoft.data.entities.institutionField.*;
import de.bauersoft.data.entities.institutionFieldAllergen.InstitutionAllergen;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldPattern.InstitutionPattern;
import de.bauersoft.services.*;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenMapContainer;
import de.bauersoft.views.institution.institutionFields.components.closingTime.ClosingTimesContainer;
import de.bauersoft.views.institution.institutionFields.components.closingTime.ClosingTimesMapContainer;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierMapContainer;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternMapContainer;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class FieldDragComponent extends FlexLayout
{
    private final InstitutionDialog institutionDialog;
    private final Institution institution;

    private final Map<Field, PatternMapContainer> patternMapContainerMap;
    private final Map<Field, MultiplierMapContainer> multiplierMapContainerMap;
    private final Map<Field, AllergenMapContainer> allergenMapContainerMap;
    private final Map<Field, ClosingTimesMapContainer> closingTimesMapContainerMap;

    private final List<InstitutionField> gridItems;
    private final Grid<InstitutionField> institutionFieldsGrid;

    private final List<Field> fieldPool;
    private final VirtualList<Field> fieldVirtualList;

    public FieldDragComponent(InstitutionDialog institutionDialog)
    {
        this.institutionDialog = institutionDialog;
        this.institution = institutionDialog.getItem();

        patternMapContainerMap = new HashMap<>();
        multiplierMapContainerMap = new HashMap<>();
        allergenMapContainerMap = new HashMap<>();
        closingTimesMapContainerMap = new HashMap<>();

        gridItems = new ArrayList<>();
        gridItems.addAll(institution.getInstitutionFields());

        institutionFieldsGrid  = new Grid<>(InstitutionField.class, false);
        institutionFieldsGrid.setSizeFull();
        institutionFieldsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        fieldPool = new ArrayList<>();

        fieldVirtualList = new VirtualList<>();
        fieldVirtualList.setSizeFull();

        institutionFieldsGrid.addColumn(institutionField ->
        {
            return institutionField.getField().getName();

        }).setHeader("Einrichtungsart");

        institutionFieldsGrid.addComponentColumn(institutionField ->
        {
            SvgIcon trashCan = LineAwesomeIcon.TRASH_SOLID.create();

            trashCan.addClickListener(event ->
            {
                gridItems.remove(institutionField);
                fieldPool.add(institutionField.getField());
                updateView();
            });

            return trashCan;
        });

        institutionFieldsGrid.addItemDoubleClickListener(event ->
        {
            if(event.getItem() == null) return;

            PatternMapContainer patternListContainer = patternMapContainerMap
                    .computeIfAbsent(event.getItem().getField(), field ->
            {
                PatternMapContainer container = new PatternMapContainer();

                for(InstitutionPattern institutionPattern : event.getItem().getInstitutionPatterns())
                    container.addContainer(institutionPattern.getPattern(), institutionPattern).setState(ContainerState.UPDATE);

                return container;
            });

            MultiplierMapContainer multiplierListContainer = multiplierMapContainerMap
                    .computeIfAbsent(event.getItem().getField(), field ->
            {
                MultiplierMapContainer container = new MultiplierMapContainer();

                for(InstitutionMultiplier institutionMultiplier : event.getItem().getInstitutionMultipliers())
                    container.addContainer(institutionMultiplier.getCourse(), institutionMultiplier).setState(ContainerState.UPDATE);

                return container;
            });

            AllergenMapContainer allergenListContainer = allergenMapContainerMap.computeIfAbsent(event.getItem().getField(), field ->
            {
                AllergenMapContainer container = new AllergenMapContainer();

                for(InstitutionAllergen institutionAllergen : event.getItem().getInstitutionAllergens())
                    container.addContainer(institutionAllergen.getAllergen(), institutionAllergen).setState(ContainerState.UPDATE);

                return container;
            });

            ClosingTimesMapContainer closingTimesMapContainer = closingTimesMapContainerMap.computeIfAbsent(event.getItem().getField(), field ->
            {
                ClosingTimesMapContainer container = new ClosingTimesMapContainer();

                for(InstitutionClosingTime institutionClosingTimes : event.getItem().getInstitutionClosingTimes())
                {
                    int key = container.getNextKey();
                    ((ClosingTimesContainer) container.addContainer(key, institutionClosingTimes, ContainerState.UPDATE)).setKey(key);
                }

                return container;
            });

            InstitutionFieldDialog institutionFieldDialog = new InstitutionFieldDialog(institutionDialog, this, event.getItem(), patternListContainer, multiplierListContainer, allergenListContainer, closingTimesMapContainer);
            institutionFieldDialog.open();
        });

        DropTarget.create(institutionFieldsGrid).addDropListener(event ->
        {
            event.getDragData().ifPresent(o ->
            {
                if(!(o instanceof Field field)) return;

                InstitutionField institutionField = new InstitutionField();
                institutionField.setInstitution(institution);
                institutionField.setField(field);

                gridItems.add(institutionField);
                fieldPool.remove(field);

                updateView();
            });
        });


        fieldVirtualList.setRenderer(new ComponentRenderer<>(field ->
        {
            Div div = new Div();
            div.setText(field.getName());
            div.getStyle()
                    .setTextAlign(Style.TextAlign.CENTER)
                    .setMarginLeft("3px")
                    .setMarginRight("3px")
                    .set("padding", "var(--lumo-space-s)")
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "var(--lumo-border-radius-s)");

            DragSource.create(div).addDragStartListener(event ->
            {
                event.setDragData(field);
            });

            return div;
        }));

        this.add(institutionFieldsGrid, fieldVirtualList);
        this.setWidthFull();
        this.setMaxHeight("25em");
        this.setHeight("25em");
        this.setWidthFull();
    }

    public void updateView()
    {
        fieldVirtualList.setItems(fieldPool);
        institutionFieldsGrid.setItems(gridItems);
    }

    public void setFieldPool(List<Field> fieldPool)
    {
        this.fieldPool.clear();
        this.fieldPool.addAll(fieldPool);
        this.fieldPool.removeAll(gridItems.stream().map(InstitutionField::getField).toList());
    }

    public void updateInstitutionFields(List<InstitutionField> oldInstitutionFields)
    {
        InstitutionFieldsService institutionFieldsService = institutionDialog.getInstitutionFieldsService();
        InstitutionPatternService institutionPatternService = institutionDialog.getInstitutionPatternService();
        InstitutionMultiplierService institutionMultiplierService = institutionDialog.getInstitutionMultiplierService();
        InstitutionAllergenService institutionAllergenService = institutionDialog.getInstitutionAllergenService();
        InstitutionClosingTimeService institutionClosingTimeService = institutionDialog.getInstitutionClosingTimeService();

        if(oldInstitutionFields == null)
            oldInstitutionFields = new ArrayList<>();

        List<InstitutionField> newInstitutionFields = new ArrayList<>(gridItems);

        List<InstitutionField> remove = new ArrayList<>();
        List<InstitutionField> update = new ArrayList<>(newInstitutionFields);

        for(InstitutionField oldInstitutionField : oldInstitutionFields)
        {
            if(!newInstitutionFields.contains(oldInstitutionField))
                remove.add(oldInstitutionField);
        }

        institutionFieldsService.deleteAll(remove);

        institutionFieldsService.updateAll(update);

        for(InstitutionField institutionField : update)
        {
            PatternMapContainer patternListContainer = patternMapContainerMap.get(institutionField.getField());
            if(patternListContainer != null)
            {
                patternListContainer.evaluate(container ->
                {
                    container.getEntity().getId().setInstitutionFieldId(institutionField.getId());
                });

                patternListContainer.run(institutionPatternService);
            }

            MultiplierMapContainer multiplierListContainer = multiplierMapContainerMap.get(institutionField.getField());
            if(multiplierListContainer != null)
            {
                multiplierListContainer.evaluate(container ->
                {
                    container.getEntity().getId().setInstitutionFieldId(institutionField.getId());
                });

                multiplierListContainer.run(institutionMultiplierService);
            }

            AllergenMapContainer allergenMapContainer = allergenMapContainerMap.get(institutionField.getField());
            if(allergenMapContainer != null)
            {
                allergenMapContainer.evaluate(container ->
                {
                    container.getEntity().getId().setInstitutionFieldId(institutionField.getId());
                });

                allergenMapContainer.run(institutionAllergenService);
            }

            ClosingTimesMapContainer closingTimesMapContainer = closingTimesMapContainerMap.get(institutionField.getField());
            if(closingTimesMapContainer != null)
            {
                closingTimesMapContainer.run(institutionClosingTimeService);
            }
        }

    }

    public void loadTemporaries()
    {
        for(PatternMapContainer patternListContainer : patternMapContainerMap.values())
            patternListContainer.loadTemporaries();

        for(MultiplierMapContainer multiplierListContainer : multiplierMapContainerMap.values())
            multiplierListContainer.loadTemporaries();

        for(AllergenMapContainer allergenListContainer : allergenMapContainerMap.values())
            allergenListContainer.loadTemporaries();
    }
}

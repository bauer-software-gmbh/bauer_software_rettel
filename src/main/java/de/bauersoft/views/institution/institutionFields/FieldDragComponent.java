package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.*;
import de.bauersoft.data.entities.pattern.Pattern;
import de.bauersoft.services.InstitutionAllergenService;
import de.bauersoft.services.InstitutionFieldsService;
import de.bauersoft.services.InstitutionMultiplierService;
import de.bauersoft.services.InstitutionPatternService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.container.StackContainer;
import de.bauersoft.views.institution.institutionFields.components.InstitutionFieldContainer;
import de.bauersoft.views.institution.institutionFields.components.allergen.AllergenContainer;
import de.bauersoft.views.institution.institutionFields.components.multiplier.MultiplierContainer;
import de.bauersoft.views.institution.institutionFields.components.pattern.PatternContainer;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;

@Getter
@CssImport(value = "./themes/rettels/components/grid-vertical-centered.css")
public class FieldDragComponent extends FlexLayout
{
    private final InstitutionDialog institutionDialog;
    private final Institution institution;

    private final Map<Field, InstitutionFieldContainer> institutionFieldContanerMap;

    private final List<InstitutionField> gridItems;
    private final Grid<InstitutionField> institutionFieldsGrid;

    private final List<Field> fieldPool;
    private final VirtualList<Field> fieldVirtualList;

    public FieldDragComponent(InstitutionDialog institutionDialog)
    {
        this.institutionDialog = institutionDialog;
        this.institution = institutionDialog.getItem();

        institutionFieldContanerMap = new HashMap<>();

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

            InstitutionFieldContainer institutionFieldContainer = institutionFieldContanerMap
                    .computeIfAbsent(event.getItem().getField(), field ->
                    {
                        Notification.show("defaults loaded");
                        InstitutionFieldContainer container = new InstitutionFieldContainer(event.getItem());

                        for(InstitutionPattern institutionPattern : event.getItem().getInstitutionPatterns())
                        {
                            container.getPatternContainers()
                                    .put(institutionPattern.getPattern(), new PatternContainer(institutionPattern.getPattern(), institutionPattern.getAmount()));
                        }

                        for(InstitutionMultiplier institutionMultiplier : event.getItem().getInstitutionMultipliers())
                        {
                            container.getMultiplierContainers()
                                    .put(institutionMultiplier.getCourse(), new MultiplierContainer(institutionMultiplier.getCourse(), institutionMultiplier.getMultiplier()));
                        }

                        for(InstitutionAllergen institutionAllergen : event.getItem().getInstitutionAllergens())
                        {
                            container.getAllergenContainers()
                                    .put(institutionAllergen.getAllergen(), new AllergenContainer(institutionAllergen.getAllergen(), institutionAllergen.getAmount(), false));
                        }

                        return container;
                    });



            InstitutionFieldDialog institutionFieldDialog = new InstitutionFieldDialog(institutionDialog, this, institutionFieldContainer);
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

        institutionPatternService.deleteAll(remove.stream().map(InstitutionField::getInstitutionPatterns).flatMap(Collection::stream).toList());
        institutionMultiplierService.deleteAll(remove.stream().map(InstitutionField::getInstitutionMultipliers).flatMap(Collection::stream).toList());
        institutionAllergenService.deleteAll(remove.stream().map(InstitutionField::getInstitutionAllergens).flatMap(Collection::stream).toList());
        institutionFieldsService.deleteAll(remove);

        institutionFieldsService.updateAll(update);

        institutionAllergenService.deleteAll(update.stream().map(InstitutionField::getInstitutionAllergens).flatMap(Collection::stream).toList());
        for(InstitutionField institutionField : update)
        {
            InstitutionFieldContainer container = institutionFieldContanerMap.get(institutionField.getField());
            if(container == null) continue;

            institutionField.setInstitutionPatterns(container.getInstitutionPatterns());
            institutionField.setInstitutionMultipliers(container.getInstitutionMultipliers());
            institutionField.setInstitutionAllergens(container.getInstitutionAllergens());
        }

        institutionPatternService.updateAll(update.stream().map(InstitutionField::getInstitutionPatterns).flatMap(Collection::stream).toList());
        institutionMultiplierService.updateAll(update.stream().map(InstitutionField::getInstitutionMultipliers).flatMap(Collection::stream).toList());
        institutionAllergenService.updateAll(update.stream().map(InstitutionField::getInstitutionAllergens).flatMap(Collection::stream).toList());
    }
}

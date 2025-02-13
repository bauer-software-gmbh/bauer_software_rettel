package de.bauersoft.views.institution.institutionFields;

import com.vaadin.flow.component.dependency.CssImport;
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
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.services.InstitutionAllergenService;
import de.bauersoft.services.InstitutionFieldsService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.dialogLayer.InstitutionFieldDialog;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;

@Getter
@CssImport(value = "./themes/rettels/components/grid-vertical-centered.css")
public class FieldDragComponent extends FlexLayout
{
    private final InstitutionDialog institutionDialog;
    private final Institution institution;

    private final Map<Field, InstitutionFieldDialog> institutionFieldDialogMap;

    private final List<InstitutionField> gridItems;
    private final Grid<InstitutionField> institutionFieldsGrid;

    private final List<Field> fieldPool;
    private final VirtualList<Field> fieldVirtualList;


    public FieldDragComponent(InstitutionDialog institutionDialog)
    {
        this.institutionDialog = institutionDialog;
        this.institution = institutionDialog.getInstitution();

        institutionFieldDialogMap = new HashMap<>();

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

            InstitutionFieldDialog institutionFieldDialog = institutionFieldDialogMap.get(event.getItem().getField());
            if(institutionFieldDialog == null)
            {
                institutionFieldDialog = new InstitutionFieldDialog(institutionDialog, this, event.getItem());
                institutionFieldDialogMap.put(event.getItem().getField(), institutionFieldDialog);
            }

            institutionFieldDialog.open();

            for(Map.Entry<Field, InstitutionFieldDialog> entry : institutionFieldDialogMap.entrySet())
            {
                System.out.println(entry.getKey().getName());
            }
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

    public void updateInstitutionFields(List<InstitutionField> oldInstitutionFields, List<InstitutionField> newInstitutionFields)
    {
        InstitutionFieldsService institutionFieldsService = institutionDialog.getInstitutionFieldsService();
        InstitutionAllergenService institutionAllergenService = institutionDialog.getInstitutionAllergenService();

        if(oldInstitutionFields == null)
            oldInstitutionFields = new ArrayList<>();

        if(newInstitutionFields == null)
            newInstitutionFields = new ArrayList<>();

        List<InstitutionField> remove = new ArrayList<>();
        List<InstitutionField> update = new ArrayList<>(newInstitutionFields);

        for(InstitutionField oldInstitutionField : oldInstitutionFields)
        {
            if(!newInstitutionFields.contains(oldInstitutionField))
                remove.add(oldInstitutionField);
        }

        institutionAllergenService.deleteAll(remove.stream().map(InstitutionField::getInstitutionAllergens).flatMap(Collection::stream).toList());
        institutionFieldsService.deleteAll(remove);

        institutionFieldsService.updateAll(update);
        institutionAllergenService.updateAll(update.stream().map(InstitutionField::getInstitutionAllergens).flatMap(Collection::stream).toList());
    }
}

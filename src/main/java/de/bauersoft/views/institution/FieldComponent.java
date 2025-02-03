package de.bauersoft.views.institution;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.formulation.FormulationKey;
import de.bauersoft.data.entities.ingredient.Ingredient;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionFieldKey;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@CssImport(value = "./themes/rettels/components/drag-item.css")
public class FieldComponent extends FlexLayout
{
    private final List<Field> fields;
    private final Map<InstitutionField, Integer> institutionFieldsMap;

    private final Grid<InstitutionField> institutionFieldsGrid;
    private final VirtualList<Field> fieldVirtualList;

    private final Map<InstitutionField, IntegerField> numberFieldMap;

    private Runnable onEntriesChange;
    private Consumer<Field> onEntryRemove;

    public FieldComponent()
    {
        fields = new ArrayList<>();
        institutionFieldsMap = new HashMap<>();

        numberFieldMap = new HashMap<>();

        institutionFieldsGrid = new Grid<>(InstitutionField.class, false);
        institutionFieldsGrid.setSizeFull();

        institutionFieldsGrid.getStyle().setMarginRight("5px");

        institutionFieldsGrid.addColumn(institutionField ->
        {
            return institutionField.getField().getName();
        }).setHeader("Name");

        institutionFieldsGrid.addColumn(new ComponentRenderer<IntegerField, InstitutionField>(institutionField ->
        {
            IntegerField numberField = new IntegerField();
            numberField.setMin(0);
            numberField.setMax(Integer.MAX_VALUE);
            numberField.setStep(1);
            numberField.setValue(
                    (institutionFieldsMap.containsKey(institutionField) ? institutionFieldsMap.get(institutionField) : institutionField.getChildCount())
            );

            numberFieldMap.put(institutionField, numberField);

            numberField.addValueChangeListener(event ->
            {
                institutionFieldsMap.put(institutionField, event.getValue());
            });

            return numberField;
        })).setHeader("Anzahl").setWidth("75px");

        institutionFieldsGrid.addComponentColumn(institutionField ->
        {
            SvgIcon trashCan = LineAwesomeIcon.TRASH_SOLID.create();
            trashCan.addClickListener(event ->
            {
                numberFieldMap.remove(institutionField);
                institutionFieldsMap.remove(institutionField);
                fields.add(institutionField.getField());

                if(onEntryRemove != null)
                    onEntryRemove.accept(institutionField.getField());

                if(onEntriesChange != null)
                    onEntriesChange.run();

                updateView();
            });

            return trashCan;
        }).setWidth("25px");

        DropTarget.create(institutionFieldsGrid).addDropListener(event ->
        {
            event.getDragData().ifPresent(object ->
            {
                if(!(object instanceof Field field)) return;

                InstitutionField institutionField = new InstitutionField();
                institutionField.setField(field);
                institutionField.setId(new InstitutionFieldKey());
                institutionField.getId().setFieldId(field.getId());

                institutionFieldsMap.put(institutionField, institutionField.getChildCount());
                fields.remove(field);

                if(onEntriesChange != null)
                    onEntriesChange.run();

                updateView();
            });
        });

        fieldVirtualList = new VirtualList<>();
        fieldVirtualList.setSizeFull();

        fieldVirtualList.setRenderer(new ComponentRenderer<Span, Field>(item ->
        {
            Span span = new Span(item.getName());
            span.addClassName("drag-item");

            DragSource.create(span).addDragStartListener(event ->
            {
                event.setDragData(item);
            });

            return span;
        }));

        this.add(institutionFieldsGrid, fieldVirtualList);
        this.setWidthFull();
    }

    public void setInstitutionFields(List<InstitutionField> institutionFields)
    {
        if(institutionFields == null) return;
        institutionFieldsMap.clear();

        institutionFieldsMap.putAll(institutionFields
                .stream()
                .collect(Collectors.toMap(
                        institutionField -> institutionField,
                        institutionField -> institutionField.getChildCount()
                )));

        if(onEntriesChange != null)
            onEntriesChange.run();
    }

    public void setFields(List<Field> fields)
    {
        if(fields == null) return;
        this.fields.clear();

        this.fields.addAll(fields);

        if(onEntriesChange != null)
            onEntriesChange.run();
    }

    public void updateView()
    {
        institutionFieldsMap.keySet().forEach(institutionField -> fields.remove(institutionField.getField()));

        fieldVirtualList.setItems(fields);
        institutionFieldsGrid.setItems(institutionFieldsMap.keySet());
    }

    public void accept(Institution institution)
    {
        institutionFieldsMap.forEach((institutionField, childCount) ->
        {
            institutionField.setChildCount(childCount);
            institutionField.setInstitution(institution);
            institutionField.getId().setInstitutionId(institution.getId());
            institutionField.getId().setFieldId(institutionField.getField().getId());
        });
    }

    public boolean isValid()
    {
        return numberFieldMap.values().stream().allMatch(numberField -> !numberField.isInvalid());
    }

    public Runnable getOnEntriesChange()
    {
        return onEntriesChange;
    }

    public Runnable setOnEntriesChange(Runnable onEntriesChange)
    {
        this.onEntriesChange = onEntriesChange;
        return onEntriesChange;
    }

    public Consumer<Field> getOnEntryRemove()
    {
        return onEntryRemove;
    }

    public void setOnEntryRemove(Consumer<Field> onEntryRemove)
    {
        this.onEntryRemove = onEntryRemove;
    }
}

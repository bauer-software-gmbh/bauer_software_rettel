package de.bauersoft.views.field.multiplier;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.field.Field;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplierKey;
import de.bauersoft.views.field.FieldDialog;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class MultiplierComponent extends FlexLayout
{
    private final FieldDialog fieldDialog;
    private final Field item;
    private final MultiplierMapContainer multiplierMapContainer;
    private final Map<Course, MultiplierField> multiplierFieldMap;

    public MultiplierComponent(FieldDialog fieldDialog)
    {
        this.fieldDialog = fieldDialog;
        this.item = fieldDialog.getItem();
        this.multiplierMapContainer = new MultiplierMapContainer();

        multiplierFieldMap = new HashMap<>();

        for(FieldMultiplier fieldMultiplier : item.getFieldMultipliers())
            multiplierMapContainer.addIfAbsent(fieldMultiplier.getCourse(), () -> fieldMultiplier);

        for(Course course : fieldDialog.getCourseService().findAll())
        {
            MultiplierContainer multiplierContainer = (MultiplierContainer) multiplierMapContainer.addIfAbsent(course, () ->
            {
                FieldMultiplier multiplier = new FieldMultiplier();
                multiplier.setId(new FieldMultiplierKey(null, course.getId()));
                multiplier.setField(item);
                multiplier.setCourse(course);

                return multiplier;
            }).setState(ContainerState.UPDATE);

            MultiplierField multiplierField = new MultiplierField(multiplierContainer);
            multiplierField.getStyle().setMarginRight("1em");
            multiplierFieldMap.put(course, multiplierField);

            this.add(multiplierField);
        }

        this.getStyle()
                .setMarginTop("1em")
                .setMaxWidth("50vw")
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }

    @Getter
    private class MultiplierField extends NumberField
    {
        private final MultiplierContainer multiplierContainer;
        private final Course course;

        public MultiplierField(MultiplierContainer multiplierContainer)
        {
            super(multiplierContainer.getEntity().getCourse().getName());

            this.multiplierContainer = multiplierContainer;
            this.course = multiplierContainer.getEntity().getCourse();

            this.setTooltipText(course.getName());

            this.setValue(Objects.requireNonNullElse(multiplierContainer.getEntity().getMultiplier(), 1d));

            this.addValueChangeListener(event ->
            {
                multiplierContainer.setTempMultiplier(Objects.requireNonNullElse(event.getValue(), 1d));
            });

            this.getStyle()
                    .setWidth("calc(20% - 1em)");
//            this.getStyle()
//                    .setWidth("calc(100% / 5 - 1em)")
//                    .setMaxWidth("calc(100% / 5 - 1em)")
//                    .setMarginLeft("5px")
//                    .setMarginRight("5px");
        }
    }
}

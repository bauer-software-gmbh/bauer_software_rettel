package de.bauersoft.views.institution.institutionFields.components.multiplier;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import de.bauersoft.views.institution.institutionFields.components.InstitutionFieldContainer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class MultiplierComponent extends FlexLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;

    private final InstitutionFieldContainer institutionFieldContainer;

    private final CourseService courseService;

    private final Map<Course, MultiplierField> multiplierFieldMap;

    public MultiplierComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, InstitutionFieldContainer institutionFieldContainer)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionFieldContainer.getInstitutionField();

        this.institutionFieldContainer = institutionFieldContainer;

        courseService = institutionDialog.getCourseService();

        multiplierFieldMap = new HashMap<>();

        for(Course course : courseService.findAll())
        {
            MultiplierContainer multiplierContainer = institutionFieldContainer
                    .getMultiplierContainers()
                    .getOrDefault(course, new MultiplierContainer(course, 1d));

            MultiplierField multiplierField = new MultiplierField(multiplierContainer);

            multiplierFieldMap.put(course, multiplierField);
            this.add(multiplierField);
        }

        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }

    public void saveToContainer()
    {
        multiplierFieldMap.values().forEach(MultiplierField::saveToContainer);
    }

    public class MultiplierField extends NumberField
    {
        private final MultiplierContainer multiplierContainer;

        private final Course course;

        public MultiplierField(MultiplierContainer multiplierContainer)
        {
            super(multiplierContainer.getCourse().getName());

            this.multiplierContainer = multiplierContainer;

            course = multiplierContainer.getCourse();

            this.setTooltipText(course.getName());
            this.setAllowedCharPattern("[0-9.,]");
            this.setMin(0);
            this.setMax(Integer.MAX_VALUE);

            this.setValue(Objects.requireNonNullElse(multiplierContainer.getMultiplier(), 1d));

            this.getStyle()
                .setWidth("calc(100% / 5 - 1em)")
                .setMaxWidth("calc(100% / 5 - 1em)")
                .setMarginLeft("5px")
                .setMarginRight("5px");
        }

        public void saveToContainer()
        {
            multiplierContainer.setMultiplier(Objects.requireNonNullElse(this.getValue(), 1d));
            institutionFieldContainer.getMultiplierContainers().put(course, multiplierContainer);
        }
    }
}

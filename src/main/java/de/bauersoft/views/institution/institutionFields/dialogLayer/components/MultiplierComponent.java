package de.bauersoft.views.institution.institutionFields.dialogLayer.components;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.dialogLayer.InstitutionFieldDialog;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MultiplierComponent extends FlexLayout
{
    private final InstitutionDialog institutionDialog;
    private final InstitutionFieldDialog institutionFieldDialog;
    private final InstitutionField institutionField;

    private final CourseService courseService;

    private final Map<Course, NumberField> multipliersMap;

    public MultiplierComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, InstitutionField institutionField)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionField;

        courseService = institutionDialog.getCourseService();

        multipliersMap = new HashMap<>();

        for(Course course : courseService.findAll())
        {
            NumberField numberField = new NumberField(course.getName());
            numberField.setTooltipText(course.getName());
            numberField.setAllowedCharPattern("[0-9.,]");
            numberField.setMin(0);
            numberField.setMax(Integer.MAX_VALUE);

            numberField.getStyle()
                        .setWidth("calc(100% / 5 - 1em)")
                        .setMaxWidth("calc(100% / 5 - 1em)")
                        .setMarginLeft("5px")
                        .setMarginRight("5px");

            multipliersMap.put(course, numberField);
        }

        multipliersMap.values().forEach(this::add);

        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setMarginBottom("var(--lumo-space-s)")
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
    }
}

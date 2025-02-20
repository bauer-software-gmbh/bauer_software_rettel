package de.bauersoft.views.institution.institutionFields.components.multiplier;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.institution.InstitutionField;
import de.bauersoft.data.entities.institution.InstitutionMultiplier;
import de.bauersoft.data.entities.institution.InstitutionMultiplierKey;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
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

    private final MultiplierMapContainer multiplierListContainer;

    private final CourseService courseService;

    private final Map<Course, MultiplierField> multiplierFieldMap;

    public MultiplierComponent(InstitutionDialog institutionDialog, InstitutionFieldDialog institutionFieldDialog, MultiplierMapContainer multiplierListContainer)
    {
        this.institutionDialog = institutionDialog;
        this.institutionFieldDialog = institutionFieldDialog;
        this.institutionField = institutionFieldDialog.getInstitutionField();

        this.multiplierListContainer = multiplierListContainer;

        courseService = institutionDialog.getCourseService();

        multiplierFieldMap = new HashMap<>();

        for(Course course : courseService.findAll())
        {
            MultiplierContainer multiplierContainer = (MultiplierContainer) multiplierListContainer.addIfAbsent(course, () ->
            {
                InstitutionMultiplier institutionMultiplier = new InstitutionMultiplier();
                institutionMultiplier.setId(new InstitutionMultiplierKey(null, course.getId()));
                institutionMultiplier.setInstitutionField(institutionField);
                institutionMultiplier.setCourse(course);

                return institutionMultiplier;
            });

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

    public class MultiplierField extends NumberField
    {
        private final MultiplierContainer multiplierContainer;

        private final Course course;

        public MultiplierField(MultiplierContainer multiplierContainer)
        {
            super(multiplierContainer.getEntity().getCourse().getName());

            this.multiplierContainer = multiplierContainer;

            this.course = multiplierContainer.getEntity().getCourse();;

            this.setTooltipText(course.getName());
            this.setAllowedCharPattern("[0-9.,]");
            this.setMin(0);
            this.setMax(Integer.MAX_VALUE);

            this.setValue(Objects.requireNonNullElse(multiplierContainer.getEntity().getMultiplier(), 1d));

            this.addValueChangeListener(event ->
            {
                multiplierContainer.setTempMultiplier(Objects.requireNonNullElse(event.getValue(), 0d));
            });

            this.getStyle()
                .setWidth("calc(100% / 5 - 1em)")
                .setMaxWidth("calc(100% / 5 - 1em)")
                .setMarginLeft("5px")
                .setMarginRight("5px");
        }

    }

}

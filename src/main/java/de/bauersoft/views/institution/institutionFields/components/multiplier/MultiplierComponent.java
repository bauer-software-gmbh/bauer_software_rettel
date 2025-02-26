package de.bauersoft.views.institution.institutionFields.components.multiplier;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplierKey;
import de.bauersoft.services.CourseService;
import de.bauersoft.views.institution.InstitutionDialog;
import de.bauersoft.components.container.ContainerState;
import de.bauersoft.views.institution.institutionFields.InstitutionFieldDialog;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
            }).setState(ContainerState.UPDATE);

            MultiplierField multiplierField = new MultiplierField(multiplierContainer);


            multiplierFieldMap.put(course, multiplierField);
            this.add(multiplierField);
            this.setFlexBasis("20%", multiplierField); // Stellt sicher, dass nur 5 Elemente in einer Reihe sind
            this.setFlexShrink(0, multiplierField);
            multiplierField.getStyle()
                    .set("padding", "10px")
                    .set("text-align", "center")
                    .setMaxWidth("calc(100% / 6)");
        }

        this.setJustifyContentMode(JustifyContentMode.BETWEEN);
        // this.setFlexGrow(1);
        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");

        this.getStyle().set("gap", "0px").set("padding", "0px").set("margin", "0px");
    }

    public class MultiplierField extends NumberField
    {
        private final MultiplierContainer multiplierContainer;
        private final Course course;

        private Optional<FieldMultiplier> fieldMultiplier;


        public MultiplierField(MultiplierContainer multiplierContainer)
        {
            super(multiplierContainer.getEntity().getCourse().getName());
            this.course = multiplierContainer.getEntity().getCourse();

            this.multiplierContainer = multiplierContainer;

            fieldMultiplier = institutionDialog.getFieldMultiplierService().findByFieldAndCourse(institutionField.getField(), course);

            this.setTooltipText(course.getName());
            this.setAllowedCharPattern("[0-9.,]");
            this.setMin(0);
            this.setMax(Integer.MAX_VALUE);

            fieldMultiplier.ifPresentOrElse(multiplier ->
            {
                this.setPlaceholder(String.valueOf(fieldMultiplier.get().getMultiplier()));

            }, () ->
            {
                this.setPlaceholder("1.0");
            });

            this.setValue(Objects.requireNonNullElse(multiplierContainer.getEntity().getMultiplier(), 1d));

            this.addValueChangeListener(event ->
            {
                multiplierContainer.setTempMultiplier(Objects.requireNonNullElse(event.getValue(), 1d));
            });

//            this.getStyle()
////                .setWidth("calc(100% / 5 - 1em)")
////                .setMaxWidth("calc(100% / 5 - 1em)")
////                .setMarginLeft("5px")
////                .setMarginRight("5px");
        }

    }

}

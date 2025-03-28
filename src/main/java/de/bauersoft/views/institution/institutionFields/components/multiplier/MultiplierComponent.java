package de.bauersoft.views.institution.institutionFields.components.multiplier;

import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Style;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import de.bauersoft.data.entities.institutionField.InstitutionField;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplier;
import de.bauersoft.data.entities.institutionFieldMultiplier.InstitutionMultiplierKey;
import de.bauersoft.services.CourseService;
import de.bauersoft.services.FieldMultiplierService;
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
    private final FieldMultiplierService fieldMultiplierService;
    private final CourseService courseService;
    private final InstitutionField item;

    private final MultiplierMapContainer multiplierListContainer;

    private final Map<Course, MultiplierField> multiplierFieldMap;

    private final HorizontalLayout headerLayout;
    private final TextField headerField;

    public MultiplierComponent(FieldMultiplierService fieldMultiplierService, CourseService courseService, InstitutionField item, MultiplierMapContainer multiplierListContainer)
    {
        this.fieldMultiplierService = fieldMultiplierService;
        this.courseService = courseService;
        this.item = item;

        this.multiplierListContainer = multiplierListContainer;

        multiplierFieldMap = new HashMap<>();

        headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();

        headerField = new TextField();
        headerField.setWidthFull();
        headerField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER, TextFieldVariant.LUMO_SMALL);
        headerField.setReadOnly(true);
        headerField.setValue("Multiplikatoren");

        headerLayout.add(headerField);
        this.add(headerLayout);

        for(Course course : courseService.findAll())
        {
            MultiplierContainer multiplierContainer = (MultiplierContainer) multiplierListContainer.addIfAbsent(course, () ->
            {
                InstitutionMultiplier institutionMultiplier = new InstitutionMultiplier();
                institutionMultiplier.setId(new InstitutionMultiplierKey(null, course.getId()));
                institutionMultiplier.setInstitutionField(item);
                institutionMultiplier.setCourse(course);

                return institutionMultiplier;
            }, ContainerState.SHOW);

            MultiplierField multiplierField = new MultiplierField(multiplierContainer);

            multiplierFieldMap.put(course, multiplierField);
            this.add(multiplierField);
        }

        this.getStyle()
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setDisplay(Style.Display.FLEX)
                .set("padding", "var(--lumo-space-s)")
                .set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-s)");
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

            fieldMultiplier = fieldMultiplierService.findByFieldAndCourse(item.getField(), course);

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
                multiplierContainer.setTempState(ContainerState.UPDATE);
            });

            this.setWidth("18.94%");
            this.getStyle()
                    .setPadding("var(--lumo-space-xs)");
        }

    }

}

package de.bauersoft.views.menuBuilder.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.repositories.course.CourseRepository;
import org.vaadin.lineawesome.LineAwesomeIcon;

public class CourseCluster extends ClusterLayout
{

    private CourseRepository courseRepository;

    private Button addButton;

    public CourseCluster(CourseRepository courseRepository)
    {
        this.courseRepository = courseRepository;

        addButton = new Button();
        addButton.setIcon(LineAwesomeIcon.PLUS_SQUARE.create());
        addButton.getStyle().set("flex-grow", "2");
        addButton.getStyle().set("align-self", "stretch");

        this.addComponent(addButton);

        courseRepository.findAll().forEach(course ->
        {
            addComponent(getCourseField(course));
        });

        updateComponents();
    }



    private TextField getCourseField(Course course)
    {
        TextField boundField = new TextField();
        boundField.setValue(course.getName());
        boundField.setTooltipText(course.getName());
        boundField.setReadOnly(true);
        boundField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);

        return boundField;
    }



    public Button getAddButton()
    {
        return addButton;
    }
}

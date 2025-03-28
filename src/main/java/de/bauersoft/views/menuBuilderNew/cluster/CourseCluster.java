package de.bauersoft.views.menuBuilderNew.cluster;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.bauersoft.data.entities.course.Course;
import lombok.Getter;
import org.vaadin.lineawesome.LineAwesomeIcon;

@Getter
public class CourseCluster extends VerticalCluster
{
    private final ClusterManager clusterManager;

    private final Button addButton;

    public CourseCluster(ClusterManager clusterManager)
    {
        this.clusterManager = clusterManager;

        addButton = new Button(LineAwesomeIcon.PLUS_SQUARE_SOLID.create());
        addButton.setWidthFull();
        addButton.getStyle()
                .setFontSize("2em")
                .set("flex-grow", "2");
                //.set("align-self", "stretch");

        this.add(addButton);

        for(Course course : clusterManager.getCoursePool())
        {
            TextField courseField = new TextField();
            courseField.setWidthFull();
            courseField.setReadOnly(true);
            courseField.setValue(course.getName());

            this.add(courseField);
        }
    }
}

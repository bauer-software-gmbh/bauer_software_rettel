package de.bauersoft.data.entities.component;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.entities.unit.Unit;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "component")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Component extends AbstractEntity
{
    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @Column(length = 1024)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", referencedColumnName = "id")
    private Unit unit;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "component_recipes",
            joinColumns = @JoinColumn(name = "component_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> recipes = new HashSet<>();

    @Override
    public String toString()
    {
        return "Component{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", course=" + course +
                ", unit=" + unit +
                ", recipeSize=" + recipes.size() +
                '}';
    }
}

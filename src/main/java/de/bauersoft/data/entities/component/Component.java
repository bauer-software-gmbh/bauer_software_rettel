package de.bauersoft.data.entities.component;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.course.Course;
import de.bauersoft.data.entities.recipe.Recipe;
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
@Builder
public class Component extends AbstractEntity
{
    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @Column(length = 1024)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "component_recipes",
            joinColumns = @JoinColumn(name = "component_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> recipes = new HashSet<>();
}

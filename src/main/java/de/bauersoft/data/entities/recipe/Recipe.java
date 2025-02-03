package de.bauersoft.data.entities.recipe;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.component.Component;
import de.bauersoft.data.entities.formulation.Formulation;
import de.bauersoft.data.entities.pattern.Pattern;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipe")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Recipe extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1024)
    private String description;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER)
    //TODO testen ob ich das brauche - Milan vvv
//    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Formulation> formulations = new HashSet<>(); //<- da lassen!

    @ManyToMany(mappedBy = "recipes", fetch = FetchType.EAGER)
    private Set<Component> components;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_patterns",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "pattern_id")
    )
    private Set<Pattern> patterns;

    @Override
    public String toString()
    {
        return "Recipe{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

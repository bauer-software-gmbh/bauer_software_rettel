package de.bauersoft.data.entities.additive;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.ingredient.Ingredient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
@Table(name = "additive", indexes =
        {
                @Index(name = "description_fulltext", columnList = "description")
        })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Additive extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false, length = 1024)
    private String description;

    @ManyToMany(mappedBy = "additives", fetch = FetchType.EAGER)
    private Set<Ingredient> ingredients;
}

package de.bauersoft.data.entities.pattern;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.recipe.Recipe;
import de.bauersoft.data.entities.variant.Variant;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "pattern")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Pattern extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean religious;

    @OneToMany(mappedBy = "pattern", fetch = FetchType.EAGER)
    private Set<Variant> variants;

    @ManyToMany(mappedBy = "patterns", fetch = FetchType.EAGER)
    private Set<Recipe> recipes;
}

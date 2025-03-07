package de.bauersoft.data.entities.field;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.fieldMultiplier.FieldMultiplier;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "field")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Field extends AbstractEntity
{
    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "field_id", referencedColumnName = "id")
    private Set<FieldMultiplier> fieldMultipliers = new HashSet<>();

    @Override
    public String toString()
    {
        return "Field{" +
                "name='" + name + '\'' +
                '}';
    }
}

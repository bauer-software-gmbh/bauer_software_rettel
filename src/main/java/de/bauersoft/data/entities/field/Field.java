package de.bauersoft.data.entities.field;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "field")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Field extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;

//    @OneToMany(mappedBy = "field", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
//    private Set<InstitutionFields> institutionFields;

    @Override
    public String toString()
    {
        return "Field{" +
                "name='" + name + '\'' +
                '}';
    }
}

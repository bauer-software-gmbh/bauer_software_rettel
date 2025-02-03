package de.bauersoft.data.entities.course;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.component.Component;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Course extends AbstractEntity
{

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
    private Set<Component> components;

}

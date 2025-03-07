package de.bauersoft.data.entities.course;

import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.component.Component;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course extends AbstractEntity
{

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    private Set<Component> components = new HashSet<>();
}

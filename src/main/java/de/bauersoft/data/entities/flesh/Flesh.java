package de.bauersoft.data.entities.flesh;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "flesh")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Flesh extends AbstractEntity
{
    @Column(nullable = false, unique = true)
    private String name;
}

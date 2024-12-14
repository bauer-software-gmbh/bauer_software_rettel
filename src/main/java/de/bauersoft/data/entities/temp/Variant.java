package de.bauersoft.data.entities.temp;

import de.bauersoft.data.entities.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "variant")
public class Variant extends AbstractEntity
{

    @OneToMany
    @JoinColumn(name = "part_id")
    private Set<Part> parts;


    public Set<Part> getParts()
    {
        return parts;
    }

    public Variant setParts(Set<Part> parts)
    {
        this.parts = parts;
        return this;
    }
}

package de.bauersoft.data.entities;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "integer default 1")
    private int version = 1;
    
	public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public int getVersion()
    {
        return version;
    }
    
    @Override
    public int hashCode()
    {
        if(getId() != null)
            return getId().hashCode();

        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof AbstractEntity that))
            return false; // null or not an AbstractEntity class

        if(getId() != null)
            return getId().equals(that.getId());

        return super.equals(that);
    }
    
}

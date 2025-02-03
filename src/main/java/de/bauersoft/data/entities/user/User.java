package de.bauersoft.data.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.bauersoft.data.entities.AbstractEntity;
import de.bauersoft.data.entities.institution.Institution;
import de.bauersoft.data.entities.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User extends AbstractEntity
{
    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String surname;

    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "BINARY(60)")
    private String password;

//    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
//    @Enumerated(EnumType.STRING)
//    @CollectionTable(name = "user_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}))
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
    private Set<Institution> institutions;
}

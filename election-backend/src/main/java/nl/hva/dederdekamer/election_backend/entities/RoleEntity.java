package nl.hva.dederdekamer.election_backend.entities;

import jakarta.persistence.*;
import nl.hva.dederdekamer.election_backend.model.RoleName;

/**
 * JPA entity representing an authorization role.
 * Guaranteed unique by {@link #name} via a DB unique constraint.
 */
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class RoleEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RoleName name;

    /** JPA-only constructor. */
    protected RoleEntity() {}

    /**
     * Creates a new role with the given name.
     * @param name role enum value
     */
    public RoleEntity(RoleName name) { this.name = name; }

    public Long getId() { return id; }
    public RoleName getName() { return name; }
}

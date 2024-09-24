package org.example.basewebsub.entity;

import javax.persistence.*;

@Entity
@Table(name = "ROLES")
public class RolesEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic
    @Column(name = "NAME")
    private String name;

    @Basic
    @Column(name = "NAME_DESCRIPTIONS")
    private String nameDescriptions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameDescriptions() {
        return nameDescriptions;
    }

    public void setNameDescriptions(String nameDescriptions) {
        this.nameDescriptions = nameDescriptions;
    }
}

package com.sliit.aams.project.model;

import jakarta.persistence.*;

/**
 * Owner: Ranaweera D.A.I.M. (IT25100994)
 * TODO: Add fields based on the project use case table in the lab sheet.
 */
@Entity
@Table(name = "project_project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add attributes

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

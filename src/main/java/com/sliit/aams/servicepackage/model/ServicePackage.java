package com.sliit.aams.servicepackage.model;

import jakarta.persistence.*;

/**
 * Owner: Deshapriya P.A.D.A. (IT25102331)
 * TODO: Add fields based on the servicepackage use case table in the lab sheet.
 */
@Entity
@Table(name = "servicepackage_servicepackage")
public class ServicePackage {

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

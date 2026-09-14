package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;

/**
 * Owner: Jayalath M.P.M.P.A. (IT25102962)
 * TODO: Add fields based on the useraccount use case table in the lab sheet.
 */
@Entity
@Table(name = "useraccount_user")
public class User {

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

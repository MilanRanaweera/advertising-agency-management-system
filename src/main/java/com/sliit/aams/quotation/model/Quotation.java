package com.sliit.aams.quotation.model;

import jakarta.persistence.*;

/**
 * Owner: Silva T.T.M. (IT25103316)
 * TODO: Add fields based on the quotation use case table in the lab sheet.
 */
@Entity
@Table(name = "quotation_quotation")
public class Quotation {

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

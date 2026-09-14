package com.sliit.aams.communication.model;

import jakarta.persistence.*;

/**
 * Owner: Igalawithana N.C. (IT25100243)
 * TODO: Add fields based on the communication use case table in the lab sheet.
 */
@Entity
@Table(name = "communication_inquiry")
public class Inquiry {

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

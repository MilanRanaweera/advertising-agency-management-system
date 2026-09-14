package com.sliit.aams.payment.model;

import jakarta.persistence.*;

/**
 * Owner: Ihjas I.M. (IT25101525)
 * TODO: Add fields based on the payment use case table in the lab sheet.
 */
@Entity
@Table(name = "payment_payment")
public class Payment {

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

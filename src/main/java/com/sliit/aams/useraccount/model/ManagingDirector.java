package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "managing_director")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ManagingDirector {

    @Id @Column(name = "manager_id")
    private Long managerId;

    @OneToOne @MapsId
    @JoinColumn(name = "manager_id")
    private Employee employee;

    @Column(name = "first_name", length = 100) private String firstName;
    @Column(name = "last_name",  length = 100) private String lastName;
    @Column(name = "telephone_no", length = 30) private String telephoneNo;
}
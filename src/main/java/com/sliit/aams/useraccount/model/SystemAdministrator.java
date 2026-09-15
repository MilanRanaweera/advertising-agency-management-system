package com.sliit.aams.useraccount.model;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_administrator")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SystemAdministrator {
    @Id @Column(name = "admin_id") private Long adminId;

    @OneToOne @MapsId @JoinColumn(name = "admin_id")
    private User user;

    @Column(name = "last_login") private LocalDateTime lastLogin;
}
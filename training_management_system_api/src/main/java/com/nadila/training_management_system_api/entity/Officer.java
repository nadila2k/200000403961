package com.nadila.training_management_system_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "officers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Officer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long officerId;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String nic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private String designation;

    private String email;

    private String phone;
}

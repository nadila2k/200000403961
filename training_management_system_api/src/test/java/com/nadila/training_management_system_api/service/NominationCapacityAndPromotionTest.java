package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.NominationRequest;
import com.nadila.training_management_system_api.dto.response.NominationResponse;
import com.nadila.training_management_system_api.entity.Department;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.NominationStatus;
import com.nadila.training_management_system_api.repository.DepartmentRepository;
import com.nadila.training_management_system_api.repository.NominationRepository;
import com.nadila.training_management_system_api.repository.OfficerRepository;
import com.nadila.training_management_system_api.repository.TrainingProgrammeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NominationCapacityAndPromotionTest {

    @Autowired
    private NominationService nominationService;

    @Autowired
    private TrainingProgrammeRepository programmeRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private NominationRepository nominationRepository;

    private TrainingProgramme programme;
    private Officer officer1;
    private Officer officer2;
    private Officer officer3;
    private Department dept;

    @BeforeEach
    void setUp() {
        nominationRepository.deleteAll();
        officerRepository.deleteAll();
        programmeRepository.deleteAll();
        departmentRepository.deleteAll();

        dept = departmentRepository.save(Department.builder()
                .name("IT Security Dept")
                .focalPointEmail("security@gov.lk")
                .focalPointPhone("+94112000000")
                .build());

        officer1 = officerRepository.save(Officer.builder()
                .fullName("Alice Officer")
                .nic("NIC_TEST_9901")
                .department(dept)
                .designation("Cyber Specialist 1")
                .email("alice@gov.lk")
                .phone("+94770000001")
                .build());

        officer2 = officerRepository.save(Officer.builder()
                .fullName("Bob Officer")
                .nic("NIC_TEST_9902")
                .department(dept)
                .designation("Cyber Specialist 2")
                .email("bob@gov.lk")
                .phone("+94770000002")
                .build());

        officer3 = officerRepository.save(Officer.builder()
                .fullName("Charlie Officer")
                .nic("NIC_TEST_9903")
                .department(dept)
                .designation("Cyber Specialist 3")
                .email("charlie@gov.lk")
                .phone("+94770000003")
                .build());

        // Programme with capacity maxParticipants = 2
        programme = programmeRepository.save(TrainingProgramme.builder()
                .title("Cybersecurity Awareness Programme")
                .description("Limited capacity course")
                .startDate(LocalDate.now().plusDays(5))
                .endDate(LocalDate.now().plusDays(7))
                .maxParticipants(2)
                .build());
    }

    @Test
    @DisplayName("Should confirm first nominations up to capacity (2) and waitlist the 3rd nomination")
    void testCapacityLimitsAndWaitlist() {
        // First nomination -> should be APPROVED (confirmed)
        NominationResponse nom1 = nominationService.nominate(NominationRequest.builder()
                .programmeId(programme.getProgrammeId())
                .officerId(officer1.getOfficerId())
                .nominatingDepartmentId(dept.getDepartmentId())
                .build());

        assertEquals(NominationStatus.APPROVED, nom1.getStatus());

        // Second nomination -> should be APPROVED (confirmed)
        NominationResponse nom2 = nominationService.nominate(NominationRequest.builder()
                .programmeId(programme.getProgrammeId())
                .officerId(officer2.getOfficerId())
                .nominatingDepartmentId(dept.getDepartmentId())
                .build());

        assertEquals(NominationStatus.APPROVED, nom2.getStatus());

        // Third nomination -> capacity (2) reached, should be placed on WAITLISTED status
        NominationResponse nom3 = nominationService.nominate(NominationRequest.builder()
                .programmeId(programme.getProgrammeId())
                .officerId(officer3.getOfficerId())
                .nominatingDepartmentId(dept.getDepartmentId())
                .build());

        assertEquals(NominationStatus.WAITLISTED, nom3.getStatus());
    }

    @Test
    @DisplayName("Should automatically promote first waitlisted officer when a confirmed participant withdraws")
    void testAutoPromotionOnWithdrawal() {
        NominationResponse nom1 = nominationService.nominate(NominationRequest.builder()
                .programmeId(programme.getProgrammeId())
                .officerId(officer1.getOfficerId())
                .nominatingDepartmentId(dept.getDepartmentId())
                .build());

        NominationResponse nom2 = nominationService.nominate(NominationRequest.builder()
                .programmeId(programme.getProgrammeId())
                .officerId(officer2.getOfficerId())
                .nominatingDepartmentId(dept.getDepartmentId())
                .build());

        NominationResponse nom3 = nominationService.nominate(NominationRequest.builder()
                .programmeId(programme.getProgrammeId())
                .officerId(officer3.getOfficerId())
                .nominatingDepartmentId(dept.getDepartmentId())
                .build());

        assertEquals(NominationStatus.APPROVED, nom1.getStatus());
        assertEquals(NominationStatus.APPROVED, nom2.getStatus());
        assertEquals(NominationStatus.WAITLISTED, nom3.getStatus());

        // Officer 1 withdraws nomination -> Seat opens up
        nominationService.withdraw(nom1.getNominationId());

        // Verify Officer 1 status is WITHDRAWN
        NominationResponse updatedNom1 = nominationService.getById(nom1.getNominationId());
        assertEquals(NominationStatus.WITHDRAWN, updatedNom1.getStatus());

        // Verify Officer 3 (first waitlisted) is automatically promoted to APPROVED
        NominationResponse updatedNom3 = nominationService.getById(nom3.getNominationId());
        assertEquals(NominationStatus.APPROVED, updatedNom3.getStatus());
    }
}

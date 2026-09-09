package com.nadila.training_management_system_api.data;

import com.nadila.training_management_system_api.entity.*;
import com.nadila.training_management_system_api.enums.TrainerType;
import com.nadila.training_management_system_api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final OfficerRepository officerRepository;
    private final VenueRepository venueRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingProgrammeRepository trainingProgrammeRepository;

    @Override
    @Transactional
    public void run(String... args) {

        // Prevent duplicate seed data
        if (departmentRepository.count() > 0) {
            return;
        }

        /*
         * =========================
         * DEPARTMENTS
         * =========================
         */

        Department hrDepartment = departmentRepository.save(
                Department.builder()
                        .name("Human Resources")
                        .focalPointEmail("hr@example.com")
                        .focalPointPhone("+94770000001")
                        .build()
        );

        Department itDepartment = departmentRepository.save(
                Department.builder()
                        .name("Information Technology")
                        .focalPointEmail("it@example.com")
                        .focalPointPhone("+94770000002")
                        .build()
        );

        Department financeDepartment = departmentRepository.save(
                Department.builder()
                        .name("Finance")
                        .focalPointEmail("finance@example.com")
                        .focalPointPhone("+94770000003")
                        .build()
        );

        Department administrationDepartment = departmentRepository.save(
                Department.builder()
                        .name("Administration")
                        .focalPointEmail("admin@example.com")
                        .focalPointPhone("+94770000004")
                        .build()
        );


        /*
         * =========================
         * OFFICERS
         * =========================
         */

        officerRepository.saveAll(List.of(

                Officer.builder()
                        .fullName("Kasun Perera")
                        .nic("199012345678")
                        .department(hrDepartment)
                        .designation("HR Officer")
                        .email("kasun@example.com")
                        .phone("+94771111111")
                        .build(),

                Officer.builder()
                        .fullName("Nimal Silva")
                        .nic("198812345679")
                        .department(itDepartment)
                        .designation("Software Engineer")
                        .email("nimal@example.com")
                        .phone("+94772222222")
                        .build(),

                Officer.builder()
                        .fullName("Amal Fernando")
                        .nic("199212345680")
                        .department(financeDepartment)
                        .designation("Finance Officer")
                        .email("amal@example.com")
                        .phone("+94773333333")
                        .build(),

                Officer.builder()
                        .fullName("Saman Kumara")
                        .nic("198512345681")
                        .department(administrationDepartment)
                        .designation("Administrative Officer")
                        .email("saman@example.com")
                        .phone("+94774444444")
                        .build()
        ));


        /*
         * =========================
         * VENUES
         * =========================
         */

        Venue mainHall = venueRepository.save(
                Venue.builder()
                        .name("Main Training Hall")
                        .location("Head Office")
                        .capacity(100)
                        .build()
        );

        Venue conferenceRoom = venueRepository.save(
                Venue.builder()
                        .name("Conference Room A")
                        .location("Administration Building")
                        .capacity(40)
                        .build()
        );

        Venue computerLab = venueRepository.save(
                Venue.builder()
                        .name("Computer Laboratory")
                        .location("IT Building")
                        .capacity(30)
                        .build()
        );


        /*
         * =========================
         * TRAINERS
         * =========================
         */

        Trainer internalTrainer = trainerRepository.save(
                Trainer.builder()
                        .fullName("Dr. Nuwan Jayasinghe")
                        .type(TrainerType.INTERNAL)
                        .specialization("Leadership and Management")
                        .organization("Internal Training Unit")
                        .email("nuwan@example.com")
                        .phone("+94775555555")
                        .build()
        );

        Trainer externalTrainer = trainerRepository.save(
                Trainer.builder()
                        .fullName("Prof. Malini Perera")
                        .type(TrainerType.EXTERNAL)
                        .specialization("Information Technology")
                        .organization("ABC Training Institute")
                        .email("malini@example.com")
                        .phone("+94776666666")
                        .build()
        );


        /*
         * =========================
         * TRAINING PROGRAMMES
         * =========================
         */

        Set<Department> leadershipDepartments = new HashSet<>();
        leadershipDepartments.add(hrDepartment);
        leadershipDepartments.add(administrationDepartment);

        TrainingProgramme leadershipProgramme =
                TrainingProgramme.builder()
                        .title("Leadership Development Programme")
                        .description(
                                "A training programme designed to improve leadership, " +
                                        "communication and management skills."
                        )
                        .startDate(LocalDate.now().plusDays(10))
                        .endDate(LocalDate.now().plusDays(12))
                        .venue(mainHall)
                        .trainer(internalTrainer)
                        .maxParticipants(50)
                        .targetDepartments(leadershipDepartments)
                        .build();


        Set<Department> javaDepartments = new HashSet<>();
        javaDepartments.add(itDepartment);

        TrainingProgramme javaProgramme =
                TrainingProgramme.builder()
                        .title("Advanced Java and Spring Boot")
                        .description(
                                "An advanced technical training programme covering " +
                                        "Java, Spring Boot and REST API development."
                        )
                        .startDate(LocalDate.now().plusDays(20))
                        .endDate(LocalDate.now().plusDays(24))
                        .venue(computerLab)
                        .trainer(externalTrainer)
                        .maxParticipants(25)
                        .targetDepartments(javaDepartments)
                        .build();


        Set<Department> financeDepartments = new HashSet<>();
        financeDepartments.add(financeDepartment);

        TrainingProgramme financeProgramme =
                TrainingProgramme.builder()
                        .title("Financial Management Training")
                        .description(
                                "Training focused on financial planning, budgeting " +
                                        "and financial management practices."
                        )
                        .startDate(LocalDate.now().plusDays(30))
                        .endDate(LocalDate.now().plusDays(32))
                        .venue(conferenceRoom)
                        .trainer(internalTrainer)
                        .maxParticipants(35)
                        .targetDepartments(financeDepartments)
                        .build();


        trainingProgrammeRepository.saveAll(List.of(
                leadershipProgramme,
                javaProgramme,
                financeProgramme
        ));

        System.out.println("=================================");
        System.out.println("Sample data seeded successfully!");
        System.out.println("=================================");
    }
}
package com.nadila.training_management_system_api.data;

import com.nadila.training_management_system_api.entity.*;
import com.nadila.training_management_system_api.enums.NominationStatus;
import com.nadila.training_management_system_api.enums.TrainerType;
import com.nadila.training_management_system_api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final NominationRepository nominationRepository;

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
                        .focalPointEmail("hr@gov.lk")
                        .focalPointPhone("+94 11 234 5678")
                        .build()
        );

        Department itDepartment = departmentRepository.save(
                Department.builder()
                        .name("Information Technology")
                        .focalPointEmail("it@gov.lk")
                        .focalPointPhone("+94 11 234 5679")
                        .build()
        );

        Department financeDepartment = departmentRepository.save(
                Department.builder()
                        .name("Finance & Treasury")
                        .focalPointEmail("finance@gov.lk")
                        .focalPointPhone("+94 11 234 5680")
                        .build()
        );

        Department administrationDepartment = departmentRepository.save(
                Department.builder()
                        .name("Administration & Operations")
                        .focalPointEmail("admin@gov.lk")
                        .focalPointPhone("+94 11 234 5681")
                        .build()
        );

        List<Department> allDepts = List.of(hrDepartment, itDepartment, financeDepartment, administrationDepartment);

        /*
         * =========================
         * VENUES
         * =========================
         */

        Venue mainHall = venueRepository.save(
                Venue.builder()
                        .name("Main Auditorium")
                        .location("Building A, 3rd Floor")
                        .capacity(150)
                        .build()
        );

        Venue conferenceRoom = venueRepository.save(
                Venue.builder()
                        .name("Executive Conference Room B")
                        .location("Building B, 1st Floor")
                        .capacity(40)
                        .build()
        );

        Venue computerLab = venueRepository.save(
                Venue.builder()
                        .name("Tech Training Lab 101")
                        .location("IT Center, Ground Floor")
                        .capacity(50)
                        .build()
        );

        /*
         * =========================
         * TRAINERS
         * =========================
         */

        Trainer internalTrainer = trainerRepository.save(
                Trainer.builder()
                        .fullName("Dr. Roy Fernando")
                        .type(TrainerType.INTERNAL)
                        .specialization("Public Leadership & Governance")
                        .organization("National Institute of Administration")
                        .email("roy.fernando@nia.gov.lk")
                        .phone("+94 71 111 2233")
                        .build()
        );

        Trainer externalTrainer = trainerRepository.save(
                Trainer.builder()
                        .fullName("Prof. Anura Jayawardena")
                        .type(TrainerType.EXTERNAL)
                        .specialization("Cybersecurity & Data Protection")
                        .organization("Cyber Resilience Center")
                        .email("anura.j@cybercenter.lk")
                        .phone("+94 71 444 5566")
                        .build()
        );

        /*
         * =========================
         * TRAINING PROGRAMMES
         * =========================
         */

        /*
         * =========================
         * TRAINING PROGRAMMES & ELIGIBILITY RULES
         * =========================
         */

        // 1. Cybersecurity Awareness Programme (Max 40 participants)
        Set<Department> cyberDepts = new HashSet<>(allDepts);

        TrainingProgramme cyberProgramme = TrainingProgramme.builder()
                .title("Cybersecurity Awareness Programme")
                .description("Essential security protocols, threat detection, phishing defense, and personal data protection compliance for public officers.")
                .startDate(LocalDate.now().plusDays(14))
                .endDate(LocalDate.now().plusDays(16))
                .venue(mainHall)
                .trainer(externalTrainer)
                .maxParticipants(40)
                .targetDepartments(cyberDepts)
                .build();

        // 2. Financial Management Programme
        TrainingProgramme finProgramme = TrainingProgramme.builder()
                .title("Financial Management Programme")
                .description("Budgeting, financial compliance, auditing, and fiscal governance in the public sector.")
                .startDate(LocalDate.now().plusDays(20))
                .endDate(LocalDate.now().plusDays(22))
                .venue(conferenceRoom)
                .trainer(internalTrainer)
                .maxParticipants(25)
                .targetDepartments(new HashSet<>(List.of(financeDepartment)))
                .build();

        // Eligibility Rule for Financial Management: Target Departments (Finance & Treasury, Budget, Planning)
        EligibilityRule finRule = EligibilityRule.builder()
                .programme(finProgramme)
                .ruleType(com.nadila.training_management_system_api.enums.EligibilityRuleType.TARGET_DEPARTMENT)
                .ruleValue("Finance & Treasury, Budget, Planning")
                .customErrorMessage("Only officers belonging to Finance, Budget, or Planning divisions are eligible.")
                .build();
        finProgramme.setEligibilityRules(List.of(finRule));

        // 3. Technical Programme
        TrainingProgramme techProgramme = TrainingProgramme.builder()
                .title("Technical Programme: Advanced Cloud Architecture")
                .description("Hands-on workshop on cloud migration, DevOps, and microservices for IT divisions.")
                .startDate(LocalDate.now().plusDays(30))
                .endDate(LocalDate.now().plusDays(33))
                .venue(computerLab)
                .trainer(externalTrainer)
                .maxParticipants(15)
                .targetDepartments(new HashSet<>(List.of(itDepartment)))
                .build();

        EligibilityRule techRule = EligibilityRule.builder()
                .programme(techProgramme)
                .ruleType(com.nadila.training_management_system_api.enums.EligibilityRuleType.TARGET_DEPARTMENT)
                .ruleValue("Information Technology, IT")
                .customErrorMessage("Only officers belonging to IT or ICT-related divisions are eligible.")
                .build();
        techProgramme.setEligibilityRules(List.of(techRule));

        // 4. Management Development Programme
        TrainingProgramme mgmtProgramme = TrainingProgramme.builder()
                .title("Management Development Programme")
                .description("Strategic leadership, policy formulation, and organizational change management.")
                .startDate(LocalDate.now().plusDays(40))
                .endDate(LocalDate.now().plusDays(45))
                .venue(conferenceRoom)
                .trainer(internalTrainer)
                .maxParticipants(20)
                .targetDepartments(new HashSet<>(allDepts))
                .build();

        EligibilityRule mgmtGradeRule = EligibilityRule.builder()
                .programme(mgmtProgramme)
                .ruleType(com.nadila.training_management_system_api.enums.EligibilityRuleType.REQUIRED_GRADE)
                .ruleValue("Grade I, Executive")
                .customErrorMessage("Management Development Programme requires Grade I or Executive designation.")
                .build();

        EligibilityRule mgmtServiceRule = EligibilityRule.builder()
                .programme(mgmtProgramme)
                .ruleType(com.nadila.training_management_system_api.enums.EligibilityRuleType.MIN_YEARS_OF_SERVICE)
                .ruleValue("3")
                .customErrorMessage("Management Development Programme requires a minimum of 3 years of service.")
                .build();

        EligibilityRule repeatRule = EligibilityRule.builder()
                .programme(mgmtProgramme)
                .ruleType(com.nadila.training_management_system_api.enums.EligibilityRuleType.REPEAT_COOLDOWN_PERIOD)
                .ruleValue("12")
                .customErrorMessage("Officer has already participated in this programme within the previous 12 months.")
                .build();

        mgmtProgramme.setEligibilityRules(List.of(mgmtGradeRule, mgmtServiceRule, repeatRule));

        trainingProgrammeRepository.saveAll(List.of(cyberProgramme, finProgramme, techProgramme, mgmtProgramme));

        /*
         * =========================
         * OFFICERS (60 Officers)
         * =========================
         */

        List<Officer> officersList = new ArrayList<>();
        String[] designations = {
                "Systems Analyst", "HR Officer", "Finance Executive", "Operations Assistant",
                "Senior IT Officer", "Accountant", "Administrative Officer", "Project Manager"
        };
        String[] grades = {"Grade I", "Grade II", "Grade III", "Executive"};

        for (int i = 1; i <= 60; i++) {
            Department dept = allDepts.get((i - 1) % allDepts.size());
            String desig = designations[(i - 1) % designations.length];
            String grade = grades[(i - 1) % grades.length];
            String nic = String.format("1990%08d", i);

            // Give varying service start dates (from 1 to 10 years ago)
            LocalDate startDate = LocalDate.now().minusYears(1 + (i % 10)).minusMonths(i % 12);

            Officer officer = Officer.builder()
                    .fullName(String.format("Officer %02d", i))
                    .nic(nic)
                    .department(dept)
                    .designation(desig)
                    .grade(grade)
                    .serviceStartDate(startDate)
                    .email(String.format("officer%02d@gov.lk", i))
                    .phone(String.format("+94 77 %03d %04d", 100 + (i % 900), 1000 + i))
                    .build();

            officersList.add(officer);
        }

        officersList = officerRepository.saveAll(officersList);

        /*
         * =========================
         * NOMINATIONS (60 Valid Nominations for Cybersecurity Awareness Programme)
         * - First 40 valid nominations -> APPROVED (Confirmed)
         * - Remaining 20 valid nominations -> WAITLISTED
         * =========================
         */

        List<Nomination> nominationsList = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(15);

        for (int i = 0; i < 60; i++) {
            Officer officer = officersList.get(i);
            // First 40 -> APPROVED, 41-60 -> WAITLISTED
            NominationStatus status = (i < 40) ? NominationStatus.APPROVED : NominationStatus.WAITLISTED;

            Nomination nom = Nomination.builder()
                    .programme(cyberProgramme)
                    .officer(officer)
                    .nominatingDepartment(officer.getDepartment())
                    .status(status)
                    .nominatedAt(baseTime.plusHours(i)) // Sequential timestamp for FIFO order
                    .build();

            nominationsList.add(nom);
        }

        nominationRepository.saveAll(nominationsList);

        System.out.println("=================================================");
        System.out.println("Data Seeding Completed Successfully!");
        System.out.println("Seeded: Cybersecurity Awareness Programme (Max: 40)");
        System.out.println("Seeded: 60 Officers & 60 Nominations (40 APPROVED, 20 WAITLISTED)");
        System.out.println("=================================================");
    }
}
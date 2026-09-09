package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.NominationRequest;
import com.nadila.training_management_system_api.dto.response.EligibilityCheckResponse;
import com.nadila.training_management_system_api.dto.response.NominationResponse;
import com.nadila.training_management_system_api.entity.Department;
import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import com.nadila.training_management_system_api.enums.NominationStatus;
import com.nadila.training_management_system_api.exception.IneligibleOfficerException;
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
class EligibilityRuleServiceTest {

    @Autowired
    private EligibilityService eligibilityService;

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

    private Department finDept;
    private Department hrDept;

    private Officer finOfficerEligible;
    private Officer hrOfficerIneligible;
    private Officer mgmtOfficerJunior;

    private TrainingProgramme finProgramme;
    private TrainingProgramme mgmtProgramme;

    @BeforeEach
    void setUp() {
        nominationRepository.deleteAll();
        officerRepository.deleteAll();
        programmeRepository.deleteAll();
        departmentRepository.deleteAll();

        finDept = departmentRepository.save(Department.builder()
                .name("Test Finance Dept")
                .focalPointEmail("fin@gov.lk")
                .focalPointPhone("+94112000001")
                .build());

        hrDept = departmentRepository.save(Department.builder()
                .name("Test HR Dept")
                .focalPointEmail("hr@gov.lk")
                .focalPointPhone("+94112000002")
                .build());

        finOfficerEligible = officerRepository.save(Officer.builder()
                .fullName("Fin Officer")
                .nic("NIC_FIN_001")
                .department(finDept)
                .designation("Senior Accountant")
                .grade("Executive")
                .serviceStartDate(LocalDate.now().minusYears(5))
                .email("fin.officer@gov.lk")
                .phone("+94770000100")
                .build());

        hrOfficerIneligible = officerRepository.save(Officer.builder()
                .fullName("HR Officer")
                .nic("NIC_HR_002")
                .department(hrDept)
                .designation("HR Assistant")
                .grade("Grade III")
                .serviceStartDate(LocalDate.now().minusYears(1))
                .email("hr.officer@gov.lk")
                .phone("+94770000200")
                .build());

        mgmtOfficerJunior = officerRepository.save(Officer.builder()
                .fullName("Junior Officer")
                .nic("NIC_JNR_003")
                .department(finDept)
                .designation("Assistant")
                .grade("Executive")
                .serviceStartDate(LocalDate.now().minusMonths(6)) // < 3 years
                .email("junior@gov.lk")
                .phone("+94770000300")
                .build());

        // Financial Management Programme (Department Rule: Finance, Budget, Planning)
        finProgramme = TrainingProgramme.builder()
                .title("Financial Management Programme")
                .description("Finance training")
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(12))
                .maxParticipants(10)
                .build();

        EligibilityRule deptRule = EligibilityRule.builder()
                .programme(finProgramme)
                .ruleType(EligibilityRuleType.TARGET_DEPARTMENT)
                .ruleValue("Test Finance Dept, Budget, Planning")
                .customErrorMessage("Only Finance, Budget, Planning divisions allowed")
                .build();

        finProgramme.setEligibilityRules(List.of(deptRule));
        finProgramme = programmeRepository.save(finProgramme);

        // Management Development Programme (Grade Rule: Grade I or Executive, Min Years: 3, Repeat Cooldown: 12 months)
        mgmtProgramme = TrainingProgramme.builder()
                .title("Management Development Programme")
                .description("Management training")
                .startDate(LocalDate.now().plusDays(20))
                .endDate(LocalDate.now().plusDays(25))
                .maxParticipants(10)
                .build();

        EligibilityRule gradeRule = EligibilityRule.builder()
                .programme(mgmtProgramme)
                .ruleType(EligibilityRuleType.REQUIRED_GRADE)
                .ruleValue("Grade I, Executive")
                .customErrorMessage("Requires Grade I or Executive")
                .build();

        EligibilityRule serviceRule = EligibilityRule.builder()
                .programme(mgmtProgramme)
                .ruleType(EligibilityRuleType.MIN_YEARS_OF_SERVICE)
                .ruleValue("3")
                .customErrorMessage("Requires 3 years of service")
                .build();

        EligibilityRule cooldownRule = EligibilityRule.builder()
                .programme(mgmtProgramme)
                .ruleType(EligibilityRuleType.REPEAT_COOLDOWN_PERIOD)
                .ruleValue("12")
                .customErrorMessage("Repeat participation within 12 months not allowed")
                .build();

        mgmtProgramme.setEligibilityRules(List.of(gradeRule, serviceRule, cooldownRule));
        mgmtProgramme = programmeRepository.save(mgmtProgramme);
    }

    @Test
    @DisplayName("Should evaluate department eligibility correctly")
    void testDepartmentEligibility() {
        EligibilityCheckResponse checkEligible = eligibilityService.checkEligibility(
                finProgramme.getProgrammeId(), finOfficerEligible.getOfficerId());
        assertTrue(checkEligible.isEligible());
        assertTrue(checkEligible.getFailureReasons().isEmpty());

        EligibilityCheckResponse checkIneligible = eligibilityService.checkEligibility(
                finProgramme.getProgrammeId(), hrOfficerIneligible.getOfficerId());
        assertFalse(checkIneligible.isEligible());
        assertFalse(checkIneligible.getFailureReasons().isEmpty());
    }

    @Test
    @DisplayName("Should reject nomination for department ineligible officer")
    void testNominationDepartmentIneligible() {
        assertThrows(IneligibleOfficerException.class, () -> {
            nominationService.nominate(NominationRequest.builder()
                    .programmeId(finProgramme.getProgrammeId())
                    .officerId(hrOfficerIneligible.getOfficerId())
                    .nominatingDepartmentId(hrDept.getDepartmentId())
                    .build());
        });
    }

    @Test
    @DisplayName("Should evaluate grade and min years of service rules")
    void testManagementProgrammeEligibility() {
        // Senior Fin Officer has Executive grade + 5 years of service -> Eligible
        EligibilityCheckResponse checkEligible = eligibilityService.checkEligibility(
                mgmtProgramme.getProgrammeId(), finOfficerEligible.getOfficerId());
        assertTrue(checkEligible.isEligible());

        // Junior Fin Officer has Executive grade but only 0.5 years service -> Ineligible (Min 3 years service rule)
        EligibilityCheckResponse checkJunior = eligibilityService.checkEligibility(
                mgmtProgramme.getProgrammeId(), mgmtOfficerJunior.getOfficerId());
        assertFalse(checkJunior.isEligible());
        assertTrue(checkJunior.getFailureReasons().stream().anyMatch(r -> r.contains("3 years")));

        // HR Officer has Grade III and 1 year service -> Ineligible on both Grade and Min Years
        EligibilityCheckResponse checkHR = eligibilityService.checkEligibility(
                mgmtProgramme.getProgrammeId(), hrOfficerIneligible.getOfficerId());
        assertFalse(checkHR.isEligible());
        assertEquals(2, checkHR.getFailureReasons().size());
    }

    @Test
    @DisplayName("Should enforce 12-month repeat participation rule")
    void testRepeatParticipationRule() {
        // First nomination succeeds
        NominationResponse nom1 = nominationService.nominate(NominationRequest.builder()
                .programmeId(mgmtProgramme.getProgrammeId())
                .officerId(finOfficerEligible.getOfficerId())
                .nominatingDepartmentId(finDept.getDepartmentId())
                .build());
        assertEquals(NominationStatus.APPROVED, nom1.getStatus());

        // Second nomination attempt for the same programme within 12 months -> Ineligible due to repeat rule
        EligibilityCheckResponse repeatCheck = eligibilityService.checkEligibility(
                mgmtProgramme.getProgrammeId(), finOfficerEligible.getOfficerId());
        assertFalse(repeatCheck.isEligible());
        assertTrue(repeatCheck.getFailureReasons().stream().anyMatch(r -> r.contains("12 months") || r.contains("Repeat")));
    }
}

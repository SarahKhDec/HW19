package org.example.HW19.service;

import org.example.HW19.entity.Customer;
import org.example.HW19.entity.Duty;
import org.example.HW19.entity.Expert;
import org.example.HW19.entity.UnderDuty;
import org.example.HW19.exceptions.UnderDutyExistException;
import org.example.HW19.exceptions.UnderDutyNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UnderDutyServiceTest {

    @Autowired
    private UnderDutyService underDutyService;

    @Autowired
    private DutyService dutyService;

    private UnderDuty kitchenAppliances;

    private UnderDuty houseSpraying;

    @BeforeAll
    static void setUp( @Autowired DutyService dutyService) {
        Duty dutyOne = dutyService.save(Duty.builder().name("One").build());
        Duty dutyTwo = dutyService.save(Duty.builder().name("Two").build());
        Duty dutyThree = dutyService.save(Duty.builder().name("Three").build());
    }

    @BeforeEach
    void setUp() {
        Duty homeAppliances = dutyService.findById(2);
        kitchenAppliances = UnderDuty.builder()
                .name("Kitchen Appliances")
                .basePrice(500_000L)
                .explanation("Repair of kitchen appliances")
                .duty(homeAppliances).build();
        Duty cleaningAndHygiene = dutyService.findById(3);
        houseSpraying = UnderDuty.builder()
                .name("House Spraying")
                .basePrice(700_000L)
                .explanation("Eliminate insects at home")
                .duty(cleaningAndHygiene).build();
    }

    @DisplayName("JUnit test for saveUnderDuty method")
    @Test
    @Order(1)
    void save_under_duty() {
        UnderDuty savedKitchenAppliances = underDutyService.save(kitchenAppliances);
        UnderDuty savedHouseSpraying = underDutyService.save(houseSpraying);

        assertThat(savedKitchenAppliances).isNotNull();
        assertThat(savedHouseSpraying).isNotNull();
    }

    @DisplayName("JUnit test for saveUnderDuty method with exist underDuty")
    @Test
    @Order(2)
    void save_under_duty_with_exist_name() {
        assertThatThrownBy(() -> underDutyService.save(kitchenAppliances))
                .isInstanceOf(UnderDutyExistException.class)
                .hasMessageContaining("an under duty already exists with this name in this duty.");
        assertThatThrownBy(() -> underDutyService.save(houseSpraying))
                .isInstanceOf(UnderDutyExistException.class)
                .hasMessageContaining("an under duty already exists with this name in this duty.");
    }

    @DisplayName("JUnit test for findAll by duty id method")
    @Test
    @Order(3)
    void findAll_by_duty_id() {
        List<UnderDuty> underDuties = underDutyService.findAllByDutyId(2L);

        assertThat(underDuties).isNotEmpty();
    }

    @DisplayName("JUnit test for findById method")
    @Test
    @Order(4)
    void findById() {
        UnderDuty underDuty = underDutyService.findById(1);

        assertThat(underDuty).isNotNull();

        assertThatThrownBy(() -> underDutyService.findById(234))
                .isInstanceOf(UnderDutyNotFoundException.class)
                .hasMessageContaining("no under duty found with this ID.");
    }

    @DisplayName("JUnit test for update method")
    @Test
    @Order(5)
    void update_base_price_and_explanation() {
        UnderDuty foundedKitchenAppliances = underDutyService.findById(1);
        foundedKitchenAppliances.setBasePrice(650_000L);

        UnderDuty foundedHouseSpraying = underDutyService.findById(2);
        foundedHouseSpraying.setExplanation("Eliminating vermin at home");

        UnderDuty updatedKitchenAppliances = underDutyService.update(foundedKitchenAppliances);
        UnderDuty updatedHouseSpraying = underDutyService.update(foundedHouseSpraying);

        assertThat(updatedKitchenAppliances.getBasePrice()).isEqualTo(650_000L);
        assertThat(updatedHouseSpraying.getExplanation()).isEqualTo("Eliminating vermin at home");
    }
}

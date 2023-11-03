package org.example.HW19.service;

import org.example.HW19.entity.Duty;
import org.example.HW19.entity.Expert;
import org.example.HW19.entity.UnderDuty;
import org.example.HW19.entity.User;
import org.example.HW19.exceptions.ExpertNotFoundException;
import org.example.HW19.utils.ConvertUrlToByteArray;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.example.HW19.entity.enumeration.ExpertStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExpertServiceTest {

    @Autowired
    private ExpertService expertService;

    @Autowired
    private UnderDutyService underDutyService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConvertUrlToByteArray convertUrlToByteArray;

    @Autowired
    private DutyService dutyService;

    private Expert expert;

    @BeforeEach
    void setUp() throws IOException {
        //User user = userService.findById(2);

        expert = Expert.builder()
                .status(NEW)
                .imageUrl(convertUrlToByteArray.convertToByteArray("C:\\Users\\Sarai\\Pictures\\11-04-2022 16.52_1_2.jpg"))
                .validity(100_000L)
                .score(0)
                .build();
    }

    @DisplayName("JUnit test for saveExpert method.")
    @Test
    @Order(1)
    void save_expert() {
        Expert newExpert = expertService.save(expert);

        assertThat(newExpert).isNotNull();
    }

    @DisplayName("JUnit test for findAll method")
    @Test
    @Order(2)
    void findAll_new_expert() {
        List<Expert> experts = expertService.findAllByStatus(NEW);

        assertThat(experts).isNotEmpty();
    }

    @DisplayName("JUnit test for findById method")
    @Test
    @Order(3)
    void findById() {
        Expert foundedExpert = expertService.findById(1L);

        assertThat(foundedExpert).isNotNull();

        assertThatThrownBy(() -> expertService.findById(123L))
                .isInstanceOf(ExpertNotFoundException.class)
                .hasMessageContaining("no expert found with this ID.");
    }

    @DisplayName("JUnit test for saveImage method")
    @Test
    @Order(4)
    void saveExpertImage() throws IOException {
        User savedExpertUser = userService.register(User.builder().email("sk@gmail.com").lastname("11-04-2022 16.52_1_2").build());
        expert.setUser(savedExpertUser);
        expertService.saveExpertImage(expert.getUser().getEmail());

        Path path = Paths.get("C:\\Users\\Sarai\\Documents\\HW19\\src\\main\\java\\org\\example\\HW19\\images\\" + expert.getUser().getLastname() + ".jpg");
        assertThat(path).exists();
    }

    @DisplayName("JUnit test for updating expert status method.")
    @Test
    @Order(5)
    void update_expert_status() {
        Expert foundedExpert = expertService.findById(1L);
        foundedExpert.setStatus(ACCEPTED);
        Expert updatedExpert = expertService.update(foundedExpert);

        assertThat(updatedExpert.getStatus()).isEqualTo(ACCEPTED);
    }

    @DisplayName("JUnit test for adding expert to under duty method.")
    @Test
    @Order(6)
    void addExpertToUnderDuty() {
//        Expert foundedExpert = expertService.findById(1L);
//        UnderDuty underDuty = underDutyService.findById(1L);
        Expert savedExpertUser = expertService.save(Expert.builder().build());
//        expert.setUser(savedExpertUser);
        Duty duty = dutyService.save(Duty.builder().name("Cleaning").build());
        UnderDuty underDuty = underDutyService.save(UnderDuty.builder().name("Washing carpets").duty(duty).build());
        expertService.addExpertToUnderDuty(savedExpertUser, underDuty);

        Expert newExpert = expertService.update(savedExpertUser);
        assertThat(newExpert.getUnderDutySet()).isNotEmpty();
    }

    @DisplayName("JUnit test for removing expert from under duty method.")
    @Test
    @Order(7)
    void removeExpertFromUnderDuty() {
        Expert foundedExpert = expertService.findById(1L);
        UnderDuty underDuty = foundedExpert.getUnderDutySet().stream()
                .filter(t -> Objects.equals(t.getId(), 1L))
                .findFirst().orElse(null);
        expertService.removeExpertFromUnderDuty(foundedExpert, underDuty);

        Expert newExpert = expertService.update(foundedExpert);
        assertThat(newExpert.getUnderDutySet()).isEmpty();
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("AddExpertToUnderDutyAfterAll.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package org.example.HW19.service;

import org.example.HW19.entity.*;
import org.example.HW19.exceptions.DateAndTimeException;
import org.example.HW19.exceptions.LessProposedPriceException;
import org.example.HW19.exceptions.OrderNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;


import static org.example.HW19.entity.enumeration.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrdersServiceTest {


    @BeforeAll
    static void setUp(@Autowired DataSource dataSource,@Autowired  ExpertService expertService,
                      @Autowired CustomerService customerService, @Autowired DutyService dutyService,
                      @Autowired UnderDutyService underDutyService) {
        Expert expert = expertService.save(Expert.builder().build());
        Customer customer = customerService.save(Customer.builder().build());
        Duty duty = dutyService.save(Duty.builder().build());
        UnderDuty underDuty = underDutyService.save(UnderDuty.builder().basePrice(5000L).duty(duty).build());
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("AddOffersForOrdersBeforeAll.sql"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UnderDutyService underDutyService;

    @Autowired
    private OffersService offersService;

    private Orders orders;

    private Orders ordersWithLessProposedPrice;

    private Orders ordersWithInvalidTime;

    @BeforeEach
    void setUp() {
        Customer customer = customerService.findById(1L);
        UnderDuty underDuty = underDutyService.findById(1L);
        orders = Orders.builder()
                .customer(customer)
                .proposedPrice(75000L)
                .description("Repairing a vacuum cleaner.")
                .dateAndTime(LocalDateTime.of(2024,10,20,11,15))
                .address("Fars, Shiraz")
                .orderStatus(WAITING_FOR_THE_SUGGESTION_OF_EXPERTS)
                .underDuty(underDuty)
                .build();
        ordersWithLessProposedPrice = Orders.builder()
                .customer(customer)
                .proposedPrice(600L)
                .description("Repairing a vacuum cleaner.")
                .dateAndTime(LocalDateTime.of(2023,10,20,11,15))
                .address("Fars, Shiraz")
                .orderStatus(WAITING_FOR_THE_SUGGESTION_OF_EXPERTS)
                .underDuty(underDuty)
                .build();
        ordersWithInvalidTime = Orders.builder()
                .customer(customer)
                .proposedPrice(75000L)
                .description("Repairing a vacuum cleaner.")
                .dateAndTime(LocalDateTime.of(2023,10,20,11,15))
                .address("Fars, Shiraz")
                .orderStatus(WAITING_FOR_THE_SUGGESTION_OF_EXPERTS)
                .underDuty(underDuty)
                .build();
    }

    @DisplayName("JUnit test for saveOrder method")
    @Test
    @Order(1)
    void save_order() {
        Orders newOrder = ordersService.save(orders);

        assertThat(newOrder).isNotNull();
    }

    @DisplayName("JUnit test for saveOrder method with less proposed price")
    @Test
    @Order(2)
    void save_order_with_less_proposed_price() {
        assertThatThrownBy(() -> ordersService.save(ordersWithLessProposedPrice))
                .isInstanceOf(LessProposedPriceException.class)
                .hasMessageContaining("your bid price is lower than the base price.");
    }

    @DisplayName("JUnit test for saveOrder method with invalid date and time")
    @Test
    @Order(3)
    void save_order_with_invalid_date_time() {
        assertThatThrownBy(() -> ordersService.save(ordersWithInvalidTime))
                .isInstanceOf(DateAndTimeException.class)
                .hasMessageContaining("the entered date is less than today's date.");
    }

    @DisplayName("JUnit test for find all orders with underDutyId and status method")
    @Test
    @Order(4)
    void testFindAllByUnderDutyIdAndStatus() {
        List<Orders> ordersList = ordersService.findAllByUnderDutyIdAndStatus(1L);

        assertThat(ordersList).isNotEmpty();
    }

    @DisplayName("JUnit test for findById method")
    @Test
    @Order(5)
    void findById() {
        Orders newOrders = ordersService.findById(1L);

        assertThat(newOrders).isNotNull();

        assertThatThrownBy(() -> ordersService.findById(123L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("no order found with this ID.");
    }

    @DisplayName("JUnit test for add offers for orders method")
    @Test
    @Order(6)
    void addOffers_for_orders() {
        Orders foundedOrder = ordersService.findById(1L);
        Offers offers1 = offersService.findById(1L);
        Offers offers2 = offersService.findById(2L);
        Offers offers3 = offersService.findById(3L);

        ordersService.addOffers(foundedOrder, offers1);
        ordersService.addOffers(foundedOrder, offers2);
        ordersService.addOffers(foundedOrder, offers3);

        Orders newOrder = ordersService.update(foundedOrder);
        assertThat(newOrder.getOffersSet().size()).isEqualTo(3);
    }

    @DisplayName("JUnit test for update order status to waiting for specialist selection method")
    @Test
    @Order(7)
    void update_order_status_to_waiting_for_specialist_selection() {
        Orders foundedOrder = ordersService.findById(1L);
        foundedOrder.setOrderStatus(WAITING_FOR_SPECIALIST_SELECTION);

        Orders newOrder = ordersService.update(foundedOrder);
        assertThat(newOrder.getOrderStatus()).isEqualTo(WAITING_FOR_SPECIALIST_SELECTION);
    }

    @DisplayName("JUnit test for find order from order list method")
    @Test
    @Order(8)
    void findOrderInOrdersList() {
        Orders foundedOrder = ordersService.findOrderInOrdersList(1L, 1L);

        assertThat(foundedOrder.getProposedPrice()).isEqualTo(75000L);
    }

    @DisplayName("JUnit test for select offer method")
    @Test
    @Order(9)
    void selectOfferForOrder() {
        Orders newOrder = ordersService.selectOfferForOrder(1L, 1L);

        assertThat(newOrder.getExpert().getId()).isEqualTo(1L);
    }

    @DisplayName("JUnit test for update order status to waiting for the specialist to come to your place method")
    @Test
    @Order(10)
    void update_order_status_to_waiting_for_the_specialist_to_come_to_your_place() {
        Orders foundedOrder = ordersService.findById(1L);
        foundedOrder.setOrderStatus(WAITING_FOR_THE_SPECIALIST_TO_COME_TO_YOUR_PLACE);

        Orders newOrder = ordersService.update(foundedOrder);
        assertThat(newOrder.getOrderStatus()).isEqualTo(WAITING_FOR_THE_SPECIALIST_TO_COME_TO_YOUR_PLACE);
    }

    @DisplayName("JUnit test for update order status to started method")
    @Test
    @Order(11)
    void update_order_status_to_started() {
        assertThatThrownBy(() -> ordersService.updateOrderStatusToStarted(1L, 1L))
                .isInstanceOf(DateAndTimeException.class)
                .hasMessageContaining("You cannot change the status of a task to 'Started' before it starts.");
    }

    @DisplayName("JUnit test for update order status to done method")
    @Test
    @Order(12)
    void update_order_status_to_done() {
        assertThatThrownBy(() -> ordersService.updateOrderStatusToDone(1L, 1L))
                .isInstanceOf(DateAndTimeException.class)
                .hasMessageContaining("You cannot change the status of a task to 'Done' before it starts.");
    }
}

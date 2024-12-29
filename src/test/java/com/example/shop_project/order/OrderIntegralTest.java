package com.example.shop_project.order;

import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Transactional  // data commit 전에 rollback 실행으로 실제 데이터 반영이 안됨
@ActiveProfiles("dev")
@WithMockUser(roles = "USER", username = "test@example.com")
public class OrderIntegralTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private static final OrderTestEntity testEntity = new OrderTestEntity();

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("주문 생성하기")
    void createOrderList() throws Exception{
        for (int i = 0; i < 10000; i++) {
            orderService.createOrder(testEntity.orderRequest());
        }

        assertEquals(10000, orderRepository.count());
    }

    @Test
    @DisplayName("주문 목록 가져오기")
    void getOrderList() throws Exception {
        // given
        Principal principal = () -> "test@example.com";

        ResultActions resultActions = mockMvc.perform(get("/order")
                .param("page", "0")
                .principal(principal)
        );

        resultActions.andExpect(status().isOk());

        assertEquals(5050062, orderRepository.count());
    }

    // 그냥 52초


    @Test
    @DisplayName("주문 상태 변경")
    void updateOrderStatus() throws Exception {
        OrderStatus orderStatus = OrderStatus.IN_SHIPPING;

    }
}

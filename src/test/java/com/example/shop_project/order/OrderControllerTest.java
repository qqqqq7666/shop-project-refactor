package com.example.shop_project.order;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.order.controller.OrderAPIController;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderDetail;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(value = {OrderAPIController.class})
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser(roles = "USER", username = "username")
@Import(SecurityConfig.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private OrderService orderService;
    @MockitoBean
    private MemberService memberService;

    private final OrderTestEntity testEntity = new OrderTestEntity();

    @Test
    @DisplayName("주문 조회 테스트")
    void retrieveOrder() throws Exception {
        // given
        Order order = testEntity.order();
        OrderResponseDto orderResponseDto = testEntity.orderResponseDto();
        given(orderService.getOrderByOrderNo(order.getOrderNo()))
                .willReturn(orderResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/order/" + order.getOrderNo())
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNo").value(orderResponseDto.getOrderNo()))
                .andDo(print());
    }

    @Test
    @DisplayName("주문 상세 조회 테스트")
    void retrieveOrderDetail() throws Exception {
        Order order = testEntity.order();
        OrderDetail orderDetail = testEntity.orderDetail();

        given(orderService.getOrderDetailList(order.getOrderNo()))
                .willReturn(Arrays.asList(orderDetail));
        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/order/1/order-detail")
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderDetailId").value(1L));
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrder() throws Exception {
        // given
        OrderRequestDto orderRequestDto = testEntity.orderRequest();
        OrderResponseDto orderResponseDto = testEntity.orderResponseDto();
        String json = objectMapper.writeValueAsString(orderRequestDto);

        given(orderService.createOrder(orderRequestDto))
                .willReturn(orderResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf())
        );

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNo").value(1L));
    }

    @Test
    @DisplayName("주문삭제 수정 테스트")
    void deleteOrder() throws Exception{
        ResultActions resultActions = mockMvc.perform(
                delete("/api/order/1/delete")

        );

        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("주문 상태 수정 테스트")
    void updateOrderStatus() throws Exception {
        Long orderNo = 1L;
        OrderStatus orderStatus = OrderStatus.IN_SHIPPING;
        OrderResponseDto orderResponseDto = testEntity.orderResponseDto();

        class TestObject{
            TestObject(OrderStatus orderStatus){this.orderStatus = orderStatus;}
            public OrderStatus orderStatus;
        }

        TestObject testObject = new TestObject(orderStatus);

        String json = objectMapper.writeValueAsString(testObject);
        log.info(json);

        given(orderService.updateOrderStatus(orderNo, orderStatus))
                .willReturn(orderResponseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                patch("/api/order/1/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        // then
        resultActions
                .andExpect(status().isCreated());


    }

    @Test
    @DisplayName("회원 주소 조회 테스트")
    void retrieveMemberAddress() throws Exception{
        Member member = testEntity.member();

        given(memberService.findByEmail("username"))
                .willReturn(member);

        ResultActions resultActions = mockMvc.perform(
                get("/api/order/member-info")
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("가장 최신의 주문번호 조회")
    void recentOrderNo() throws Exception {
        Order order = testEntity.order();
        given(orderService.getRecentOrder())
                .willReturn(order);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/order/recent-order-no")
        );

        // then
        resultActions
                .andExpect(status().isOk());
    }

}

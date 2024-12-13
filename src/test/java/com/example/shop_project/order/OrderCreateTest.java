package com.example.shop_project.order;

import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.entity.PayMethod;
import com.example.shop_project.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderCreateTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 생성 테스트")
    void addOrder() throws Exception {
        //given
        final List<OrderDetailDto> dtoList = new ArrayList<>();
        dtoList.add(OrderDetailDto.builder()
                .productId(1L)
                .price(2000L)
                .quantity(3L)
                .build());
        final OrderRequestDto request = OrderRequestDto.builder()
                .orderStatus(OrderStatus.NEW)
                .address("test")
                .addressDetail("test")
                .postNo("12345")
                .payMethod(PayMethod.CARD)
                .requirement("")
                .totalPrice(30L)
                .detailDtoList(dtoList)
                .build();

        final String json = objectMapper.writeValueAsString(request);

        //when
        ResultActions result = mockMvc.perform(
                post("/api/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );

        //then
        result.andExpect(
                status().isCreated()
        )
                .andExpect(jsonPath("$[0].orderId").value(1));
    }

}

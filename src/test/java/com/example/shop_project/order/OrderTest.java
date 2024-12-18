package com.example.shop_project.order;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.entity.Order;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class OrderTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 상태 수정 테스트")
    void 주문상태수정() throws Exception {
        //given when
        OrderStatus orderStatus = OrderStatus.IN_SHIPPING;

        ResultActions result = mockMvc.perform(
                patch("/api/order/21/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderStatus\": \"배송중\"}")
                        .param("orderNo", String.valueOf(21L))
        );

        //then
        log.info(result.andReturn().getResponse().getContentAsString());
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderStatus").value(orderStatus));
    }

    @Test
    @DisplayName("결제 테스트")
    void 결제테스트() throws Exception {
        // given
        OrderTestEntity testEntity = new OrderTestEntity();
        Gson gson = testEntity.gson();
        OrderRequestDto requestDto = testEntity.orderRequest();

        String json = objectMapper.writeValueAsString(requestDto);

        ResultActions resultActions = mockMvc.perform(
                post("/api/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated());
    }



}

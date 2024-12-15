package com.example.shop_project.order;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.entity.OrderStatus;
import com.example.shop_project.order.entity.PayMethod;
import com.example.shop_project.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @DisplayName("주문 생성 테스트")
    public void 주문생성() throws Exception {

        //given
        final List<OrderDetailDto> dtoList = new ArrayList<>();
        OrderDetailDto detailDto = OrderDetailDto.builder()
                .productId(1L)
                .price(2000L)
                .quantity(3L)
                .build();
        dtoList.add(detailDto);
        MemberRequestDTO requestDTO = new MemberRequestDTO();
        requestDTO.setEmail("test@example.com");
        requestDTO.setNickname("tester");
        requestDTO.setPassword("ValidPass1!");
        requestDTO.setConfirmPassword("ValidPass1!");
        requestDTO.setPhone("010-1234-5678");
        requestDTO.setPostNo("12345");
        requestDTO.setAddress("서울시 중구");
        requestDTO.setAddressDetail("상세주소");

        final OrderRequestDto request = OrderRequestDto.builder()
                .orderStatus(OrderStatus.NEW)
                .address("test")
                .addressDetail("test")
                .postNo("12345")
                .payMethod(PayMethod.CARD)
                .requirement("")
                .totalPrice(30L)
                .detailDtoList(dtoList)
                .memberRequestDTO(requestDTO)
                .build();

        final String json = objectMapper.writeValueAsString(request);
        log.info("json = {}",json);

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
                .andExpect(jsonPath("$.postNo").value("12345"))
                .andExpect(jsonPath("$.address").value("test"));
    }

    @Test
    @DisplayName("주문 상태 수정 테스트")
    void 주문상태수정() throws Exception {
        //given when
        ResultActions result = mockMvc.perform(
                patch("/api/order/4/update-status")
                        .param("orderNo", "4")
                        .param("orderStatus", "IN_SHIPPING")
        );

        //then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderStatus").value("IN_SHIPPING"));
    }

}

package com.example.shop_project.order;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.payment.dto.PaymentDto;
import com.example.shop_project.order.entity.*;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.payment.repository.PaymentRepository;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.payment.service.PaymentService;
import com.example.shop_project.payment.entity.Payment;
import com.example.shop_project.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private MemberRepository memberRepository;

    private final OrderTestEntity testEntity = new OrderTestEntity();

    @Test
    @DisplayName("결제 성공 테스트")
    void checkoutSuccess() throws Exception {
        // given
        Order order = testEntity.order();
        Payment payment = testEntity.payment();

        when(paymentRepository.save(payment)).thenReturn(payment);
        when(orderRepository.findFirstByOrderByOrderNoDesc()).thenReturn(Optional.ofNullable(order));
        when(orderMapper.toEntity(any(PaymentDto.class))).thenReturn(payment);

        // when
        Payment response = paymentService.createPayment(testEntity.paymentDto());

        //then
        assertNotNull(response);
        assertEquals(response.getPaymentId(), 1L);
    }

    @Test
    @DisplayName("주문별 결제내역 조회")
    void retrievePayment() throws Exception {
        // given
        Order order = testEntity.order();
        Payment payment = testEntity.payment();

//        doReturn(Optional.of(order)).when(orderRepository.findByOrderNo(1L));
        when(orderRepository.findByOrderNo(order.getOrderNo())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));

        // when
        Payment response = paymentService.getPaymentByOrderNo(1L);

        // then
        assertNotNull(response);
        assertEquals(response.getPaymentId(), 1L);
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrder() throws Exception {

        // given
        OrderRequestDto requestDto = testEntity.orderRequest();
        Order order = testEntity.order();
        OrderDetail orderDetail = testEntity.orderDetail();
        OrderDetailDto orderDetailDto = testEntity.detailDto();
        OrderResponseDto responseDto = testEntity.orderResponseDto();

        when(orderMapper.toEntity(any(OrderRequestDto.class), any(ProductRepository.class))).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
//        when(orderMapper.toEntity(any(OrderDetailDto.class), any(ProductRepository.class))).thenReturn(orderDetail);
//        when(productRepository.findById(orderDetailDto.getProductId())).thenReturn(Optional.of(testEntity.product()));
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);


        // when
        OrderResponseDto response = orderService.createOrder(requestDto);

        // then
        assertNotNull(response);
    }

    @Test
    @DisplayName("주문상태 수정 테스트")
    void updateOrderStatus() throws Exception {
        Order order = testEntity.order();
        OrderStatus updatedStatus = OrderStatus.IN_SHIPPING;
        OrderResponseDto responseDto = OrderResponseDto.builder()
                .orderStatus(updatedStatus)
                .build();

        when(orderRepository.findByOrderNo(order.getOrderNo())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);

        // when
        OrderResponseDto response = orderService.updateOrderStatus(order.getOrderNo(), updatedStatus);

        // then
        assertNotNull(response);
        assertEquals(response.getOrderStatus(), updatedStatus);
    }

    @Test
    @DisplayName("주문상세 목록 조회 테스트")
    void retrieveOrderDetail() throws Exception {
        // given
        Long orderNo = 1L;
        Order order = testEntity.order();
        List<OrderDetail> detailList = Arrays.asList(testEntity.orderDetail());

        when(orderRepository.findByOrderNo(orderNo)).thenReturn(Optional.of(order));
        when(orderDetailRepository.findAllByOrder(order)).thenReturn(detailList);

        // when
        List<OrderDetail> response = orderService.getOrderDetailList(orderNo);

        // then
        assertNotNull(response);
        assertEquals(response.get(0).getOrderDetailId(), 1L);
    }

    @Test
    @DisplayName("주문, 주문상세 조회 테스트")
    void retrieveOrderMap() throws Exception {
        // given
        Order order = testEntity.order();
        Member member = testEntity.member();
        OrderResponseDto orderResponseDto = testEntity.orderResponseDto();
        List<Order> orderList = Collections.singletonList(order);
        List<OrderDetail> orderDetailList = Collections.singletonList(testEntity.orderDetail());
        Pageable pageable = PageRequest.of(0, 10);
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return member.getEmail();
            }
        };
        Page<Order> orderPage = new PageImpl<>(orderList);

        when(memberRepository.findByEmail(principal.getName())).thenReturn(Optional.of(member));
        when(orderRepository.findAllByMemberAndOrderStatusNotAndOrderDetailListProductProductNameContainingOrderByOrderNoDesc(member, OrderStatus.FAIL, "test", pageable))
                .thenReturn(orderPage);
        when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);
//        when(orderDetailRepository.findAllByOrder(order)).thenReturn(orderDetailList);

        // when
        Page<OrderResponseDto> response = orderService.getOrderPageList(principal, pageable, "test");

        // then
        assertFalse(response.isEmpty());
    }

    @Test
    @DisplayName("전체 주문 조회 테스트")
    void retrieveAllOrder() throws Exception {
        // given
        OrderResponseDto orderResponseDto = testEntity.orderResponseDto();
        Order order = testEntity.order();

        when(orderRepository.findAllByOrderByOrderNoDesc()).thenReturn(Arrays.asList(order));
        when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);

        // when
        List<OrderResponseDto> response = orderService.getOrderList();

        // then
        assertEquals(response.get(0).getOrderNo(), orderResponseDto.getOrderNo());
    }

    @Test
    @DisplayName("주문 조회 테스트")
    void retrieveOrder() throws Exception{
        Long orderNo = 1L;
        Order order = testEntity.order();
        OrderResponseDto orderResponseDto = testEntity.orderResponseDto();

        when(orderMapper.toResponseDto(order)).thenReturn(orderResponseDto);
        when(orderRepository.findByOrderNo(orderNo)).thenReturn(Optional.of(order));

        // when
        OrderResponseDto response = orderService.getOrderByOrderNo(orderNo);

        // then
        assertEquals(response.getOrderNo(), orderNo);
    }

    @Test
    @DisplayName("주문 삭제 테스트")
    void deleteOrder() throws Exception {
        // given
        Order order = testEntity.order();

        when(orderRepository.findByOrderNo(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderDetailRepository).deleteByOrder(order);
        doNothing().when(paymentRepository).deleteByOrder(order);
        doNothing().when(orderRepository).deleteByOrderNo(order.getOrderNo());

        // when
        orderService.deleteOrder(order.getOrderNo());

        // verify
        verify(orderDetailRepository, times(1)).deleteByOrder(any(Order.class));
        verify(paymentRepository, times(1)).deleteByOrder(any(Order.class));
        verify(orderRepository, times(1)).deleteByOrderNo(anyLong());
    }

    @Test
    @DisplayName("최근 주문 조회 성공 테스트")
    void retrieveRecentOrder() throws Exception{
        //given
        Order order = testEntity.order();

        when(orderRepository.findFirstByOrderByOrderNoDesc()).thenReturn(Optional.of(order));

        //when
        Order response = orderService.getRecentOrder();

        //then
        assertEquals(1L, response.getOrderNo());
    }

    @Test
    @DisplayName("최근 주문 조회 첫주문 테스트")
    void retrieveRecentOrderFail() throws Exception{
        //given
        Order order = testEntity.order();

        when(orderRepository.findFirstByOrderByOrderNoDesc()).thenReturn(Optional.empty());

        //when
        Order response = orderService.getRecentOrder();

        assertEquals(0L, response.getOrderNo());
    }

    @Test
    @DisplayName("환불 주문 생성 테스트")
    void createCanceledOrder() throws Exception {
        //given
        Order order = testEntity.order();

        when(orderRepository.findByOrderNo(1L)).thenReturn(Optional.of(order));
    }
}

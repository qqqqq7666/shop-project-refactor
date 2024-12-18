package com.example.shop_project.order;

import com.example.shop_project.member.Membership;
import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.controller.OrderAPIController;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.order.dto.PaymentDto;
import com.example.shop_project.order.entity.*;
import com.example.shop_project.order.mapper.OrderMapper;
import com.example.shop_project.order.repository.OrderDetailRepository;
import com.example.shop_project.order.repository.OrderRepository;
import com.example.shop_project.order.repository.PaymentRepository;
import com.example.shop_project.order.service.OrderService;
import com.example.shop_project.order.service.PaymentService;
import com.example.shop_project.product.entity.Product;
import com.example.shop_project.product.repository.ProductRepository;
import com.nimbusds.jose.shaded.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class OrderUnitTest {
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

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(OrderAPIController.class).build();
    }

    private Order order(){
        return Order.builder()
                .orderNo(1L)
                .totalPrice(3000L)
                .postNo("12345")
                .requirement("test 요청사항")
                .orderStatus(OrderStatus.NEW)
                .address("test 주소")
                .member(member())
                .addressDetail("test addressDetail")
                .contact("test contact")
                .addressee("test addressee")
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    private OrderRequestDto orderRequest() {
        List<OrderDetailDto> detailDtoList = new ArrayList<>();
        detailDtoList.add(detailDto());
        return OrderRequestDto.builder()
                .totalPrice(3000L)
                .postNo("12345")
                .requirement("test 요청사항")
                .orderStatus(OrderStatus.NEW)
                .address("test 주소")
                .member(member())
                .addressDetail("test addressDetail")
                .detailDtoList(detailDtoList)
                .contact("test contact")
                .addressee("test addressee")
                .build();
    }

    private OrderResponseDto orderResponseDto() {
        return OrderResponseDto.builder()
                .orderNo(1L)
                .totalPrice(3000L)
                .postNo("12345")
                .requirement("test 요청사항")
                .orderStatus(OrderStatus.NEW)
                .address("test 주소")
                .member(member())
                .addressDetail("test addressDetail")
                .contact("test contact")
                .addressee("test addressee")
                .createdDate(LocalDateTime.now())
                .build();
    }

    private OrderDetailDto detailDto(){
        return OrderDetailDto.builder()
                .price(3000L)
                .productId(1L)
                .quantity(1L)
                .build();
    }

    private OrderDetail orderDetail(){
        return OrderDetail.builder()
                .orderDetailId(1L)
                .order(order())
                .price(3000L)
                .product(product())
                .quantity(3000L)
                .build();
    }

    private Member member(){
        return new Member(
                1L,
                "test@test.com",
                "testPassword",
                "testName",
                "testPhone",
                "testPostNo",
                "testAddress",
                "testAddressDetail",
                "testNickname",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "testProfileImage",
                Role.USER,
                Membership.BRONZE,
                false);
    }

    private PaymentDto paymentDto() {
        return PaymentDto.builder()
                .amount(3000L)
                .memberName(member().getName())
                .payMethod(PayMethod.KG)
                .payStatus(PayStatus.SUCCESS)
                .build();
    }

    private Payment payment(){
        return Payment.builder()
                .paymentId(1L)
                .amount(3000L)
                .memberName(member().getName())
                .payMethod(PayMethod.KG)
                .payStatus(PayStatus.SUCCESS)
                .order(order())
                .build();
    }

    private Product product(){
        return Product.builder()
                .productId(1L)
                .productName("test productName")
                .categoryName("test categoryName")
                .description("test description")
                .build();
    }

    class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(localDateTime));
        }
    }

    class LocalDateTimeDeserializer implements JsonDeserializer <LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.KOREA));
        }
    }

    private Gson gson(){
        Gson gson;

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();

        return gson;
    }



    @Test
    @DisplayName("결제 생성 테스트")
    void 결제생성테스트() throws Exception {
        // given
        Order order = order();
        Payment payment = payment();

        when(paymentRepository.save(payment)).thenReturn(payment);
        when(orderRepository.findFirstByOrderByOrderNoDesc()).thenReturn(Optional.ofNullable(order));
        when(orderMapper.toEntity(any(PaymentDto.class))).thenReturn(payment);

        // when
        Payment response = paymentService.createPayment(paymentDto());

        //then
        assertNotNull(response);
        assertEquals(response.getPaymentId(), 1L);
    }

    @Test
    @DisplayName("주문별 결제내역 조회")
    void 주문별결제내역조회() throws Exception {
        // given
        Order order = order();
        Payment payment = payment();

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
    void 주문생성() throws Exception {

        // given
        OrderRequestDto requestDto = orderRequest();
        Order order = order();
        OrderDetail orderDetail = orderDetail();
        OrderDetailDto orderDetailDto = detailDto();
        OrderResponseDto responseDto = orderResponseDto();

        when(orderMapper.toEntity(any(OrderRequestDto.class))).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toEntity(any(OrderDetailDto.class))).thenReturn(orderDetail);
        when(productRepository.findById(orderDetailDto.getProductId())).thenReturn(Optional.of(product()));
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);


        // when
        OrderResponseDto response = orderService.createOrder(requestDto);

        // then
        assertNotNull(response);
    }

    @Test
    @DisplayName("주문상태 수정 테스트")
    void 주문상태수정() throws Exception {
        Order order = order();
        OrderResponseDto responseDto = orderResponseDto();

        when(orderRepository.findByOrderNo(order.getOrderNo())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);

        // when
        OrderResponseDto response = orderService.updateOrderStatus(order().getOrderNo(), OrderStatus.IN_SHIPPING);

        // then
        assertNotNull(response);
        assertEquals(response.getOrderStatus(), OrderStatus.IN_SHIPPING);
    }
}

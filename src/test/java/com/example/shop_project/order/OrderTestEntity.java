package com.example.shop_project.order;

import com.example.shop_project.member.Membership;
import com.example.shop_project.member.Provider;
import com.example.shop_project.member.Role;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.order.dto.OrderDetailDto;
import com.example.shop_project.order.dto.OrderRequestDto;
import com.example.shop_project.order.dto.OrderResponseDto;
import com.example.shop_project.payment.dto.PaymentDto;
import com.example.shop_project.order.entity.*;
import com.example.shop_project.payment.entity.PayMethod;
import com.example.shop_project.payment.entity.PayStatus;
import com.example.shop_project.payment.entity.Payment;
import com.example.shop_project.product.entity.Product;
import com.nimbusds.jose.shaded.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderTestEntity {
    Order order(){
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail = OrderDetail.builder()
                .orderDetailId(1L)
                .price(3000L)
                .product(product())
                .quantity(3000L)
                .build();
        orderDetailList.add(orderDetail);
        Order order = Order.builder()
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
                .orderDetailList(orderDetailList)
                .build();
        order.assignOrderToOrderDetail();
        return order;
    }

    Order anotherOrder(){
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail = OrderDetail.builder()
                .orderDetailId(1L)
                .order(order())
                .price(3000L)
                .product(product())
                .quantity(3000L)
                .build();
        orderDetailList.add(orderDetail);
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
                .orderDetailList(orderDetailList)
                .build();
    }

    OrderRequestDto orderRequest() {
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
                .orderDetailDtoList(detailDtoList)
                .contact("test contact")
                .addressee("test addressee")
                .isPaidWithPoint(false)
                .build();
    }

    OrderResponseDto orderResponseDto() {
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

    OrderDetailDto detailDto(){
        return OrderDetailDto.builder()
                .price(3000L)
                .productId(2L)
                .quantity(1L)
                .build();
    }

    OrderDetail orderDetail(){
        Order order = order();
        return OrderDetail.builder()
                .orderDetailId(1L)
                .order(order())
                .price(3000L)
                .product(product())
                .quantity(3000L)
                .build();
    }

    Member member(){
        return new Member(
                2L,
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
                false, Provider.NONE);
    }

    PaymentDto paymentDto() {
        return PaymentDto.builder()
                .amount(3000L)
                .memberName(member().getName())
                .payMethod(PayMethod.KG)
                .payStatus(PayStatus.SUCCESS)
                .build();
    }

    Payment payment(){
        return Payment.builder()
                .paymentId(1L)
                .amount(3000L)
                .memberName(member().getName())
                .payMethod(PayMethod.KG)
                .payStatus(PayStatus.SUCCESS)
                .order(order())
                .build();
    }

    Product product(){
        return Product.builder()
                .productId(2L)
                .productName("test productName")
                .description("test description")
                .build();
    }

    Gson gson(){
        Gson gson;

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        gson = gsonBuilder.setPrettyPrinting().create();

        return gson;
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
}

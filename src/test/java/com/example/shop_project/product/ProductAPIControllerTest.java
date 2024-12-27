package com.example.shop_project.product;

import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.service.MemberService;
import com.example.shop_project.product.controller.ProductAPIController;
import com.example.shop_project.product.dto.ProductOptionDto;
import com.example.shop_project.product.dto.ProductRequestDto;
import com.example.shop_project.product.dto.ProductResponseDto;
import com.example.shop_project.product.entity.Size;
import com.example.shop_project.product.service.ProductService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProductAPIControllerTest {
    @InjectMocks
    private ProductAPIController productController;

    @Mock
    private ProductService productService;

    @Mock
    private MemberService memberService;


    private final ProductTestEntity productTestEntity = new ProductTestEntity();
    private Principal principal;
    private ProductRequestDto productRequestDto;
    private List<MultipartFile> mockImages;
    private ProductResponseDto productResponseDto;
    private Validator validator;

    @BeforeEach
    void setUp() {  // 기본 데이터 셋업
        // Mock ProductRequestDto
        productRequestDto = productTestEntity.prepareProductRequestDto();
        // Mock Images
        mockImages = productTestEntity.prepareMockImages();
        // Mock ProductResponseDto
        productResponseDto = productTestEntity.prepareProductResponseDto();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 상품등록_성공() {
        // given
        // Mock Principal
        principal = mock(Principal.class);
        given(principal.getName()).willReturn("test@example.com");

        // Mock BindingResult
        BindingResult mockBindingResult = mock(BindingResult.class);
        given(mockBindingResult.hasErrors()).willReturn(false);

        // Mock MemberService
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setNickname("TestNickname");
        given(memberService.findByEmail("test@example.com"))
                .willReturn(member);

        // Mock ProductService
        given(productService.createProduct(any(ProductRequestDto.class), anyList()))
                .willReturn(productResponseDto);


        // when
        ResponseEntity<?> response = productController.createProduct(
                productRequestDto,
                mockImages,
                mockBindingResult,
                principal
        );


        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());  // ResponseEntity의 상태 코드 확인(200)
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body); // 응답의 바디가 Null이 아닌지 확인
        assertEquals("상품 등록 성공", body.get("message")); // 응답 메시지가 일치하는지 확인
    }

    @Test
    void 상품등록_실패_상품명누락() {
        // given
        // 기본 셋업 dto 대신 유효성 검사에 어긋나는 dto 생성
        ProductRequestDto invalidDto = new ProductRequestDto();
        invalidDto.setProductName(""); // 상품명 누락
        invalidDto.setCategoryId(1L);
        invalidDto.setDescription("Test description");
        invalidDto.setPrice(1000);
        invalidDto.setOptions(List.of(new ProductOptionDto(Size.S, "Red", 10)));

        // Mock BindingResult
        BindingResult mockBindingResult = mock(BindingResult.class);
        FieldError productNameError = new FieldError("productRequestDto", "productName", "제품 이름은 필수 입력 항목입니다."); // 오류 설정
        List<FieldError> fieldErrors = List.of(productNameError);
        given(mockBindingResult.hasErrors()).willReturn(true); // true로 설정해서 오류 반환
        lenient().when(mockBindingResult.getFieldErrors()).thenReturn(fieldErrors); // lenient() 적용

        // Mock Principal
        Principal principal = mock(Principal.class);
        // lenient().when(principal.getName()).thenReturn("test@example.com");

        // when
        ResponseEntity<?> response = productController.createProduct(
                invalidDto,
                mockImages,
                mockBindingResult,
                principal
        );


        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 상태 코드 검사 (400)
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertNotNull(errors); // 에러 응답의 바디가 Null이 아닌지 확인
        assertTrue(errors.containsKey("productName")); // productName의 에러 코드가 들어갔는지 확인
        assertEquals("제품 이름은 필수 입력 항목입니다.", errors.get("productName")); // 오류 메시지 검증
        verify(mockBindingResult, times(1)).getFieldErrors(); // 호출 확인
    }

    @Test
    void 상품등록_실패_상품명길이초과() {
        // given
        // 기본 셋업 dto 대신 유효성 검사에 어긋나는 dto 생성
        ProductRequestDto invalidDto = new ProductRequestDto();
        invalidDto.setProductName("TestProductName TestProductName TestProductName"); // 상품명 누락
        invalidDto.setCategoryId(1L);
        invalidDto.setDescription("Test description");
        invalidDto.setPrice(1000);
        invalidDto.setOptions(List.of(new ProductOptionDto(Size.S, "Red", 10)));

        // Mock BindingResult
        BindingResult mockBindingResult = mock(BindingResult.class);
        FieldError productNameError = new FieldError("productRequestDto", "productName", "제품 이름은 최대 20자까지 가능합니다."); // 오류 설정
        List<FieldError> fieldErrors = List.of(productNameError);
        given(mockBindingResult.hasErrors()).willReturn(true); // true로 설정해서 오류 반환
        lenient().when(mockBindingResult.getFieldErrors()).thenReturn(fieldErrors); // lenient() 적용

        // Mock Principal
        Principal principal = mock(Principal.class);
        // lenient().when(principal.getName()).thenReturn("test@example.com");

        // when
        ResponseEntity<?> response = productController.createProduct(
                invalidDto,
                mockImages,
                mockBindingResult,
                principal
        );


        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 상태 코드 검사 (400)
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertNotNull(errors); // 에러 응답의 바디가 Null이 아닌지 확인
        assertTrue(errors.containsKey("productName")); // productName의 에러 코드가 들어갔는지 확인
        assertEquals("제품 이름은 최대 20자까지 가능합니다.", errors.get("productName")); // 오류 메시지 검증
        verify(mockBindingResult, times(1)).getFieldErrors(); // 호출 확인
    }

    @Test
    void 상품등록_실패_상세설명누락() {
        // given
        // 기본 셋업 dto 대신 유효성 검사에 어긋나는 dto 생성
        ProductRequestDto invalidDto = new ProductRequestDto();
        invalidDto.setProductName("Test Product");
        invalidDto.setCategoryId(1L);
        invalidDto.setDescription(""); // 상세 설명 누락
        invalidDto.setPrice(1000);
        invalidDto.setOptions(List.of(new ProductOptionDto(Size.S, "Red", 10)));

        // Mock BindingResult
        BindingResult mockBindingResult = mock(BindingResult.class);
        FieldError DescriptionError = new FieldError("productRequestDto", "description", "상세 설명은 필수 입력 항목입니다."); // 오류 설정
        List<FieldError> fieldErrors = List.of(DescriptionError);
        given(mockBindingResult.hasErrors()).willReturn(true); // true로 설정해서 오류 반환
        lenient().when(mockBindingResult.getFieldErrors()).thenReturn(fieldErrors); // lenient() 적용

        // Mock Principal
        Principal principal = mock(Principal.class);
        // lenient().when(principal.getName()).thenReturn("test@example.com");

        // when
        ResponseEntity<?> response = productController.createProduct(
                invalidDto,
                mockImages,
                mockBindingResult,
                principal
        );


        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 상태 코드 검사 (400)
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertNotNull(errors); // 에러 응답의 바디가 Null이 아닌지 확인
        assertTrue(errors.containsKey("description")); // productName의 에러 코드가 들어갔는지 확인
        assertEquals("상세 설명은 필수 입력 항목입니다.", errors.get("description")); // 오류 메시지 검증
        verify(mockBindingResult, times(1)).getFieldErrors(); // 호출 확인
    }

    @Test
    void 상품등록_실패_상세설명길이초과() {
        // given
        // 기본 셋업 dto 대신 유효성 검사에 어긋나는 dto 생성
        ProductRequestDto invalidDto = new ProductRequestDto();
        invalidDto.setProductName("Test Product");
        invalidDto.setCategoryId(1L);
        invalidDto.setDescription("TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription TestDescription"); // 상세 설명 누락
        invalidDto.setPrice(1000);
        invalidDto.setOptions(List.of(new ProductOptionDto(Size.S, "Red", 10)));

        // Mock BindingResult
        BindingResult mockBindingResult = mock(BindingResult.class);
        // FieldError DescriptionError = new FieldError("productRequestDto", "description", "상세 설명은 최대 100자까지 가능합니다."); // 오류 설정
        // List<FieldError> fieldErrors = List.of(DescriptionError);
        given(mockBindingResult.hasErrors()).willReturn(true); // true로 설정해서 오류 반환
        // lenient().when(mockBindingResult.getFieldErrors()).thenReturn(fieldErrors); // lenient() 적용

        // Mock Principal
        Principal principal = mock(Principal.class);
        // lenient().when(principal.getName()).thenReturn("test@example.com");

        // when
        ResponseEntity<?> response = productController.createProduct(
                invalidDto,
                mockImages,
                mockBindingResult,
                principal
        );


        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // 상태 코드 검사 (400)
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertNotNull(errors); // 에러 응답의 바디가 Null이 아닌지 확인
        assertTrue(errors.containsKey("description")); // productName의 에러 코드가 들어갔는지 확인
        assertEquals("상세 설명은 최대 100자까지 가능합니다.", errors.get("description")); // 오류 메시지 검증
        verify(mockBindingResult, times(1)).getFieldErrors(); // 호출 확인
    }




}

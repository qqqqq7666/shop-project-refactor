package com.example.shop_project.member;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class MemberGlobalExceptionHandler  {
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex, Model model) {
        // 유효성 검사 실패 메시지 가져오기
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        model.addAttribute("error", errorMessage);

        // 다시 회원가입 페이지로 이동
        return "member/join";
    }
}

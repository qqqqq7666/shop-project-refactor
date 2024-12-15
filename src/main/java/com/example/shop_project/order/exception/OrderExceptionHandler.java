package com.example.shop_project.order.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderExceptionHandler{
    @ExceptionHandler
    protected String errorPageHandle(Exception e){
        return "order/error";
    }
}

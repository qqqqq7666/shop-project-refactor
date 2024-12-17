package com.example.shop_project.order.exception;

import com.example.shop_project.order.controller.OrderAPIController;
import com.example.shop_project.order.controller.OrderViewController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {OrderViewController.class, OrderAPIController.class})
@Slf4j
public class OrderExceptionHandler{
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<String> errorPageHandle(Exception e){
        log.info("OrderAPIController에서 NullPointerException 발생! {}", e.getMessage());
        return ResponseEntity.status(403).body(e.getMessage());
    }
}

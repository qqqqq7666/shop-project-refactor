package com.example.shop_project.order.exception;

import com.example.shop_project.order.controller.OrderAPIController;
import com.example.shop_project.order.controller.OrderViewController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = {OrderAPIController.class, OrderViewController.class})
public class OrderExceptionHandler{
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<String> errorPageHandle(Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

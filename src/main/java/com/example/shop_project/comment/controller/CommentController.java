package com.example.shop_project.comment.controller;

import com.example.shop_project.comment.entity.CommentRequestDto;
import com.example.shop_project.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products/{productId}/inquiries/{inquiryId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public String createComment(
            @PathVariable Long inquiryId,
            @PathVariable Long productId,
            @ModelAttribute CommentRequestDto requestDto) {

        String nickname = SecurityContextHolder.getContext().getAuthentication().getName();

        commentService.createComment(inquiryId, requestDto, nickname);

        return "redirect:/products/" + productId + "/inquiries/" + inquiryId;
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable Long productId,
                                @PathVariable Long inquiryId,
                                @PathVariable Long commentId) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        commentService.deleteComment(commentId, userEmail);

        return "redirect:/products/" + productId + "/inquiries/" + inquiryId;
    }
}

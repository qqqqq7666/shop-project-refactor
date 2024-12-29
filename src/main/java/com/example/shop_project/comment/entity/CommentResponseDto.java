package com.example.shop_project.comment.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String member;

    public CommentResponseDto(Long id, String content, String member) {
        this.id = id;
        this.content = content;
        this.member = member;
    }
}
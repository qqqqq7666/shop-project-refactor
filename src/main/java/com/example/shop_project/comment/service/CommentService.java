package com.example.shop_project.comment.service;

import com.example.shop_project.comment.entity.Comment;
import com.example.shop_project.comment.entity.CommentRequestDto;
import com.example.shop_project.comment.entity.CommentResponseDto;
import com.example.shop_project.comment.repository.CommentRepository;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.repository.InquiryRepository;
import com.example.shop_project.member.entity.Member;
import com.example.shop_project.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    // 댓글 작성
    public CommentResponseDto createComment(Long inquiryId, CommentRequestDto requestDto, String userEmail) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 문의를 찾을 수 없습니다: " + inquiryId));

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보를 찾을 수 없습니다: " + userEmail));

        // 관리자 권한 확인
        if (!member.getRole().getKey().equals("ROLE_ADMIN")) {
            throw new IllegalStateException("댓글 작성 권한이 없습니다.");
        }

        Comment comment = new Comment();
        comment.setInquiry(inquiry);
        comment.setContent(requestDto.getContent());
        comment.setMember(member);

        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment.getId(), savedComment.getContent(), member.getNickname());
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        // 현재 로그인된 사용자가 댓글 작성자인지 확인
        if (!comment.getMember().getEmail().equals(userEmail)) {
            throw new IllegalStateException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}

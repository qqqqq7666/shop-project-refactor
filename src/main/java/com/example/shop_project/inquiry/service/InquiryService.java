package com.example.shop_project.inquiry.service;

import com.example.shop_project.inquiry.entity.InquiryRequestDto;
import com.example.shop_project.inquiry.entity.Inquiry;
import com.example.shop_project.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    // 문의 생성
    public Inquiry createInquiry(InquiryRequestDto dto) {
        Inquiry inquiry = Inquiry.builder()
                .productId(dto.getProductId())
                .nickname(dto.getNickname())
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType()) // 유형 설정
                .date(LocalDate.now())  // 생성 시 현재 날짜 추가
                .build();
        return inquiryRepository.save(inquiry);
    }

    // 문의 개별 조회
    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 문의를 찾을 수 없습니다: " + id));
    }

    // 문의 목록 조회
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    // 문의 삭제
    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 문의를 찾을 수 없습니다: " + id);
        }
        inquiryRepository.deleteById(id);
    }
}
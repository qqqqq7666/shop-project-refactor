package com.example.shop_project.member.mapper;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.dto.MemberResponseDTO;
import com.example.shop_project.member.entity.Member;
import org.mapstruct.Mapper;

// 작동 안해서 일단 사용 안함
@Mapper(componentModel = "spring")
public interface MemberMapper {
	MemberResponseDTO toResponseDto(Member member);
	
	Member toEntity(MemberRequestDTO memberDto);
}

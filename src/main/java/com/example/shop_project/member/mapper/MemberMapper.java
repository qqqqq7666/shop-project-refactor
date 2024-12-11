package com.example.shop_project.member.mapper;

import com.example.shop_project.member.dto.MemberRequestDTO;
import com.example.shop_project.member.dto.MemberResponseDTO;
import com.example.shop_project.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
	MemberResponseDTO toResponseDto(Member member);
}

package com.example.shop_project.point;

import com.example.shop_project.member.repository.MemberRepository;
import com.example.shop_project.point.repository.PointRepository;
import com.example.shop_project.point.service.PointService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @InjectMocks
    PointService pointService;
    @Mock
    PointRepository pointRepository;
    @Mock
    MemberRepository memberRepository;


}

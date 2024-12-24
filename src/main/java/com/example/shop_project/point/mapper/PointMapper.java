package com.example.shop_project.point.mapper;

import com.example.shop_project.point.dto.*;
import com.example.shop_project.point.entity.Point;
import com.example.shop_project.point.entity.SavedPoint;
import com.example.shop_project.point.entity.UsedPoint;
import com.example.shop_project.point.repository.PointRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PointRepository.class)
public interface PointMapper {
    PointDto toDto(Point point);

//    @Mapping(target = "point", source = "pointId", qualifiedByName = "pointMapper")
    SavedPoint toEntity(SavedPointRequestDto savedPointRequestDto);
    SavedPointResponseDto toResponseDto(SavedPoint savedPoint);

//    @Mapping(target = "point", source = "pointId", qualifiedByName = "pointMapper")
    UsedPoint toEntity(UsedPointRequestDto usedPointRequestDto, @Context PointRepository pointRepository);
    UsedPointResponseDto toResponseDto(UsedPoint usedPoint);

    @Named("pointMapper")
    default Point pointMapper(Long pointId, @Context PointRepository pointRepository){
        if(pointId == null)
            return null;
        return pointRepository.findById(pointId).orElseThrow(() -> new IllegalArgumentException("포인트가 존재하지 않습니다."));
    }
}

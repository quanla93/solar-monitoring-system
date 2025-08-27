package com.solar.monitoring.system.util;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.model.SolarMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SolarMetricsMapper {
    
    SolarMetricsMapper INSTANCE = Mappers.getMapper(SolarMetricsMapper.class);
    
    SolarMetricsDto toDto(SolarMetrics entity);
    
    @Mapping(target = "rawData", ignore = true)
    SolarMetrics toEntity(SolarMetricsDto dto);
}
package com.solar.monitoring.system.util;

import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.model.SolarMetrics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SolarMetricsMapper {
    
    SolarMetricsMapper INSTANCE = Mappers.getMapper(SolarMetricsMapper.class);
    
    /**
 * Converts a SolarMetrics entity into a SolarMetricsDto.
 *
 * @param entity the source SolarMetrics entity
 * @return a SolarMetricsDto containing mapped values from the entity
 */
SolarMetricsDto toDto(SolarMetrics entity);
    
    /**
     * Converts a SolarMetricsDto into a SolarMetrics entity.
     *
     * The generated mapper will populate matching fields from the DTO into the entity.
     * The entity's `rawData` property is intentionally ignored during mapping.
     *
     * @param dto the source DTO to convert
     * @return a SolarMetrics entity populated from the DTO (with `rawData` left unset)
     */
    @Mapping(target = "rawData", ignore = true)
    SolarMetrics toEntity(SolarMetricsDto dto);
}
package com.solar.monitoring.system.service;

import com.solar.monitoring.system.dto.HistoryQueryDto;
import com.solar.monitoring.system.dto.SolarMetricsDto;
import com.solar.monitoring.system.model.SolarMetrics;
import com.solar.monitoring.system.repository.ISolarMetricsRepository;
import com.solar.monitoring.system.util.SolarMetricsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
public abstract class AbstractMongoService implements IMongoService {
    
    @Autowired
    protected ISolarMetricsRepository solarMetricsRepository;
    
    @Autowired
    protected SolarMetricsMapper mapper;
    
    /**
     * Build a Pageable for queries using the page and size from the provided HistoryQueryDto,
     * sorting results by `timestamp` in descending order.
     *
     * @param query the history query containing pagination parameters (page and size)
     * @return a PageRequest configured with the query's page, size, and a descending `timestamp` sort
     */
    protected Pageable createPageable(HistoryQueryDto query) {
        return PageRequest.of(
            query.getPage(),
            query.getSize(),
            Sort.by(Sort.Direction.DESC, "timestamp")
        );
    }
    
    /**
     * Convert a SolarMetricsDto into a SolarMetrics entity.
     *
     * @param dto the DTO to convert
     * @return the resulting SolarMetrics entity
     */
    protected SolarMetrics mapToEntity(SolarMetricsDto dto) {
        return mapper.toEntity(dto);
    }
    
    /**
     * Convert a SolarMetrics entity to its corresponding SolarMetricsDto.
     *
     * @param entity the domain entity to convert
     * @return the DTO representation of the given entity
     */
    protected SolarMetricsDto mapToDto(SolarMetrics entity) {
        return mapper.toDto(entity);
    }
}
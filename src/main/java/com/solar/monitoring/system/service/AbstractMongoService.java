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
    
    protected Pageable createPageable(HistoryQueryDto query) {
        return PageRequest.of(
            query.getPage(),
            query.getSize(),
            Sort.by(Sort.Direction.DESC, "timestamp")
        );
    }
    
    protected SolarMetrics mapToEntity(SolarMetricsDto dto) {
        return mapper.toEntity(dto);
    }
    
    protected SolarMetricsDto mapToDto(SolarMetrics entity) {
        return mapper.toDto(entity);
    }
}
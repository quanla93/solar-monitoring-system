package com.solar.monitoring.system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solar_raw_data")
public class SqlServerData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "machine_id")
    private String machineId;
    
    @Column(name = "data_content", columnDefinition = "TEXT")
    private String dataContent;
    
    @Column(name = "data_type")
    private String dataType; // XML or JSON
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "processed")
    private Boolean processed = false;
}
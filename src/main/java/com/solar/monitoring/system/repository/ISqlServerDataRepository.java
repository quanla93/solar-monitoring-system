package com.solar.monitoring.system.repository;

import com.solar.monitoring.system.model.SqlServerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISqlServerDataRepository extends JpaRepository<SqlServerData, Long> {
    
    List<SqlServerData> findByProcessedFalseOrderByCreatedAtAsc();
    
    @Modifying
    @Query("UPDATE SqlServerData s SET s.processed = true WHERE s.id = :id")
    void markAsProcessed(@Param("id") Long id);
}
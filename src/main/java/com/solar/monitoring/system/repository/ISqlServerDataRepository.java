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
    
    /**
 * Retrieves all SqlServerData entities that have not been processed, ordered by their creation timestamp ascending.
 *
 * @return a list of unprocessed SqlServerData records ordered by createdAt (oldest first); empty list if none found
 */
List<SqlServerData> findByProcessedFalseOrderByCreatedAtAsc();
    
    /**
     * Marks the SqlServerData row with the given id as processed by setting its `processed` flag to true.
     *
     * <p>Executed as a JPQL modifying query (no entity is returned). Must be invoked within a transaction
     * so the update is persisted.
     *
     * @param id the primary key of the SqlServerData row to mark processed
     */
    @Modifying
    @Query("UPDATE SqlServerData s SET s.processed = true WHERE s.id = :id")
    void markAsProcessed(@Param("id") Long id);
}
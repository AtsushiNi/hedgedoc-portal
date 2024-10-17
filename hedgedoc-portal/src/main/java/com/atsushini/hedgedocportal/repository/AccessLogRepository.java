package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atsushini.hedgedocportal.entity.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    
    @Query("SELECT a.statusCode, COUNT(a) FROM AccessLog a GROUP BY a.statusCode")
    List<Object[]> findStatusCodeCount();

    @Query("SELECT a.requestMethod, COUNT(a) FROM AccessLog a GROUP BY a.requestMethod")
    List<Object[]> findRequestMethodCount();

    @Query("SELECT a.requestUrl, COUNT(a) FROM AccessLog a GROUP BY a.requestUrl")
    List<Object[]> findRequestUrlCount();
}

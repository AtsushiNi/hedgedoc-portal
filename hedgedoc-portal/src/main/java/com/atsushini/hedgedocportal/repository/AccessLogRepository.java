package com.atsushini.hedgedocportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atsushini.hedgedocportal.entity.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    
}

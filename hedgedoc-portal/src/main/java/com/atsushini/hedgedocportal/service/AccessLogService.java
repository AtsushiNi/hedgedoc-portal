package com.atsushini.hedgedocportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atsushini.hedgedocportal.repository.AccessLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessLogService {
    
    public final AccessLogRepository accessLogRepository;

    public List<Object[]> getStatusCodeCount() {
        return accessLogRepository.findStatusCodeCount();
    }

    public List<Object[]> getRequestMethodCount() {
        return accessLogRepository.findRequestMethodCount();
    }

    public List<Object[]> getRequestUrlCount() {
        return accessLogRepository.findRequestUrlCount();
    }
}

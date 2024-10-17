package com.atsushini.hedgedocportal.interseptor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.entity.AccessLog;
import com.atsushini.hedgedocportal.repository.AccessLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessLogInterceptor implements HandlerInterceptor {
    
    private final AccessLogRepository accessLogRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        Long responseTime = System.currentTimeMillis() - startTime;

        AccessLog accessLog = new AccessLog();
        accessLog.setUserId(getCurrentUserId(request));
        accessLog.setRequestUrl(request.getRequestURI());
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setResponseTime(responseTime);
        accessLog.setTimestamp(LocalDateTime.now());
        accessLog.setStatusCode(response.getStatus());

        accessLogRepository.save(accessLog);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            return null;
        }

        CurrentUserDto currentUser = (CurrentUserDto) session.getAttribute(("currentUser")); 

        return currentUser.getId();
    }
}

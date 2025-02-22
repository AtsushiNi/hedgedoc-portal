package com.atsushini.hedgedocportal.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atsushini.hedgedocportal.service.AccessLogService;
import com.atsushini.hedgedocportal.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/admin/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {
    
    private final UserService userService;
    private final AccessLogService accessLogService;

    @GetMapping("/users")
    public List<Object[]> getCumulativeUserCount() {
        return userService.getCumulativeUserCount();
    }

    @GetMapping("/status-codes")
    public List<Object[]> getStatusCodeCount() {
        return accessLogService.getStatusCodeCount();
    }

    @GetMapping("/request-methods")
    public List<Object[]> getRequestMethodCount() {
        return accessLogService.getRequestMethodCount();
    }

    @GetMapping("/request-urls")
    public List<Object[]> getRequestUrlCount() {
        return accessLogService.getRequestUrlCount();
    }
}

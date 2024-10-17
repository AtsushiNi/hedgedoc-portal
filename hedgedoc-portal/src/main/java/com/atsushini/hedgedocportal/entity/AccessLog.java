package com.atsushini.hedgedocportal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import lombok.Data;;

@Entity
@Data
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String requestUrl;
    private String requestMethod;
    private int statusCode;
    private Long responseTime;
    private LocalDateTime timestamp;
}

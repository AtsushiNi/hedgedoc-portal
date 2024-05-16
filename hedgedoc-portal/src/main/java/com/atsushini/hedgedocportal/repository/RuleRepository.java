package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atsushini.hedgedocportal.entity.Rule;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    public List<Rule> findByUserId(Long userId);
}

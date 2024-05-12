package com.atsushini.hedgedocportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atsushini.hedgedocportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByHedgedocId(String hedgedocId);
}

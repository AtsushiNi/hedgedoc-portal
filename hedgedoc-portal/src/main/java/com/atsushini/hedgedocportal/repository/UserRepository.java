package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atsushini.hedgedocportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByHedgedocId(String hedgedocId);

    @Query("SELECT CAST(u.createdAt AS LocalDate), COUNT(u) " +
            "FROM User u " +
            "GROUP BY CAST(u.createdAt AS DATE) " +
            "ORDER BY CAST(u.createdAt AS DATE)")
    List<Object[]> findUserCountPerDay();
}

package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    void deleteByUserUserId(Integer userId);
}
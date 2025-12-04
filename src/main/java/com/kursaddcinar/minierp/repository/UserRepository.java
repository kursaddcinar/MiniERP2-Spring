package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Pagination (Role bilgisiyle birlikte)
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Page<User> findAll(Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role.roleName = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
    //List<User> findByRoles_Name(String roleName);
}
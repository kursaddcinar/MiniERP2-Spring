package com.kursaddcinar.minierp.repository;

import com.kursaddcinar.minierp.jwt.entity.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	boolean existsByName(String name);
	Optional<Role> findByName(String name);
}
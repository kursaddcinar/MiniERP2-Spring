package com.kursaddcinar.minierp.jwt;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kursaddcinar.minierp.jwt.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

//	@Query(value = "from User where username = :username")
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	boolean existsByUsername(String username);
}

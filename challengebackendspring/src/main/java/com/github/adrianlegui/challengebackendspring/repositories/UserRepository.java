package com.github.adrianlegui.challengebackendspring.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.adrianlegui.challengebackendspring.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
	Optional<UserEntity> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}

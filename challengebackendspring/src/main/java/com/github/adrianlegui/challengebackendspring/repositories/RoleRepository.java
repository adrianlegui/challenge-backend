package com.github.adrianlegui.challengebackendspring.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.adrianlegui.challengebackendspring.entities.Role;
import com.github.adrianlegui.challengebackendspring.entities.RoleEntity;

public interface RoleRepository
	extends JpaRepository<RoleEntity, Long> {
	Optional<RoleEntity> findByRoleName(Role roleName);
}

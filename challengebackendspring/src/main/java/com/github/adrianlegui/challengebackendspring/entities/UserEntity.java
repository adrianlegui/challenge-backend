package com.github.adrianlegui.challengebackendspring.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@NotBlank
	@Column(unique = true)
	private String username;

	@NotBlank
	@Column(unique = true)
	private String email;

	@NotBlank
	@Column(unique = true)
	private String password;

	@ManyToMany
	@JoinTable(
		name = "user_role",
		joinColumns = @JoinColumn(
			name = "user_id",
			referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(
			name = "role_id",
			referencedColumnName = "id"))
	private Set<RoleEntity> roles = new HashSet<>();

	
	public void removeRole(Role role) {
		RoleEntity roleEntity = this.roles.stream()
			.filter(rol -> rol.getRoleName() == role)
			.findFirst()
			.orElse(null);
		
		if (roleEntity != null)
			this.roles.remove(roleEntity);
	}
}

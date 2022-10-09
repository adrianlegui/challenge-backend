package com.github.adrianlegui.challengebackendspring.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.adrianlegui.challengebackendspring.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	@EqualsAndHashCode.Include
	private Long id;

	private String username;

	private String email;

	private String password;

	private Collection<? extends GrantedAuthority> authorities;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


	@Override
	public String getPassword() {
		return password;
	}


	@Override
	public String getUsername() {
		return username;
	}


	@Override
	public boolean isAccountNonExpired() {
		return true;
	}


	@Override
	public boolean isAccountNonLocked() {
		return true;
	}


	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}


	@Override
	public boolean isEnabled() {
		return true;
	}


	public static UserDetails build(UserEntity userEntity) {
		return new CustomUserDetails(
			userEntity.getId(), userEntity.getUsername(),
			userEntity.getEmail(), userEntity.getPassword(),
			userEntity
				.getRoles()
				.stream()
				.map(
					role -> new SimpleGrantedAuthority(
						role.getRoleName().name()))
				.toList());
	}


	public String getEmail() {
		return email;
	}

}

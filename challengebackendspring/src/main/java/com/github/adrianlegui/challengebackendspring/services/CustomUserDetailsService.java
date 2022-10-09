package com.github.adrianlegui.challengebackendspring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.adrianlegui.challengebackendspring.entities.UserEntity;
import com.github.adrianlegui.challengebackendspring.repositories.UserRepository;
import com.github.adrianlegui.challengebackendspring.security.CustomUserDetails;

@Service
@Transactional
public class CustomUserDetailsService
	implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;


	@Override
	public UserDetails loadUserByUsername(String username)
		throws UsernameNotFoundException {
		UserEntity userEntity =
			userRepository
				.findByUsername(username)
				.orElseThrow(
					() -> new UsernameNotFoundException(
						"User Not Found with username: "
							+ username));

		return CustomUserDetails.build(userEntity);
	}

}

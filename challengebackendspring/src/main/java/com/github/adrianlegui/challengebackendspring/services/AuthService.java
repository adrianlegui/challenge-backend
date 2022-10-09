package com.github.adrianlegui.challengebackendspring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.adrianlegui.challengebackendspring.dto.LoginDTORequest;
import com.github.adrianlegui.challengebackendspring.dto.LoginDTOResponse;
import com.github.adrianlegui.challengebackendspring.dto.RegisterDTORequest;
import com.github.adrianlegui.challengebackendspring.dto.RegisterDTOResponse;
import com.github.adrianlegui.challengebackendspring.entities.UserEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.EmailAlreadyExistsException;
import com.github.adrianlegui.challengebackendspring.exceptions.RoleNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.UsernameAlreadyExistsException;
import com.github.adrianlegui.challengebackendspring.mappers.UserMapper;
import com.github.adrianlegui.challengebackendspring.repositories.RoleRepository;
import com.github.adrianlegui.challengebackendspring.repositories.UserRepository;
import com.github.adrianlegui.challengebackendspring.security.CustomUserDetails;
import com.github.adrianlegui.challengebackendspring.security.jwt.JwtUtils;


@Service
@Transactional
public class AuthService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtUtils jwtUtils;


	public LoginDTOResponse authenticateUser(
		LoginDTORequest loginDTORequest) {
		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(
				loginDTORequest.getUsername(),
				loginDTORequest.getPassword());

		Authentication authentication =
			authenticationManager.authenticate(authToken);

		SecurityContextHolder
			.getContext()
			.setAuthentication(authentication);

		String jwt = jwtUtils.generateToken(authentication);

		CustomUserDetails userDetails =
			(CustomUserDetails) authentication.getPrincipal();

		LoginDTOResponse response =
			userMapper.userDetailsToLoginDtoResponse(userDetails);
		response.setToken(jwt);

		return response;
	}


	public RegisterDTOResponse registerUser(
		RegisterDTORequest registerDTORequest) {
		if (userRepository
			.existsByUsername(registerDTORequest.getUsername()))
			throw new UsernameAlreadyExistsException(
				"username is already exists");

		if (userRepository
			.existsByEmail(registerDTORequest.getEmail()))
			throw new EmailAlreadyExistsException(
				"email is already exists");

		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(registerDTORequest.getUsername());
		userEntity.setEmail(registerDTORequest.getEmail());
		userEntity
			.setPassword(
				passwordEncoder
					.encode(registerDTORequest.getPassword()));
		userEntity
			.getRoles()
			.add(
				roleRepository
					.findByRoleName(registerDTORequest.getRole())
					.orElseThrow(
						() -> new RoleNotFoundException(
							"not found role "
								+ registerDTORequest
									.getRole()
									.toString())));

		userRepository.save(userEntity);

		return userMapper.entityToRegisterDTOResponse(userEntity);
	}

}

package com.github.adrianlegui.challengebackendspring.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;

import com.github.adrianlegui.challengebackendspring.dto.LoginDTOResponse;
import com.github.adrianlegui.challengebackendspring.dto.RegisterDTOResponse;
import com.github.adrianlegui.challengebackendspring.entities.Role;
import com.github.adrianlegui.challengebackendspring.entities.UserEntity;
import com.github.adrianlegui.challengebackendspring.security.CustomUserDetails;

@Mapper(
	componentModel = "spring",
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
	RegisterDTOResponse entityToRegisterDTOResponse(
		UserEntity userEntity);

	@Mapping(target = "roles", source = "authorities")
	LoginDTOResponse userDetailsToLoginDtoResponse(
		CustomUserDetails userDetails);


	default Role fromGrantedAuthority(
		GrantedAuthority grantedAuthority) {
		return grantedAuthority == null ? null
			: Role.valueOf(grantedAuthority.getAuthority());
	}
}

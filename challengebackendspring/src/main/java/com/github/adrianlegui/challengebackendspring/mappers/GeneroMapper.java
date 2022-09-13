package com.github.adrianlegui.challengebackendspring.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTO;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GeneroMapper {

	GeneroDTOPATCH dtoPostToDtoPath(GeneroDTOPOST generoDTOPOST);

	GeneroEntity dtoPathtoEntity(GeneroDTOPATCH generoDTOPATCH);

	GeneroDTO entityToDTO(GeneroEntity generoEntity);

	GeneroDTOGET entityToDtoGet(GeneroEntity generoEntity);

	GeneroEntity dtoPathtoEntity(GeneroDTOPATCH generoDTOPATCH, @MappingTarget GeneroEntity generoEntity);
}

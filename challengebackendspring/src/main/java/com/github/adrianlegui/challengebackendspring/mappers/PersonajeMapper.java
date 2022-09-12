package com.github.adrianlegui.challengebackendspring.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTO;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonajeMapper {
	public PersonajeEntity dtoPatchToEntity(PersonajeDTOPATCH personajeDTOPATCH, @MappingTarget PersonajeEntity personajeEntity);

	public PersonajeEntity dtoPostToEntity(PersonajeDTOPOST personajeDTOPOST);
	
	public PersonajeEntity dtoPatchToEntity(PersonajeDTOPATCH personajeDTOPATCH);
	
	public PersonajeDTOGET entityToDtoGet(PersonajeEntity personajeEntity);

	public PersonajeDTOPOST entityToDtoPost(PersonajeEntity personajeEntity);

	public PersonajeDTO entityToDto(PersonajeEntity personajeEntity);
	
	public PersonajeDTOPATCH dtoPostToDtoPatch(PersonajeDTOPOST personajeDTOPOST);
	
}

package com.github.adrianlegui.challengebackendspring.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;

@Mapper(
	componentModel = "spring",
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SerieMapper {

	SerieDTOPATCH dtoPostToDtoPatch(SerieDTOPOST serieDTOPOST);

	SerieEntity dtoPatchToEntity(SerieDTOPATCH serieDTOPATCH);

	SerieDTO entityToDTO(SerieEntity serieEntity);

	SerieDTOGET entityToDtoGet(SerieEntity serieEntity);

	SerieEntity dtoPatchToEntity(
		SerieDTOPATCH serieDTOPATCH,
		@MappingTarget SerieEntity serieEntity);

}

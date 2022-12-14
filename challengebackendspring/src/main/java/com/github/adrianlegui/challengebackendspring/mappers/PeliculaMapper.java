package com.github.adrianlegui.challengebackendspring.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTO;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;

@Mapper(
	componentModel = "spring",
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PeliculaMapper {

	PeliculaDTOPATCH dtoPostToDtoPatch(
		PeliculaDTOPOST peliculaDTOPOST);

	PeliculaEntity dtoPatchToEntity(
		PeliculaDTOPATCH peliculaDTOPATCH);

	PeliculaDTO entityToDTO(PeliculaEntity peliculaEntity);

	PeliculaDTOGET entityToDtoGet(PeliculaEntity peliculaEntity);

	PeliculaEntity dtoPatchToEntity(
		PeliculaDTOPATCH peliculaDTOPATCH,
		@MappingTarget PeliculaEntity peliculaEntity);

}

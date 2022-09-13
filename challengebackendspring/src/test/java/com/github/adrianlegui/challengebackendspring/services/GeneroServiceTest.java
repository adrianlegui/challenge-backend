package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTO;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPOST;
import com.github.adrianlegui.challengebackendspring.dto.PeliculaDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOId;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PeliculaEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.GeneroMapper;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {
	@InjectMocks
	GeneroService generoService;
	
	@Mock
	GeneroRepository generoRepository;
	
	@Mock
	PeliculaRepository peliculaRepository;
	
	@Mock
	SerieRepository serieRepository;
	
	@Mock
	GeneroMapper generoMapper;

	@Test
	void create_WithNotExistedPeliculaId_ThrowEntityNotFoundException() {
		Long notExistedPeliculaId = 1L;
		PeliculaDTOId notExistedPelicula = new PeliculaDTOId();
		notExistedPelicula.setId(notExistedPeliculaId);
		
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getPeliculas().add(notExistedPelicula);
		
		when(peliculaRepository.existsById(notExistedPeliculaId))
			.thenReturn(false);
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.create(generoDTOPOST)
				);
	}
	
	@Test
	void create_WithNotExistedSerieId_ThrowEntityNotFoundException() {
		Long notExistedSerieId = 1L;
		SerieDTOId notExistedSerie = new SerieDTOId();
		notExistedSerie.setId(notExistedSerieId);
		
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getSeries().add(notExistedSerie);
		
		when(serieRepository.existsById(notExistedSerieId))
			.thenReturn(false);
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.create(generoDTOPOST)
				);
	}
	
	@Test
	void create_WithGeneroDTOPOST_ReturnGeneroDTO() {
		Long existedPeliculaId = 1L;
		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		peliculaDTOId.setId(existedPeliculaId);
		
		Long existedSerieId = 1L;
		SerieDTOId serieDTOId = new SerieDTOId();
		serieDTOId.setId(existedSerieId);
		
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getPeliculas().add(peliculaDTOId);
		generoDTOPOST.getSeries().add(serieDTOId);
		
		// check if the movies exists
		when(peliculaRepository.existsById(existedPeliculaId))
			.thenReturn(true);
		// check if the series exists
		when(serieRepository.existsById(existedSerieId))
			.thenReturn(true);
		
		// create genre without association
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		
		when(
			generoMapper.dtoPostToDtoPath(generoDTOPOST))
		.thenReturn(generoDTOPATCH);
		
		GeneroEntity generoEntity = new GeneroEntity();
		
		when(
				generoMapper.dtoPathtoEntity(generoDTOPATCH)
				)
		.thenReturn(generoEntity);
		
		when(
				generoRepository.save(generoEntity)
				)
		.thenReturn(generoEntity);
		
		// create association with movies
		PeliculaEntity pelicula = new PeliculaEntity();
		when(
				peliculaRepository.findById(existedPeliculaId)
				)
		.thenReturn(Optional.of(pelicula));
		
		when(
				generoRepository.save(generoEntity)
				)
		.thenReturn(generoEntity);
		
		// create association with series
		SerieEntity serie = new SerieEntity();
		when(
				serieRepository.findById(existedSerieId)
				)
		.thenReturn(Optional.of(serie));
		
		when(
				generoRepository.save(generoEntity)
				)
		.thenReturn(generoEntity);
		
		when(
				generoMapper.entityToDTO(generoEntity)
				)
		.thenReturn(new GeneroDTO());
		
		GeneroDTO resultado = generoService.create(generoDTOPOST);
		
		assertNotNull(resultado);
	}

	@Test
	void findById_NotExistedGeneroId_ThrowEntityNotFoundException() {
		Long notExistedGeneroId = 1L;
		
		when(
				generoRepository.findById(notExistedGeneroId)
				)
		.thenReturn(Optional.empty());
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.findById(notExistedGeneroId)
				);
	}
	
	@Test
	void findById_ExistedGeneroId_ReturnGeneroDTOGET() {
		Long existedGeneroId = 1L;
		
		GeneroEntity generoEntity = new GeneroEntity();
		
		when(
				generoRepository.findById(existedGeneroId)
				)
		.thenReturn(Optional.of(generoEntity ));
		
		GeneroDTOGET generoDTOGET = new GeneroDTOGET();
		
		when(
				generoMapper.entityToDtoGet(generoEntity)
				)
		.thenReturn(generoDTOGET);
		
		GeneroDTOGET resultado = generoService.findById(existedGeneroId);
		
		assertNotNull(resultado);
	}

	@Test
	void findAll_ReturnEmptyList() {
		GeneroEntity generoEntity = new GeneroEntity();
		List<GeneroEntity> generoEntities = new ArrayList<>();
		generoEntities.add(generoEntity);
		
		when(
				generoRepository.findAll()
				)
		.thenReturn(generoEntities);
		
		GeneroDTOGET generoDTOGET = new GeneroDTOGET();
		
		when(
				generoMapper.entityToDtoGet(generoEntity)
				)
		.thenReturn(generoDTOGET);
		
		List<GeneroDTOGET> resultado = generoService.findAll();
		
		assertFalse(resultado.isEmpty());
	}

	@Test
	void update_WithNullId_ThrowIdNullException() {
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		
		assertThrows(
				IdNullException.class,
				() -> generoService.update(generoDTOPATCH)
				);
	}

	@Test
	void update_WithNotExistedGeneroId_ThrowEntityNotFoundException() {
		Long notExistedGeneroId = 1L;
		
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		generoDTOPATCH.setId(notExistedGeneroId);
		
		when(
				generoRepository.findById(notExistedGeneroId)
				)
		.thenReturn(Optional.empty());
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.update(generoDTOPATCH)
				);
	}
	
	@Test
	void update_WithGeneroDTOPATCH_ReturnGeneroDTO() {
		Long existedGeneroId = 1L;
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		generoDTOPATCH.setId(existedGeneroId);
		
		GeneroEntity generoEntity = new GeneroEntity();
		when(
				generoRepository.findById(existedGeneroId)
				)
		.thenReturn(Optional.of(generoEntity));
		
		when(
				generoMapper.dtoPathtoEntity(
						generoDTOPATCH,
						generoEntity)
				)
		.thenReturn(generoEntity);
		
		GeneroDTO generoDTO = new GeneroDTO();
		when(
				generoRepository.save(generoEntity)
				)
		.thenReturn(generoEntity);
		
		when(
				generoMapper.entityToDTO(generoEntity)
				)
		.thenReturn(generoDTO);
		
		GeneroDTO resultado = generoService.update(generoDTOPATCH);
		
		assertNotNull(resultado);
	}
	
	@Test
	void deleteById_WithNotExistedGeneroId_ThrowDeleteNotExistedException() {
		Long notExistedGeneroId = 1L;
		
		when(
				generoRepository.findById(notExistedGeneroId)
				)
		.thenReturn(Optional.empty());
		
		assertThrows(
				DeleteNotExistedException.class, 
				() -> generoService.deleteById(notExistedGeneroId)
				);
	}
	
	@Test
	void deleteById_ExistedGeneroId_DoesNotThrowException() {
		Long existedGeneroId = 1L;
		
		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.addPelicula(new PeliculaEntity());
		generoEntity.addSerie(new SerieEntity());
		
		when(
				generoRepository.findById(existedGeneroId)
				)
		.thenReturn(Optional.of(generoEntity));
		
		when(
				generoRepository.save(generoEntity)
				)
		.thenReturn(generoEntity);
		
		doNothing().when(generoRepository).deleteById(existedGeneroId);
		
		assertDoesNotThrow(
				() -> generoService.deleteById(existedGeneroId)
				);
	}

	@Test
	void deleteAll_VoidTable_throwDeleteVoidTableException() {
		when(
				generoRepository.count()
				)
		.thenReturn(0L);
		
		assertThrows(
				DeleteVoidTableException.class, 
				() -> generoService.deleteAll()
				);
	}
	
	@Test
	void deleteAll_NotVoidTable_DoesNotThrowException() {
		when(
				generoRepository.count()
				)
		.thenReturn(1L);
		
		Long generoId = 1L;
		GeneroEntity generoEntity = new GeneroEntity();
		generoEntity.setId(generoId);
		List<GeneroEntity> generoEntities = new ArrayList<>();
		generoEntities.add(generoEntity);
		
		when(
				generoRepository.findAll()
				)
		.thenReturn(generoEntities);
		
		when(
				generoRepository.findById(generoId)
				)
		.thenReturn(Optional.of(generoEntity));
		
		when(
				generoRepository.save(generoEntity)
				)
		.thenReturn(generoEntity);
		
		doNothing().when(generoRepository).deleteById(generoId);
		
		assertDoesNotThrow(
				() -> generoService.deleteAll()
				);
	}

}

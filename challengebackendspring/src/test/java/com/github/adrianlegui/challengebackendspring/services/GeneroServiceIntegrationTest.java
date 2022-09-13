package com.github.adrianlegui.challengebackendspring.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
import com.github.adrianlegui.challengebackendspring.mappers.MappersConfig;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@SpringBootTest(classes = {MappersConfig.class})
@Transactional
class GeneroServiceIntegrationTest {
	@Autowired
	GeneroService generoService;
	
	@Autowired
	PeliculaRepository peliculaRepository;
	
	@Autowired
	SerieRepository serieRepository;
	
	@Autowired
	GeneroRepository generoRepository;

	@Test
	void create_WithNotExistedPeliculaId_ThrowEntityNotFoundException() {
		PeliculaEntity notExistedPelicula =	peliculaRepository.save(
				new PeliculaEntity()
				);
		
		Long notExistedPeliculaId = notExistedPelicula.getId();
		peliculaRepository.delete(notExistedPelicula);
		
		PeliculaDTOId notExistedPeliculaDTOId = new PeliculaDTOId();
		notExistedPeliculaDTOId.setId(notExistedPeliculaId);
		
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getPeliculas().add(notExistedPeliculaDTOId);
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.create(generoDTOPOST)
				);
	}
	
	@Test
	void create_WithNotExistedSerieId_ThrowEntityNotFoundException() {
		SerieEntity notExistedSerie =	serieRepository.save(
				new SerieEntity()
				);
		
		Long notExistedSerieId = notExistedSerie.getId();
		serieRepository.delete(notExistedSerie);
		
		SerieDTOId notExistedSerieDTOId = new SerieDTOId();
		notExistedSerieDTOId.setId(notExistedSerieId);
		
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getSeries().add(notExistedSerieDTOId);
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.create(generoDTOPOST)
				);
	}
	
	@Test
	void create_WithExistedPeliculaAndSerie_ReturnGeneroDTO() {
		PeliculaEntity pelicula = peliculaRepository.save(
				new PeliculaEntity()
				);
		
		PeliculaDTOId peliculaDTOId = new PeliculaDTOId();
		peliculaDTOId.setId(pelicula.getId());
		
		SerieEntity serie = serieRepository.save(
				new SerieEntity()
				);
		
		SerieDTOId serieDTOId = new SerieDTOId();
		serieDTOId.setId(serie.getId());
		
		GeneroDTOPOST generoDTOPOST = new GeneroDTOPOST();
		generoDTOPOST.getPeliculas().add(peliculaDTOId);
		generoDTOPOST.getSeries().add(serieDTOId);
		
		GeneroDTO resultado = generoService.create(generoDTOPOST);
		
		assertTrue(generoRepository.existsById(resultado.getId()));
	}

	@Test
	void findById_NotExistedGeneroId_ThrowEntityNotFoundException() {
		GeneroEntity notExistedGenero = generoRepository.save(
				new GeneroEntity()
				);
		
		Long notExistedGeneroId = notExistedGenero.getId();
		
		generoRepository.delete(notExistedGenero);
		
		assertThrows(
				EntityNotFoundException.class,
				() -> generoService.findById(notExistedGeneroId)
				);
	}
	
	@Test
	void findById_ExistedGeneroId_ReturnGeneroDTOGET() {
		GeneroEntity existedGeneroId = generoRepository.save(
				new GeneroEntity()
				);
		
		GeneroDTOGET resultado = generoService.findById(
				existedGeneroId.getId()
				);
		
		assertNotNull(resultado);
		assertEquals(existedGeneroId.getId(), resultado.getId());
	}

	@Test
	void findAll_NoVoidTable_ReturnNotEmptyList() {
		generoRepository.save(new GeneroEntity());
		
		Long entidades = generoRepository.count();
		
		List<GeneroDTOGET> resultado = generoService.findAll();
		
		assertEquals(entidades, resultado.size());
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
		GeneroEntity notExistedGenero = generoRepository.save(
				new GeneroEntity()
				);
		
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		generoDTOPATCH.setId(notExistedGenero.getId());
		
		generoRepository.delete(notExistedGenero);
		
		assertThrows(
				EntityNotFoundException.class, 
				() -> generoService.update(generoDTOPATCH)
				);
	}
	
	@Test
	void update_ChangeName_ReturnGeneroDTO() {
		GeneroEntity genero = generoRepository.save(new GeneroEntity());
		genero.setNombre("nombre anterior");
		generoRepository.save(genero);
		
		GeneroDTOPATCH generoDTOPATCH = new GeneroDTOPATCH();
		generoDTOPATCH.setId(genero.getId());
		generoDTOPATCH.setNombre("nombre nuevo");
		
		GeneroDTO resultado = generoService.update(generoDTOPATCH);
		
		assertEquals(
				generoDTOPATCH.getNombre(),
				resultado.getNombre()
				);
		
		Optional<GeneroEntity> actualizado = generoRepository.findById(genero.getId());
		assertEquals(
				generoDTOPATCH.getNombre(), 
				actualizado.get().getNombre()
				);
	}

	@Test
	void deleteById_WithNotExistedGeneroId_ThrowDeleteNotExistedException() {
		GeneroEntity notExistedGenero = generoRepository.save(
				new GeneroEntity()
				);
		
		Long notExistedGeneroId = notExistedGenero.getId();
		
		generoRepository.deleteById(notExistedGeneroId);
		
		assertThrows(
				DeleteNotExistedException.class, 
				() -> generoService.deleteById(notExistedGeneroId)
				);
	}
	
	@Test
	void deleteById_WithExistedGeneroId_DoesNotThrowException() {
		GeneroEntity existedGenero = generoRepository.save(
				new GeneroEntity()
				);
		
		Long existedGeneroId = existedGenero.getId();
		
		assertDoesNotThrow(
				() -> generoService.deleteById(existedGeneroId)
				);
	}

	@Test
	void deleteAll_WithVoidTable_ThrowDeleteVoidTableException() {
		if(0 < generoRepository.count()) {
			List<GeneroEntity> generoEntities = generoRepository.findAll();
			
			for(GeneroEntity genero : generoEntities) {
				genero.removeAllPelicula();
				genero.removeAllSerie();
				generoRepository.save(genero);
			}
			
			generoRepository.deleteAll();
		}
		
		assertThrows(
				DeleteVoidTableException.class, 
				() -> generoService.deleteAll()
				);
	}
	
	@Test
	void deleteAll_WithNotVoidTable_DoesNotThrowException() {
		generoRepository.save(new GeneroEntity());
		
		assertDoesNotThrow(() -> generoService.deleteAll());
	}

}

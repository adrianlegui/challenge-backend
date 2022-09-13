package com.github.adrianlegui.challengebackendspring.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import com.github.adrianlegui.challengebackendspring.mappers.GeneroMapper;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PeliculaRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@Service
@Transactional
public class GeneroService {
	@Autowired
	private GeneroRepository generoRepository;
	
	@Autowired
	private PeliculaRepository peliculaRepository; 
	
	@Autowired
	private SerieRepository serieRepository;
	
	@Autowired
	private GeneroMapper generoMapper;
	
	public GeneroDTO create(GeneroDTOPOST generoDTOPOST) {
		// check if the movies exists
		for(PeliculaDTOId pelicula : generoDTOPOST.getPeliculas())
			if(!peliculaRepository.existsById(pelicula.getId()))
				throw new EntityNotFoundException(
						"pelicula not found with id " + pelicula.getId()
						);
		
		// check if the series exists
		for(SerieDTOId serie : generoDTOPOST.getSeries())
			if(!serieRepository.existsById(serie.getId()))
				throw new EntityNotFoundException(
						"serie not found with id " + serie.getId()
						);
		
		// create genre without association
		GeneroDTOPATCH generoDTOPATCH = generoMapper.dtoPostToDtoPath(
				generoDTOPOST
				);
		
		GeneroEntity generoParaCrear = generoMapper.dtoPathtoEntity(
				generoDTOPATCH
				);
		
		GeneroEntity generoCreado = generoRepository.save(generoParaCrear);
		
		// create association with movies
		for(PeliculaDTOId pelicula : generoDTOPOST.getPeliculas()) {
			Optional<PeliculaEntity> peliculaOptional = 
					peliculaRepository.findById(pelicula.getId());
			
			if(peliculaOptional.isPresent()) {
				generoCreado.addPelicula(peliculaOptional.get());
				generoCreado = generoRepository.save(generoCreado);
			}
		}
		
		// create association with series
		for(SerieDTOId serie : generoDTOPOST.getSeries()) {
			Optional<SerieEntity> serieOptional =
					serieRepository.findById(serie.getId());
			
			if(serieOptional.isPresent()) {
				generoCreado.addSerie(serieOptional.get());
				generoCreado = generoRepository.save(generoCreado);
			}
		}
		
		return generoMapper.entityToDTO(generoCreado);
	}
	
	public GeneroDTOGET findById(Long id) {
		Optional<GeneroEntity> generoOptional =
				generoRepository.findById(id);
		
		if(generoOptional.isEmpty())
			throw new EntityNotFoundException("genero not found with id " + id);
		else
			return generoMapper.entityToDtoGet(generoOptional.get());
	}
	
	public List<GeneroDTOGET> findAll(){
		List<GeneroDTOGET> generoDTOGETs = new ArrayList<>();
		
		List<GeneroEntity> generoEntities =	generoRepository.findAll();
		
		for(GeneroEntity genero : generoEntities)
			generoDTOGETs.add(
					generoMapper.entityToDtoGet(genero)
					);
		
		return generoDTOGETs;
	}
	
	public GeneroDTO update(GeneroDTOPATCH generoDTOPATCH) {
		if(generoDTOPATCH.getId() == null)
			throw new IdNullException("can not update with id null");
		
		Optional<GeneroEntity> generoOptional =
				generoRepository.findById(generoDTOPATCH.getId());
		
		GeneroEntity generoActualizado;
		
		if(generoOptional.isEmpty())
			throw new EntityNotFoundException(
					"genero not found with id " + generoDTOPATCH.getId()
					);
		else
			generoActualizado = 
			generoMapper.dtoPathtoEntity(
					generoDTOPATCH,
					generoOptional.get());
		
		GeneroEntity generoSalvado =
				generoRepository.save(generoActualizado);
		
		return generoMapper.entityToDTO(generoSalvado);
	}
	
	public void deleteById(Long id) {
		Optional<GeneroEntity> generoOptional =
				generoRepository.findById(id);
		
		if(generoOptional.isEmpty())
			throw new DeleteNotExistedException(
					"genero not exist with id " + id
					);
		
		GeneroEntity generoParaBorrar = generoOptional.get();
		generoParaBorrar.removeAllPelicula();
		generoParaBorrar.removeAllSerie();
		generoRepository.save(generoParaBorrar);
		generoRepository.deleteById(id);
	}
	
	public void deleteAll() {
		if(0 >= generoRepository.count())
			throw new DeleteVoidTableException("genero table is void");
		else {
			List<GeneroEntity> generoEntities = generoRepository.findAll();
			
			for(GeneroEntity genero : generoEntities)
				deleteById(genero.getId());
		}
	}
}

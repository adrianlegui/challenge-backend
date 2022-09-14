package com.github.adrianlegui.challengebackendspring.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOId;
import com.github.adrianlegui.challengebackendspring.dto.PersonajeDTOId;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTO;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.SerieDTOPOST;
import com.github.adrianlegui.challengebackendspring.entities.GeneroEntity;
import com.github.adrianlegui.challengebackendspring.entities.PersonajeEntity;
import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;
import com.github.adrianlegui.challengebackendspring.exceptions.AssociationNotExistingException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteNotExistedException;
import com.github.adrianlegui.challengebackendspring.exceptions.DeleteVoidTableException;
import com.github.adrianlegui.challengebackendspring.exceptions.EntityNotFoundException;
import com.github.adrianlegui.challengebackendspring.exceptions.IdNullException;
import com.github.adrianlegui.challengebackendspring.mappers.SerieMapper;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;
import com.github.adrianlegui.challengebackendspring.repositories.PersonajeRepository;
import com.github.adrianlegui.challengebackendspring.repositories.SerieRepository;

@Service
@Transactional
public class SerieService {
	@Autowired
	private PersonajeRepository personajeRepository;

	@Autowired
	private GeneroRepository generoRepository;

	@Autowired
	private SerieRepository serieRepository;

	@Autowired
	private SerieMapper serieMapper;

	public SerieDTO create(SerieDTOPOST serieDTOPOST) {
		// check if the characters exist
		for (PersonajeDTOId personaje : serieDTOPOST
			.getPersonajesEnSerie())
			if (!personajeRepository
				.existsById(personaje.getId()))
				throw new EntityNotFoundException(
					"personaje not found with id "
						+ personaje.getId());

		// check if the genres exist
		for (GeneroDTOId genero : serieDTOPOST
			.getGenerosDeLaSerie())
			if (!generoRepository.existsById(genero.getId()))
				throw new EntityNotFoundException(
					"genero not found with id " + genero.getId());

		// create serie without associations
		SerieDTOPATCH serieDTOPATCH = serieMapper
			.dtoPostToDtoPatch(serieDTOPOST);

		SerieEntity serieParaCrear = serieMapper
			.dtoPatchToEntity(serieDTOPATCH);

		SerieEntity serieCreada = serieRepository
			.save(serieParaCrear);

		// create association to characters
		for (PersonajeDTOId personaje : serieDTOPOST
			.getPersonajesEnSerie()) {
			Optional<PersonajeEntity> personajeOptional = personajeRepository
				.findById(personaje.getId());

			if (personajeOptional.isPresent()) {
				serieCreada.addPersonaje(personajeOptional.get());
				serieCreada = serieRepository.save(serieCreada);
			}

		}

		// create association to genres
		for (GeneroDTOId genero : serieDTOPOST
			.getGenerosDeLaSerie()) {
			Optional<GeneroEntity> generoOptional = generoRepository
				.findById(genero.getId());

			if (generoOptional.isPresent()) {
				serieCreada.addGenero(generoOptional.get());
				serieCreada = serieRepository.save(serieCreada);
			}
		}

		return serieMapper.entityToDTO(serieCreada);
	}

	public SerieDTOGET findById(Long id) {
		Optional<SerieEntity> serieOptional = serieRepository
			.findById(id);

		if (serieOptional.isEmpty())
			throw new EntityNotFoundException(
				"serie not found with id " + id);
		else {
			SerieEntity serieEntity = serieOptional.get();
			return serieMapper.entityToDtoGet(serieEntity);
		}
	}

	public List<SerieDTOGET> findAll(
		String nombre,
		Long generoId,
		Orden orden) {
		List<SerieDTOGET> serieDTOGETs = new ArrayList<>();

		List<SerieEntity> serieEntities = serieRepository
			.findAll();

		Iterator<SerieEntity> iterator = serieEntities.iterator();

		while (iterator.hasNext()) {
			SerieEntity serieEntity = iterator.next();

			if ((nombre != null
				&& !nombre.equals(serieEntity.getTitulo()))
				|| (generoId != null && !serieEntity
					.hasGeneroWithIdNumber(generoId)))
				iterator.remove();
		}

		if (orden != null) {
			serieEntities.sort(
				Comparator
					.comparing(SerieEntity::getFechaDeCreacion));

			if (orden == Orden.DESC)
				Collections.reverse(serieEntities);
		}

		for (SerieEntity serie : serieEntities)
			serieDTOGETs.add(serieMapper.entityToDtoGet(serie));

		return serieDTOGETs;

	}

	public SerieDTO update(SerieDTOPATCH serieDTOPATCH) {
		if (serieDTOPATCH.getId() == null)
			throw new IdNullException(
				"can not update with id null");

		Optional<SerieEntity> serieOptional = serieRepository
			.findById(serieDTOPATCH.getId());

		if (serieOptional.isEmpty())
			throw new EntityNotFoundException(
				"serie not found with id "
					+ serieDTOPATCH.getId());
		else {
			SerieEntity serieActualizada = serieMapper
				.dtoPatchToEntity(
					serieDTOPATCH,
					serieOptional.get());

			serieActualizada = serieRepository
				.save(serieActualizada);

			return serieMapper.entityToDTO(serieActualizada);
		}
	}

	public void deleteById(Long id) {
		Optional<SerieEntity> serieOptional = serieRepository
			.findById(id);

		if (serieOptional.isEmpty())
			throw new DeleteNotExistedException(
				"serie not exist with id " + id);
		else {
			SerieEntity serieParaBorrar = serieOptional.get();

			serieParaBorrar.removeAllGenero();
			serieParaBorrar.removeAllPersonaje();
			serieParaBorrar = serieRepository
				.save(serieParaBorrar);
			serieRepository.delete(serieParaBorrar);
		}
	}

	public void deleteAll() {
		if (0 >= serieRepository.count())
			throw new DeleteVoidTableException(
				"serie table is empty");
		else {
			List<SerieEntity> serieEntities = serieRepository
				.findAll();

			for (SerieEntity serie : serieEntities) {
				serie.removeAllGenero();
				serie.removeAllPersonaje();
				serieRepository.save(serie);
				serieRepository.delete(serie);
			}
		}
	}

	public SerieDTO asociarPersonajeASerie(
		Long serieId,
		Long personajeId) {

		Optional<PersonajeEntity> personajeOptional = personajeRepository
			.findById(personajeId);

		if (personajeOptional.isEmpty())
			throw new EntityNotFoundException(
				"personaje not found with id " + personajeId);

		Optional<SerieEntity> serieOptional = serieRepository
			.findById(serieId);

		if (serieOptional.isEmpty())
			throw new EntityNotFoundException(
				"serie not found with id " + personajeId);

		SerieEntity serieEntity = serieOptional.get();
		serieEntity.addPersonaje(personajeOptional.get());
		serieEntity = serieRepository.save(serieEntity);

		return serieMapper.entityToDTO(serieEntity);
	}

	public void desasociarPersonajeASerie(
		Long serieId,
		Long personajeId) {

		Optional<SerieEntity> serieOptional = serieRepository
			.findById(serieId);

		if (serieOptional.isEmpty())
			throw new EntityNotFoundException(
				"serie not found with id " + serieId);

		SerieEntity serieEntity = serieOptional.get();
		if (!serieEntity.hasPersonajeWithId(personajeId))
			throw new AssociationNotExistingException(
				String.format(
					"Association not existing between serie %s and personaje %s",
					serieId,
					personajeId));
		
		serieEntity.removePersonaje(personajeId);
		serieRepository.save(serieEntity);
	}
}

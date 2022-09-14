package com.github.adrianlegui.challengebackendspring.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "serie")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SerieEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	private String titulo;
	private String imagen;
	private Integer calificacion;
	private LocalDate fechaDeCreacion;

	@ManyToMany
	@JoinTable(
		name = "serie_personaje",
		joinColumns = @JoinColumn(
			name = "serie_id",
			referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(
			name = "personaje_id",
			referencedColumnName = "id"))
	private Set<PersonajeEntity> personajesEnSerie = new HashSet<>();

	@ManyToMany
	@JoinTable(
		name = "serie_genero",
		joinColumns = @JoinColumn(
			name = "serie_id",
			referencedColumnName = "id"),
		inverseJoinColumns = @JoinColumn(
			name = "genero_id",
			referencedColumnName = "id"))
	private Set<GeneroEntity> generosDeLaSerie = new HashSet<>();

	public void addPersonaje(PersonajeEntity personajeEntity) {
		this.personajesEnSerie.add(personajeEntity);
		personajeEntity.getSeries().add(this);
	}

	public void removePersonaje(long personajeId) {
		PersonajeEntity personajeEntity = this.personajesEnSerie
			.stream().filter(t -> t.getId() == personajeId).findFirst()
			.orElse(null);

		if (personajeEntity != null) {
			this.personajesEnSerie.remove(personajeEntity);
			personajeEntity.getSeries().remove(this);
		}
	}

	public void removeAllPersonaje() {
		Iterator<PersonajeEntity> personajeIterator = personajesEnSerie
			.iterator();

		while (personajeIterator.hasNext()) {
			PersonajeEntity personaje = personajeIterator.next();
			personaje.getSeries().remove(this);
			personajeIterator.remove();
		}
	}

	public void addGenero(GeneroEntity generoEntity) {
		this.generosDeLaSerie.add(generoEntity);
		generoEntity.getSeries().add(this);
	}

	public void removeGenero(long generoId) {
		GeneroEntity generoEntity = this.generosDeLaSerie.stream()
			.filter(t -> t.getId() == generoId).findFirst()
			.orElse(null);

		if (generoEntity != null) {
			this.generosDeLaSerie.remove(generoEntity);
			generoEntity.getSeries().remove(this);
		}
	}

	public void removeAllGenero() {
		Iterator<GeneroEntity> generoIterator = generosDeLaSerie
			.iterator();

		while (generoIterator.hasNext()) {
			GeneroEntity genero = generoIterator.next();
			genero.getSeries().remove(this);
			generoIterator.remove();
		}
	}

	public boolean hasGeneroWithIdNumber(Long generoId) {
		for (GeneroEntity genero : generosDeLaSerie)
			if (Objects.equals(genero.getId(), generoId))
				return true;
		return false;
	}
	
	public boolean hasPersonajeWithId(Long personajeId) {
		for(PersonajeEntity personaje : personajesEnSerie)
			if(Objects.equals(personaje.getId(), personajeId))
				return true;
		return false;
	}

}

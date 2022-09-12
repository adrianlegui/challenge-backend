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
@Table(name = "pelicula")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PeliculaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	private String titulo;
	private LocalDate fechaDeCreacion;
	private Integer calificacion;
	
	@ManyToMany
	@JoinTable(
			name = "pelicula_personaje",
			joinColumns = @JoinColumn(
					name = "pelicula_id",
					referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
					name = "personaje_id",
					referencedColumnName = "id")
			)
	private Set<PersonajeEntity> personajesEnPelicula = new HashSet<>();
	
	@ManyToMany
	@JoinTable(
			name = "pelicula_genero",
			joinColumns = @JoinColumn(
					name = "pelicula_id",
					referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
					name = "genero_id",
					referencedColumnName = "id")
			)
	private Set<GeneroEntity> generosDeLaPelicula = new HashSet<>();
	
	public void addPersonaje(PersonajeEntity... entities) {
		for(PersonajeEntity personaje : entities) {
			this.personajesEnPelicula.add(personaje);
			personaje.getPeliculas().add(this);
		}
	}
	
	public void removePersonaje(Long... personajeIds) {
		for(Long personajeId : personajeIds) {
			PersonajeEntity personajeEntity = this.personajesEnPelicula.stream().filter(t -> Objects.equals(t.getId(), personajeId)).findFirst().orElse(null);
		    
			if (personajeEntity != null) {
		      this.personajesEnPelicula.remove(personajeEntity);
		      personajeEntity.getPeliculas().remove(this);
		      }
		}
	}
	
	public void removeAllPersonaje() {
		Iterator<PersonajeEntity> iteratorPersonaje = personajesEnPelicula.iterator();
		
		while(iteratorPersonaje.hasNext()) {
			PersonajeEntity personaje = iteratorPersonaje.next();
			personaje.getPeliculas().remove(this);
			iteratorPersonaje.remove();
		}
	}
	
	public void addGenero(GeneroEntity... entities) {
		for(GeneroEntity genero : entities) {
			this.generosDeLaPelicula.add(genero);
			genero.getPeliculas().add(this);
		}
	}
	
	public void removeGenero(Long... generoIds) {
		for(Long generoId : generoIds) {
			GeneroEntity generoEntity = this.generosDeLaPelicula.stream().filter(t -> Objects.equals(t.getId(), generoId)).findFirst().orElse(null);
		    
			if (generoEntity != null) {
		      this.generosDeLaPelicula.remove(generoEntity);
		      generoEntity.getPeliculas().remove(this);
		      }
		}
	}
	
	
	public void removeAllGenero() {
		Iterator<GeneroEntity> generoIterator = generosDeLaPelicula.iterator();
		
		while(generoIterator.hasNext()) {
			GeneroEntity genero = generoIterator.next();
			genero.getPeliculas().remove(this);
			generoIterator.remove();
		}
	}
	
	
	public boolean hasGeneroWithIdNumber(Long idGenero) {
		for(GeneroEntity genero : generosDeLaPelicula)
			if(Objects.equals(genero.getId(), idGenero))
				return true;
		return false;
	}

}


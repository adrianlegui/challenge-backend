package com.github.adrianlegui.challengebackendspring.entities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "personaje")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PersonajeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	private String imagen;
	private String nombre;
	private Integer edad;
	private Float peso;
	private String historia;
	
	@ManyToMany(mappedBy = "personajesEnPelicula")
	private Set<PeliculaEntity> peliculas = new HashSet<>();
	
	@ManyToMany(mappedBy = "personajesEnSerie")
	private Set<SerieEntity> series = new HashSet<>();
	
	public boolean hasMovieWithIdNumber(Long id) {
		for(PeliculaEntity pelicula : peliculas)
			if(Objects.equals(pelicula.getId(), id))
				return true;
		return false;
	}
	
	public boolean hasSerieWithIdNumber(Long id) {
		for(SerieEntity serie : series)
			if(Objects.equals(serie.getId(), id))
				return true;
		return false;
	}
	
	public void addPelicula(PeliculaEntity... entities) {
		for(PeliculaEntity pelicula : entities) {
			this.peliculas.add(pelicula);
			pelicula.getPersonajesEnPelicula().add(this);
		}
	}
	
	public void removePelicula(Long... peliculaIds) {
		for(Long peliculaId : peliculaIds) {
			PeliculaEntity peliculaEntity = this.peliculas.stream().filter(t -> Objects.equals(t.getId(), peliculaId)).findFirst().orElse(null);
		    
			if (peliculaEntity != null) {
		      this.peliculas.remove(peliculaEntity);
		      peliculaEntity.getPersonajesEnPelicula().remove(this);
		      }
		}
	}
	
	public void removeAllPelicula() {
		Iterator<PeliculaEntity> iteratorPelicula = peliculas.iterator();
		
		while(iteratorPelicula.hasNext()) {
			PeliculaEntity pelicula = iteratorPelicula.next();
			pelicula.getPersonajesEnPelicula().remove(this);
			iteratorPelicula.remove();
		}
	}
	
	public void addSerie(SerieEntity... entities) {
		for(SerieEntity serie : entities) {
			this.series.add(serie);
			serie.getPersonajesEnSerie().add(this);
		}
	}
	
	public void removeSerie(Long... serieIds) {
		for(Long serieId : serieIds) {
			SerieEntity serie = this.series.stream().filter(t -> Objects.equals(t.getId(), serieId)).findFirst().orElse(null);
		    
			if (serie != null) {
		      this.series.remove(serie);
		      serie.getPersonajesEnSerie().remove(this);
		      }
		}
	}
	
	public void removeAllSerie() {
		Iterator<SerieEntity> iteratorSerie = series.iterator();
		
		while(iteratorSerie.hasNext()) {
			SerieEntity serie = iteratorSerie.next();
			serie.getPersonajesEnSerie().remove(this);
			iteratorSerie.remove();
		}
	}
}

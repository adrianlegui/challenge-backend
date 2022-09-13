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
@Table(name = "genero")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GeneroEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	String nombre;
	String imagen;
	
	@ManyToMany(mappedBy = "generosDeLaPelicula")
	private Set<PeliculaEntity> peliculas  = new HashSet<>();
	
	@ManyToMany(mappedBy = "generosDeLaSerie")
	private Set<SerieEntity> series  = new HashSet<>();
	
	
	public void addPelicula(PeliculaEntity... entities) {
		for(PeliculaEntity pelicula : entities) {
			this.peliculas.add(pelicula);
			pelicula.getGenerosDeLaPelicula().add(this);
		}
	}
	
	
	public void removePelicula(Long... peliculaIds) {
		for(Long peliculaId : peliculaIds) {
			PeliculaEntity pelicula = 
					this.peliculas.stream()
					.filter(
							p -> Objects.equals(p.getId(), peliculaId)
							)
					.findFirst()
					.orElse(null);
			
			if(pelicula != null) {
				this.peliculas.remove(pelicula);
				pelicula.getGenerosDeLaPelicula().remove(this);
			}
		}
	}
	
	
	public void removeAllPelicula() {
		Iterator<PeliculaEntity> peliculaIterator = peliculas.iterator();
		
		while(peliculaIterator.hasNext()) {
			PeliculaEntity pelicula = peliculaIterator.next();
			pelicula.getGenerosDeLaPelicula().remove(this);
			peliculaIterator.remove();
		}
	}
	
	
	public void addSerie(SerieEntity... entities) {
		for(SerieEntity serie : entities) {
			this.series.add(serie);
			serie.getGenerosDeLaSerie().add(this);
		}
	}
	
	
	public void removeSerie(Long... generoIds) {
		for(Long generoId : generoIds) {
			SerieEntity serie = 
					series.stream()
					.filter(
							s -> Objects.equals(s.getId(), generoId)
							)
					.findFirst()
					.orElse(null);
			
			if(serie != null) {
				series.remove(serie);
				serie.getGenerosDeLaSerie().remove(this);
			}
		}
	}
	
	
	public void removeAllSerie() {
		Iterator<SerieEntity> serieIterator = series.iterator();
		
		while(serieIterator.hasNext()) {
			SerieEntity serie = serieIterator.next();
			serie.getGenerosDeLaSerie().remove(this);
			serieIterator.remove();
		}
	}
}

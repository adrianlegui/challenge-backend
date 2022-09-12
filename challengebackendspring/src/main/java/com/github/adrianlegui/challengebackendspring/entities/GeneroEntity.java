package com.github.adrianlegui.challengebackendspring.entities;

import java.util.HashSet;
import java.util.Iterator;
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
	
	public void removeAllPelicula() {
		Iterator<PeliculaEntity> peliculaIterator = peliculas.iterator();
		
		while(peliculaIterator.hasNext()) {
			PeliculaEntity pelicula = peliculaIterator.next();
			pelicula.getGenerosDeLaPelicula().remove(this);
			peliculaIterator.remove();
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

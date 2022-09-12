package com.github.adrianlegui.challengebackendspring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.adrianlegui.challengebackendspring.dto.GeneroDTO;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOGET;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPATCH;
import com.github.adrianlegui.challengebackendspring.dto.GeneroDTOPOST;
import com.github.adrianlegui.challengebackendspring.mappers.GeneroMapper;
import com.github.adrianlegui.challengebackendspring.repositories.GeneroRepository;

@Service
public class GeneroService {
	@Autowired
	private GeneroRepository generoRepository;
	
	@Autowired
	private GeneroMapper generoMapper;
	
	public GeneroDTO create(GeneroDTOPOST GeneroDTOPOST) {
		return null;
	}
	
	public GeneroDTOGET findById(Long id) {
		return null;
	}
	
	public List<GeneroDTOGET> findAll(){
		return null;
	}
	
	public GeneroDTO update(GeneroDTOPATCH generoDTOPATCH) {
		return null;
	}
	
	public void deleteById(Long id) {
		
	}
	
	public void deleteAll() {
		
	}
}

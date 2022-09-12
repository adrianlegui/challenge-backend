package com.github.adrianlegui.challengebackendspring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.adrianlegui.challengebackendspring.entities.SerieEntity;

@Repository
public interface SerieRepository extends JpaRepository<SerieEntity, Long> {
}

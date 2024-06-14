package com.dentsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Groupe;
import com.dentsbackend.entities.Professor;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
  List<Groupe> findByProfessor(Professor professor);

  Page<Groupe> findByProfessorContaining(Professor professor, Pageable pageable);

  // Assuming professorRepository has a method for searching professors by name
  Page<Groupe> findByProfessorUserNameContainingIgnoreCase(String nom, Pageable pageable);

  Page<Groupe> findByYearContaining(String year, Pageable pageable);

  public Groupe findByCode(String code);
  

  public List<Groupe> findByYear(String year);

}

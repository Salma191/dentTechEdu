package com.dentsbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Professor;
import java.util.List;




@Repository
public interface ProfessorRepository extends JpaRepository <Professor,Long> {
   Professor findByUserName(String userName);
   List<Professor> findByLastName(String lastName);
   Page<Professor> findByLastNameContainingIgnoreCase(String nom, Pageable pageable);
   Page<Professor> findByFirstNameContainingIgnoreCase(String nom, Pageable pageable);
}

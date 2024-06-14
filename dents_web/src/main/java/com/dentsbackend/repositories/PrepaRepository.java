package com.dentsbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Preparation;
import com.dentsbackend.entities.Tooth;

@Repository
public interface PrepaRepository extends JpaRepository<Preparation,Long> {

    Page<Preparation> findByTypeContaining(String nom, Pageable pageable);

    
  
}

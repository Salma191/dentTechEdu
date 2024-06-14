package com.dentsbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Tooth;

@Repository
public interface ToothRepository extends JpaRepository<Tooth,Long>{

    Page<Tooth> findByNameContaining(String nom, Pageable pageable);
    Page<Tooth> findByCodeContaining(String nom, Pageable pageable);
    
}

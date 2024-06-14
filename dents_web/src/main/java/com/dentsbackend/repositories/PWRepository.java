package com.dentsbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Groupe;
import com.dentsbackend.entities.PW;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface PWRepository extends JpaRepository<PW,Long>{
    public List<PW> findByGroupesCode(String code);
    public List<PW> findByGroupesId(int id);
    public List<PW> findByGroupes(Groupe groupe);
    Page<PW> findByGroupesCodeContaining(String groupe, Pageable pageable);
    Page<PW> findByTitleContaining(String nom, Pageable pageable);
    Page<PW> findByTitleIgnoreCaseContainingOrPreparationTypeIgnoreCaseContaining(String a,String b,Pageable p);
}

package com.dentsbackend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Groupe;
import com.dentsbackend.entities.Professor;
import com.dentsbackend.entities.Student;
import com.dentsbackend.entities.StudentPW;
import com.dentsbackend.entities.StudentPWPK;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface StudentPWRepository extends JpaRepository<StudentPW,StudentPWPK>{
    @Query("SELECT spw FROM StudentPW spw WHERE spw.id.student_id = :studentId")
    List<StudentPW> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT spw FROM StudentPW spw WHERE spw.id.student_id = :studentId")
    Page<StudentPW> findByStudentIdContaining(@Param("studentId") Long studentId, Pageable pageable);

    @Query("SELECT spw FROM StudentPW spw INNER JOIN spw.student s INNER JOIN s.groupe g WHERE g.professor = ?1")
    Page<StudentPW> findByProfessor(Professor professor, Pageable pageable);

    @Query("SELECT spw FROM StudentPW spw INNER JOIN spw.student s INNER JOIN s.groupe g WHERE g = ?1")
    Page<StudentPW> findByGroupe(Groupe groupe, Pageable pageable);
    @Query("SELECT spw FROM StudentPW spw WHERE spw.student = ?1")
Page<StudentPW> findByStudent(Student student, Pageable pageable);


    
}

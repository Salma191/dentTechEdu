package com.dentsbackend.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dentsbackend.entities.Groupe;
import com.dentsbackend.entities.Professor;
import com.dentsbackend.entities.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    public Student  findByUserName(String userName);
    public Student findByEmail(String email);
    public List<Student> findByGroupe(Groupe groupe);
    public List<Student> findByGroupeId(Long id);
    public Page<Student> findByGroupe(Groupe code,Pageable page);

    public Page<Student> findByGroupeCodeContainingIgnoreCase(String code,Pageable page);
    public Page<Student> findByGroupeCodeContaining(String code,Pageable page);
    List<Student> findByGroupeStudents(Student s);

    @Query("SELECT s FROM Student s JOIN s.groupe g WHERE g.professor = ?1")
    Page<Student> findStudentsByProfessor(Professor professor, Pageable pageable);

    Page<Student> findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String firstName, String lastName, Pageable pageable);
   
   

}
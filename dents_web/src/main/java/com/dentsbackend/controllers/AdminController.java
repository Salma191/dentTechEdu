package com.dentsbackend.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.internal.util.type.PrimitiveWrapperHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.dentsbackend.repositories.PWRepository;
import com.dentsbackend.repositories.PrepaRepository;
import com.dentsbackend.entities.Groupe;
import com.dentsbackend.entities.PW;
import com.dentsbackend.entities.Preparation;
import com.dentsbackend.entities.Professor;
import com.dentsbackend.entities.Role;
import com.dentsbackend.entities.Student;
import com.dentsbackend.entities.StudentPW;
import com.dentsbackend.entities.StudentPWPK;
import com.dentsbackend.entities.Tooth;
import com.dentsbackend.repositories.GroupeRepository;
import com.dentsbackend.repositories.ProfessorRepository;
import com.dentsbackend.repositories.RoleRepository;
import com.dentsbackend.repositories.StudentPWRepository;
import com.dentsbackend.repositories.StudentRepository;
import com.dentsbackend.repositories.ToothRepository;
import com.dentsbackend.services.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/prof")
public class AdminController {

  @Autowired
  ProfessorRepository professorRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  ToothRepository ToothRepository;

  @Autowired
  PWRepository PWRepository;
  @Autowired
  PrepaRepository prepaRepository;

  @Autowired
  StudentPWRepository spr;


  

  // ...............................................gestion du
  // profile...............................................

  @GetMapping
  public String prof(Model model, Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      int groupes = groupeRepository.findByProfessor(prof1).size();
      model.addAttribute("groupes", groupes);
      List<Student> students = new ArrayList<>();
      for (Groupe groupe : groupeRepository.findByProfessor(prof1)) {
        students.addAll(studentRepository.findByGroupe(groupe));
      }
      int nbr = students.size();
      model.addAttribute("students", nbr);
      int pws = PWRepository.findAll().size();
      model.addAttribute("pws", pws);
      int dents = ToothRepository.findAll().size();
      model.addAttribute("dents", dents);

      List<String> titles = new ArrayList<>();
      List<Integer> nbrgr = new ArrayList<>();
      List<Integer> nbrtp = new ArrayList<>();

      for (Groupe groupe : groupeRepository.findByProfessor(prof1)) {
        titles.add(groupe.getCode());
        nbrgr.add(studentRepository.findByGroupe(groupe).size());
      }

      for (Groupe groupe : groupeRepository.findByProfessor(prof1)) {
        nbrtp.add(PWRepository.findByGroupes(groupe).size());
      }
      model.addAttribute("nbrtp", nbrtp);
      model.addAttribute("titles", titles);
      model.addAttribute("nbrgr", nbrgr);

      model.addAttribute("professor", prof1);

    }
    return "prof";

  }

  @GetMapping("/profile")
  public String profile(Model model, Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();

      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("userdetail", userDetails);
      model.addAttribute("professor", prof1);

    }
    return "profile";

  }

  @PostMapping("/update/{id}")
  public String updateProfessor(@PathVariable("id") long id, Professor professor, Model model,
      @RequestParam("file") MultipartFile photoFile, Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Optional<Role> role = roleRepository.findByName("ROLE_PROFESSOR");
      Role roole = role.get();
      professor.setRoles(Set.of(roole));

      professor.setId(id);
      String a = prof.get().getPassword();
      professor.setPassword(a);
      professor.setPhoto(prof.get().getPhoto());
      if (photoFile != null && !photoFile.isEmpty()) {
        try {
          byte[] photoBytes = photoFile.getBytes();
          professor.setPhoto(photoBytes);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      professorRepository.save(professor);
    }
    return "redirect:/prof";
  }

  // ...............................................gestion du
  // groupe...............................................

  @Autowired
  GroupeRepository groupeRepository;

  @GetMapping("/groupe")
  public String groupe(Groupe groupe, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> profOptional = professorRepository.findById(userDetails.getId());

      if (profOptional.isPresent()) {
        Professor prof1 = profOptional.get();
        byte[] photo = prof1.getPhoto();

        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }

        Set<String> years = new HashSet<>();
        Pageable pageable = PageRequest.of(page, 9); // 10 éléments par page, ajustez selon vos besoins

        // Fetch professors with pagination
        Page<Groupe> professorPage = groupeRepository.findByProfessorUserNameContainingIgnoreCase(prof1.getUserName(),
            pageable);

        List<Groupe> groupes = professorPage.getContent();
        List<Groupe> groupees = groupeRepository.findByProfessor(prof1);
        for (Groupe gr : groupees) {
          years.add(gr.getYear());
        }
        if(groupees.isEmpty()){
          model.addAttribute("msg","No groups at that moment");
          model.addAttribute("page", "p");
        model.addAttribute("groupes", null);
        model.addAttribute("professor", prof1);
        }else{

        model.addAttribute("years", years);
        model.addAttribute("page", "p");
        model.addAttribute("groupes", groupes);
        model.addAttribute("professor", prof1);
        model.addAttribute("professorPage", professorPage);
        model.addAttribute("pageInfo", professorPage.getPageable());
        }
      }
    }
    return "groupe";
  }

  @GetMapping("/addg")
  public String showAddForm(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();

      Set<String> years = new HashSet<>();
      Pageable pageable = PageRequest.of(page, 9); // 10 éléments par page, ajustez selon vos besoins

      // Fetch professors with pagination
      Page<Groupe> professorPage = groupeRepository.findByProfessorUserNameContainingIgnoreCase(prof1.getUserName(),
          pageable);

      List<Groupe> groupes = professorPage.getContent();
      List<Groupe> groupees = groupeRepository.findByProfessor(prof1);
      for (Groupe gr : groupees) {
        years.add(gr.getYear());
      }
      model.addAttribute("years", years);
      model.addAttribute("groupes", groupes);
      model.addAttribute("page", "p");
      model.addAttribute("professor", prof1);
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("professor", prof1);

      model.addAttribute("mode", "add");
      model.addAttribute("groupe", new Groupe());

    }
    return "groupe";

  }

  @PostMapping("/addgroupe")
  public String savegroupe(Groupe groupe, Model model, Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Optional<Professor> prof = professorRepository.findById(userDetails.getId());

    Professor prof1 = prof.get();
    System.out.println(prof1.getEmail());

    groupe.setProfessor(prof1);
    groupeRepository.save(groupe);
    return "redirect:/prof/groupe";
  }

  @GetMapping("/editg/{id}")
  public String editProfesssor(@PathVariable("id") long id, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);

      Groupe groupe = groupeRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

      Set<String> years = new HashSet<>();
      Pageable pageable = PageRequest.of(page, 9); // 10 éléments par page, ajustez selon vos besoins

      // Fetch professors with pagination
      Page<Groupe> professorPage = groupeRepository.findByProfessorUserNameContainingIgnoreCase(prof1.getUserName(),
          pageable);

      List<Groupe> groupes = professorPage.getContent();
      List<Groupe> groupees = groupeRepository.findByProfessor(prof1);
      for (Groupe gr : groupees) {
        years.add(gr.getYear());
      }

      model.addAttribute("years", years);
      model.addAttribute("groupes", groupes);
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("professor", prof1);
      model.addAttribute("page", "p");

      model.addAttribute("groupe", groupe);
      model.addAttribute("mode", "update");
    }
    return "groupe";
  }

  @PostMapping("/updategroupe/{id}")
  public String updateProfessor(@PathVariable("id") long id, Groupe groupe, Model model,
      Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Optional<Professor> prof = professorRepository.findById(userDetails.getId());
    Professor prof1 = prof.get();

    groupe.setId(id);
    groupe.setProfessor(prof1);

    groupeRepository.save(groupe);

    return "redirect:/prof/groupe";
  }

  @GetMapping("/deletegroupe/{id}")
  public String deleteProfessor(@PathVariable("id") long id, Model model, Authentication authentication,
      HttpServletRequest request) {
    try {
      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<Professor> prof = professorRepository.findById(userDetails.getId());
        Professor prof1 = prof.get();
        model.addAttribute("professor", prof1);
      }

      Groupe group = groupeRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
      groupeRepository.delete(group);

      String referer = request.getHeader("Referer");
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(referer);
      String pageParam = request.getParameter("page");
      model.addAttribute("page", "p");
      if (pageParam != null) {
        builder.replaceQueryParam("page", pageParam);
      }

      // Redirection vers la page précédente avec le même numéro de page
      return "redirect:" + builder.toUriString();

    } catch (Exception e) {
      e.printStackTrace();
      return "error500";
    }
  }

  @GetMapping("/groupeByYear")
  public String groupeByYear(@RequestParam(value = "year", required = false) String year, Model model,
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
      byte[] photo = prof1.getPhoto();

      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }

      Set<String> years = new HashSet<>();
      List<Groupe> groupees = groupeRepository.findByProfessor(prof1);
      for (Groupe gr : groupees) {
        years.add(gr.getYear());
      }
      model.addAttribute("years", years);

      Pageable pageable = PageRequest.of(page, 9); // 10 éléments par page, ajustez selon vos besoins
      // Fetch professors with pagination
      Page<Groupe> professorPage = groupeRepository.findByYearContaining(year, pageable);
      List<Groupe> groupes = professorPage.getContent();
      model.addAttribute("groupes", groupes);
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("page", "b");
      model.addAttribute("year", year);
    }

    return "groupe";
  }

  @GetMapping("/groupe/searchByNom")
  public String searchByNomgroupe(@RequestParam(name = "a") String nom, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);

      Groupe groupe = null;
      if (nom.isEmpty()) {

        return "redirect:/prof/groupe";

      } else {
        Pageable pageable = PageRequest.of(page, 200);
        Page<Groupe> professorPage = groupeRepository.findByProfessorUserNameContainingIgnoreCase(prof1.getUserName(),
            pageable);
        List<Groupe> groupes = professorPage.getContent();
        Set<String> years = new HashSet<>();

        // Fetch professors with pagination
        List<Groupe> groupees = groupeRepository.findByProfessor(prof1);
        for (Groupe gr : groupees) {
          years.add(gr.getYear());
        }

        model.addAttribute("years", years);
        for (Groupe gr : groupes) {
          if (gr.getCode().equalsIgnoreCase(nom)) {
            groupe = gr;
          }
        }
        if (groupe == null) {
          model.addAttribute("msg", "No groupe with this title");
        } else {
          model.addAttribute("professorPage", professorPage);
          model.addAttribute("pageInfo", professorPage.getPageable());
          model.addAttribute("groupes", groupe);
          model.addAttribute("page", "p");

        }
      }
    }
    return "groupe";
  }

  // ...............................................gestion des
  // etudiants...............................................

  @Autowired
  StudentRepository studentRepository;

  @GetMapping("/etudiant")
  public String etudiant(Student student, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);

      Pageable pageable = PageRequest.of(page, 6); // Afficher seulement 6 étudiants par page

      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);

      // Créer une nouvelle page à partir de la liste fusionnée de tous les étudiants
      Page<Student> professorPage = studentRepository.findStudentsByProfessor(prof1, pageable);
      List<Student> students = professorPage.getContent();

      List<String> encodedPhotos = new ArrayList<>();
      for (Student stu : students) {
        byte[] photo1 = stu.getPhoto();
        if (photo1 != null && photo1.length > 0) {
          String encodedPhot = Base64.getEncoder().encodeToString(photo1);
          encodedPhotos.add(encodedPhot);
        } else {
          encodedPhotos.add("");
        }
      }
      if(students.isEmpty()){
        model.addAttribute("msg", "No students at that moment");
        model.addAttribute("groupes", groupes);
        model.addAttribute("page", "p");
      model.addAttribute("students", null);

      }else{
      model.addAttribute("groupes", groupes);
      model.addAttribute("page", "p");
      model.addAttribute("students", students);
      model.addAttribute("encodedPhotos", encodedPhotos);

      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      }

    }
    return "etudiant";
  }

  @GetMapping("/searchByNometu")
  public String searchByNometu(@RequestParam(value = "a", required = false) String nom, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }

      model.addAttribute("professor", prof1);

      if (nom=="") {
        return "redirect:/prof/etudiant";
      } else {
        Pageable pageable = PageRequest.of(page, 6);
        List<Student> studentss = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
        Page<Student> professorPage = studentRepository.findByFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(nom, nom,pageable);
        students = professorPage.getContent();

       

        // Créer une nouvelle page à partir de la liste fusionnée de tous les étudiants

        List<String> encodedPhotos = new ArrayList<>();
        for (Student stu : students) {
          byte[] photo1 = stu.getPhoto();
          if (photo1 != null && photo1.length > 0) {
            String encodedPhot = Base64.getEncoder().encodeToString(photo1);
            encodedPhotos.add(encodedPhot);
          } else {
            encodedPhotos.add("");
          }
        }
        if (students.isEmpty()) {
          model.addAttribute("msg", "No students with this last or first name");
        } else {
          model.addAttribute("professorPage", professorPage);
          model.addAttribute("pageInfo", professorPage.getPageable());
          model.addAttribute("students", students);
          model.addAttribute("page", "m");
          model.addAttribute("nom",nom);

        }
        model.addAttribute("groupes", groupes);

        model.addAttribute("encodedPhotos", encodedPhotos);

      }
    }
    return "etudiant";

  }

  @GetMapping("/adde")
  public String showAddetudiant(Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      List<Student> students = new ArrayList<>();

      Pageable pageable = PageRequest.of(page, 6);
      Page<Student> professorPage = studentRepository.findStudentsByProfessor(prof1, pageable);
      students = professorPage.getContent();
      List<String> encodedPhotos = new ArrayList<>();
      for (Student stu : students) {
        byte[] photo1 = stu.getPhoto();
        if (photo1 != null && photo1.length > 0) {
          String encodedPhot = Base64.getEncoder().encodeToString(photo1);
          encodedPhotos.add(encodedPhot);
        } else {
          encodedPhotos.add("");
        }
      }
      model.addAttribute("students", students);
      model.addAttribute("encodedPhotos", encodedPhotos);

      model.addAttribute("professor", prof1);
      model.addAttribute("mode", "add");
      model.addAttribute("student", new Student());
      model.addAttribute("groupes", groupeRepository.findByProfessor(prof1));
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("page", "p");

    }
    return "etudiant";

  }

  @PostMapping("/addetudiant")
  public String save(Student student, Model model, @RequestParam("file") MultipartFile photoFile) {

    if (photoFile != null && !photoFile.isEmpty()) {
      try {
        byte[] photoBytes = photoFile.getBytes();
        student.setPhoto(photoBytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    studentRepository.save(student);

    return "redirect:/prof/etudiant";
  }

  @GetMapping("/edite/{id}")
  public String editetudiant(@PathVariable("id") long id, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Optional<Professor> prof = professorRepository.findById(userDetails.getId());
    Professor prof1 = prof.get();
    model.addAttribute("professor", prof1);

    Student student = studentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

    if (student.getPhoto() != null) {
      String encodedPhot = Base64.getEncoder().encodeToString(student.getPhoto());
      model.addAttribute("encodedPhoto", encodedPhot);

    }
    List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
    Pageable pageable = PageRequest.of(page, 6);
    List<Student> students = new ArrayList<>();

    Page<Student> professorPage = studentRepository.findStudentsByProfessor(prof1, pageable);
    students = professorPage.getContent();
    List<String> encodedPhotos = new ArrayList<>();
    for (Student stu : students) {
      byte[] photo1 = stu.getPhoto();
      if (photo1 != null && photo1.length > 0) {
        String encodedPhot = Base64.getEncoder().encodeToString(photo1);
        encodedPhotos.add(encodedPhot);
      } else {
        encodedPhotos.add("");
      }
    }
    model.addAttribute("students", students);
    model.addAttribute("encodedPhotos", encodedPhotos);
    model.addAttribute("student", student);
    model.addAttribute("mode", "update");
    model.addAttribute("groupes", groupes);
    model.addAttribute("professorPage", professorPage);
    model.addAttribute("pageInfo", professorPage.getPageable());
    model.addAttribute("page", "p");

    return "etudiant";
  }

  @PostMapping("/updateetudiant/{id}")
  public String updateedutiant(@PathVariable("id") long id, Student student, Model model,
      @RequestParam("file") MultipartFile photoFile, @RequestParam(value = "groupe", required = false) Groupe groupe) {
    Optional<Student> std = studentRepository.findById(student.getId());
    if (groupe == null) {
      Optional<Student> s = studentRepository.findById(id);
      student.setGroupe(s.get().getGroupe());

    } else {
      Groupe grp = groupeRepository.findById(groupe.getId())
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

      if (grp != null) {
        student.setGroupe(grp);
      }
    }

    // Vérifier si un nouveau fichier d'image est fourni
    if (photoFile != null && !photoFile.isEmpty()) {
      try {
        byte[] photoBytes = photoFile.getBytes();
        student.setPhoto(photoBytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // Aucun fichier n'a été téléchargé, conservez l'image existante du professeur
      student.setPhoto(std.get().getPhoto());
    }

    studentRepository.save(student);

    return "redirect:/prof/etudiant";
  }

  @GetMapping("/deleteetudiant/{id}")
  public String deleteEtudiant(@PathVariable("id") long id, Model model, Authentication authentication,
      HttpServletRequest request) {
    try {
      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<Professor> prof = professorRepository.findById(userDetails.getId());
        Professor prof1 = prof.get();
        model.addAttribute("professor", prof1);
      }

      Student student = studentRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
      studentRepository.delete(student);

      String referer = request.getHeader("Referer");
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(referer);
      String pageParam = request.getParameter("page");
      if (pageParam != null) {
        builder.replaceQueryParam("page", pageParam);
      }

      // Redirection vers la page précédente avec le même numéro de page
      return "redirect:" + builder.toUriString();
    } catch (Exception e) {
      e.printStackTrace();
      return "error500";
    }
  }

  @GetMapping("/studentByGroupe")
  public String studentByGroupe(@RequestParam(value = "groupe", required = false) String groupe, Model model,
      Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      model.addAttribute("groupes", groupes);
      byte[] photo = prof1.getPhoto();

      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
    }
    Pageable pageable = PageRequest.of(page, 6);

    Page<Student> professorPage = studentRepository.findByGroupeCodeContaining(groupe, pageable);
    List<Student> students = professorPage.getContent();
    List<String> encodedPhotos = new ArrayList<>();
    for (Student stu : students) {
      byte[] photo1 = stu.getPhoto();
      if (photo1 != null && photo1.length > 0) {
        String encodedPhot = Base64.getEncoder().encodeToString(photo1);
        encodedPhotos.add(encodedPhot);
      } else {
        encodedPhotos.add("");
      }
    }
    if (students.isEmpty()) {
      model.addAttribute("msg", "No students in this groupe");
    } else {
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("students", students);
      model.addAttribute("encodedPhotos", encodedPhotos);
      model.addAttribute("page", "b");
      model.addAttribute("groupe", groupe);

    }

    return "etudiant";
  }

  @GetMapping("/studentofGroupe")
  public String studentOfGroupe(@RequestParam(value = "groupe") String groupe, Model model,
      Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      model.addAttribute("groupes", groupes);
      byte[] photo = prof1.getPhoto();

      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
    }
    Pageable pageable = PageRequest.of(page, 6);

    Page<Student> professorPage = studentRepository.findByGroupeCodeContaining(groupe, pageable);
    List<Student> students = professorPage.getContent();
    List<String> encodedPhotos = new ArrayList<>();
    for (Student stu : students) {
      byte[] photo1 = stu.getPhoto();
      if (photo1 != null && photo1.length > 0) {
        String encodedPhot = Base64.getEncoder().encodeToString(photo1);
        encodedPhotos.add(encodedPhot);
      } else {
        encodedPhotos.add("");
      }
    }

    if (students.isEmpty()) {
      model.addAttribute("msg", "No students in this groupe");
    } else {
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("students", students);
      model.addAttribute("encodedPhotos", encodedPhotos);
      model.addAttribute("groupe", groupe);
      model.addAttribute("page", "p");

    }

    return "groupestudents";
  }

  // ...............................................gestion des
  // dents...............................................

  @GetMapping("/dent")
  public String dent(Tooth tooth, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      Pageable pageable = PageRequest.of(page, 12);
      Page<Tooth> professorPage = ToothRepository.findAll(pageable);
      List<Tooth> Tooths = professorPage.getContent();
      model.addAttribute("professor", prof1);

if(Tooths.isEmpty()){
  model.addAttribute("msg", "No theet for the moment");
}else{


      model.addAttribute("tooths", Tooths);
      
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
}
    }
    return "dent";
  }

  @GetMapping("/addd")
  public String showAddDentForm(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page,
      HttpServletRequest request) {

    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();

      model.addAttribute("mode", "add");
      model.addAttribute("tooth", new Tooth());

      String pageParam = request.getParameter("page");
      if(pageParam==null){
        pageParam="0";
      }
      int currentPage = Integer.parseInt(pageParam);
      int itemsPerPage = 12;
     

      Pageable pageable = PageRequest.of(currentPage, itemsPerPage);
      Page<Tooth> professorPage = ToothRepository.findAll(pageable);
      List<Tooth> Tooths = professorPage.getContent();
      model.addAttribute("professor", prof1);
      if(!Tooths.isEmpty()){
        model.addAttribute("tooths", Tooths); 
        model.addAttribute("professorPage", professorPage);
        model.addAttribute("pageInfo", professorPage.getPageable());
      }else{
        model.addAttribute("msg", "no teeth for the moment");
      }
      
    }

    return "dent";
  }

  @PostMapping("/adddent")
  public String savedent(Tooth tooth, Model model, Authentication authentication) {
    ToothRepository.save(tooth);
    return "redirect:/prof/dent";
  }

  @GetMapping("/editd/{id}")
  public String editdent(@PathVariable("id") long id, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
    }

    Tooth tooth = ToothRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Tooth Id:" + id));
    Pageable pageable = PageRequest.of(page, 12);
    Page<Tooth> professorPage = ToothRepository.findAll(pageable);
    List<Tooth> Tooths = professorPage.getContent();
    model.addAttribute("tooths", Tooths);

    model.addAttribute("tooth", tooth);
    model.addAttribute("mode", "update");
    model.addAttribute("professorPage", professorPage);
    model.addAttribute("pageInfo", professorPage.getPageable());

    return "dent";
  }

  @PostMapping("/updatedent/{id}")
  public String updatedent(@PathVariable("id") long id, Tooth tooth, Model model,
      Authentication authentication) {
    Tooth toth = ToothRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid tooth Id:" + id));

    if (tooth != null) {
      toth.setName(tooth.getName());
      toth.setCode(tooth.getCode());
    }
    ToothRepository.save(toth);

    return "redirect:/prof/dent";
  }

  @GetMapping("/deletedent/{id}")
  public String deletedent(@PathVariable("id") long id, Model model, Authentication authentication,
      HttpServletRequest request) {
    try {
      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<Professor> prof = professorRepository.findById(userDetails.getId());
        Professor prof1 = prof.get();
        model.addAttribute("professor", prof1);
      }

      Tooth tooth = ToothRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid Tooth Id:" + id));
      ToothRepository.delete(tooth);

      // Récupérer le numéro de page actuel depuis les paramètres de la requête
      String referer = request.getHeader("Referer");
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(referer);
      String pageParam = request.getParameter("page");
      if (pageParam != null) {
        builder.replaceQueryParam("page", pageParam);
      }

      // Redirection vers la page précédente avec le même numéro de page
      return "redirect:" + builder.toUriString();
    } catch (Exception e) {
      e.printStackTrace();
      return "error500";
    }
  }

  @GetMapping("/dent/searchByNom")
  public String searchByNomdent(@RequestParam(name = "a") String nom, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);
    }
    if (nom.isEmpty()) {
      Pageable pageable = PageRequest.of(page, 12);
      Page<Tooth> professorPage = ToothRepository.findAll(pageable);
      List<Tooth> Tooths = professorPage.getContent();
      model.addAttribute("tooths", Tooths);
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());

    } else {
      Pageable pageable = PageRequest.of(page, 12);
      Page<Tooth> professorPage = ToothRepository.findByNameContaining(nom, pageable);
      if (professorPage.isEmpty()) {
        professorPage = (ToothRepository.findByCodeContaining(nom, pageable));
      }
      List<Tooth> Tooths = professorPage.getContent();

      if (Tooths.isEmpty()) {
        model.addAttribute("msg", "No tooth with this name or code ");
      } else {
        model.addAttribute("tooths", Tooths);
        model.addAttribute("professorPage", professorPage);
        model.addAttribute("pageInfo", professorPage.getPageable());

      }
    }
    return "dent";
  }

  // ...............................................gestion des travaux
  // pratiques...............................................

  @GetMapping("/searchByNom")
  public String searchByNom(@RequestParam(name = "a") String nom, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      model.addAttribute("groupes", groupes);
    }
    List<String> encodedPdfs = new ArrayList<>();
    if (nom.isEmpty()) {
      Pageable pageable = PageRequest.of(page, 6); // 10 éléments par page, ajustez selon vos besoins

      // Fetch professors with pagination
      Page<PW> pwpage = PWRepository.findAll(pageable);

      List<PW> pws = pwpage.getContent();
      
      model.addAttribute("pws", pws);
      model.addAttribute("professorPage", pwpage);
      model.addAttribute("pageInfo", pwpage.getPageable());
      model.addAttribute("page", "r");

      for (PW pdf : pws) {
        byte[] pdf1 = pdf.getDocs();
        if (pdf1 != null && pdf1.length > 0) {
          String encodedPdf = Base64.getEncoder().encodeToString(pdf1);
          encodedPdfs.add(encodedPdf);
        } else {
          encodedPdfs.add("");
        }
      }
      model.addAttribute("pdfs", encodedPdfs);

    } else {
      Pageable pageable = PageRequest.of(page, 6); // 10 éléments par page, ajustez selon vos besoins
      Page<PW> pwpage = PWRepository.findByTitleIgnoreCaseContainingOrPreparationTypeIgnoreCaseContaining(nom,nom, pageable);
      List<PW> pws = pwpage.getContent();
      if (pws.isEmpty()) {
        model.addAttribute("msg", "No pw with this title");
      } else {
        model.addAttribute("pws", pws);
        model.addAttribute("professorPage", pwpage);
        model.addAttribute("pageInfo", pwpage.getPageable());
        model.addAttribute("page", "t");
        model.addAttribute("nom", nom);

        for (PW pdf : pws) {
          byte[] pdf1 = pdf.getDocs();
          if (pdf1 != null && pdf1.length > 0) {
            String encodedPdf = Base64.getEncoder().encodeToString(pdf1);
            encodedPdfs.add(encodedPdf);
          } else {
            encodedPdfs.add("");
          }
        }
        model.addAttribute("pdfs", encodedPdfs);
      }
    }
    return "PW";
  }

  @GetMapping("/pw")
  public String tp(PW pw, Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      model.addAttribute("groupes", groupes);
      Pageable pageable = PageRequest.of(page, 6);
      Page<PW> pwpage = PWRepository.findAll(pageable);
      List<PW> pws = pwpage.getContent();
      if(pws.isEmpty()){
        model.addAttribute("msg", "No practical work for the moment");
      }else{

      
      model.addAttribute("pws", pws);
      model.addAttribute("professorPage", pwpage);
      model.addAttribute("pageInfo", pwpage.getPageable());
      model.addAttribute("page", "r");
      }
      List<String> encodedPdfs = new ArrayList<>();
      for (PW pdf : pws) {
        byte[] pdf1 = pdf.getDocs();
        if (pdf1 != null && pdf1.length > 0) {
          String encodedPdf = Base64.getEncoder().encodeToString(pdf1);
          encodedPdfs.add(encodedPdf);
        } else {
          encodedPdfs.add("");
        }
      }
      model.addAttribute("pdfs", encodedPdfs);

    }
    return "PW";
  }

  @GetMapping("/pwByGroupe")
  public String pwByGroupe(@RequestParam("groupe") String groupe, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      model.addAttribute("groupes", groupes);
      byte[] photo = prof1.getPhoto();

      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
        

      }
    }

    Pageable pageable = PageRequest.of(page, 6); // 10 éléments par page, ajustez selon vos besoins

    // Fetch professors with pagination
    Page<PW> pwpage = PWRepository.findByGroupesCodeContaining(groupe, pageable);

    List<PW> pws = pwpage.getContent();
    if (pws.isEmpty()) {
      model.addAttribute("msg", "This group has no practical work");

    } else {
      model.addAttribute("pws", pws);
      model.addAttribute("professorPage", pwpage);
      model.addAttribute("pageInfo", pwpage.getPageable());
      model.addAttribute("pws", pws);
      model.addAttribute("page","s");
      model.addAttribute("groupe",groupe);
      
    }
    return "PW";
  }

  @GetMapping("/addp")
  public String showAddTp(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      Pageable pageable = PageRequest.of(page, 6);
      Page<PW> pwpage = PWRepository.findAll(pageable);
      List<PW> pwss = pwpage.getContent();
      model.addAttribute("professorPage", pwpage);
      model.addAttribute("pageInfo", pwpage.getPageable());

      List<Tooth> tooths = ToothRepository.findAll();
      List<Preparation> prepas = prepaRepository.findAll();
      model.addAttribute("tooths", tooths);
      model.addAttribute("prepas", prepas);
      model.addAttribute("pws", pwss);
      model.addAttribute("professor", prof1);
      model.addAttribute("mode", "add");
      model.addAttribute("pw", new PW());
      model.addAttribute("page", "r");

    }
    return "PW";

  }

  @PostMapping("/addpw")
  public String saveTP(PW pw, Model model, @RequestParam("docFile") MultipartFile docFile) throws IOException {
    try {
      if (docFile != null && !docFile.isEmpty()) {
        byte[] docBytes = docFile.getBytes();
        pw.setDocs(docBytes);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    PWRepository.save(pw);
    return "redirect:/prof/pw";
  }

  // ..........................................

  // .......................................

  @GetMapping("/editp/{id}")
  public String editTP(@PathVariable("id") long id, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Optional<Professor> prof = professorRepository.findById(userDetails.getId());
    Professor prof1 = prof.get();
    model.addAttribute("professor", prof1);
    PW pw = PWRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    List<Tooth> tooths = ToothRepository.findAll();
    Pageable pageable = PageRequest.of(page, 6); // 10 éléments par page, ajustez selon vos besoins

    // Fetch professors with pagination
    Page<PW> pwpage = PWRepository.findAll(pageable);
    List<PW> pwss = pwpage.getContent();

    List<Preparation> prepas = prepaRepository.findAll();
    model.addAttribute("prepas", prepas);
    model.addAttribute("pws", pwss);
    model.addAttribute("pw", pw);
    model.addAttribute("tooths", tooths);
    model.addAttribute("mode", "update");
    model.addAttribute("professorPage", pwpage);
    model.addAttribute("pageInfo", pwpage.getPageable());
    model.addAttribute("page", "r");
    return "PW";
  }

  @PostMapping("/updatepw/{id}")
  public String updateTP(@PathVariable("id") long id, PW pw, Model model,
      @RequestParam(value = "tooth", required = false) Tooth tooth,
      @RequestParam(value = "prepa", required = false) Preparation prepa,
      @RequestParam("docFile") MultipartFile pdfFile) {

    Optional<PW> pw1 = PWRepository.findById(id);
 
    pw.setGroupes(pw1.get().getGroupes());

    if (tooth != null) {
      pw.setTooth(tooth);
      ;
    } else {
      pw.setTooth(pw1.get().getTooth());

    }
    if (prepa != null) {
      pw.setPreparation(prepa);
      ;
      ;
    } else {
      pw.setPreparation(pw1.get().getPreparation());

    }

    if (pdfFile != null && !pdfFile.isEmpty()) {
      try {
        byte[] photoBytes = pdfFile.getBytes();
        pw.setDocs(photoBytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // Aucun fichier n'a été téléchargé, conservez l'image existante du professeur
      pw.setDocs(pw1.get().getDocs());
    }

    PWRepository.save(pw);

    return "redirect:/prof/pw";
  }

  @GetMapping("/deletepw/{id}")
  public String deleteTp(@PathVariable("id") long id, Model model, Authentication authentication) {
    try {
      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<Professor> prof = professorRepository.findById(userDetails.getId());
        Professor prof1 = prof.get();
        model.addAttribute("professor", prof1);
      }

      PW pw = PWRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
      PWRepository.delete(pw);

      return "redirect:/prof/pw";
    } catch (Exception e) {
      e.printStackTrace();
      return "error500";
    }
  }

  // ...............................................gestion du tp avec
  // students...............................................

  @GetMapping("/studentpw")
  public String getPw(Authentication authentication, Model model, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);
        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
        System.out.println(groupes.size());
        model.addAttribute("groupes", groupes);
        List<StudentPW> allStudentPWs = new ArrayList<>();
        List<String> pwencodedPhotos1 = new ArrayList<>();
        List<String> pwencodedPhotos2 = new ArrayList<>();
        List<String> pwencodedPhotos3 = new ArrayList<>();

        Pageable pageable = PageRequest.of(page, 6);
        Page<StudentPW> pwpage = spr.findByProfessor(prof1, pageable);
        List<StudentPW> pws = pwpage.getContent();
        allStudentPWs.addAll(pws);

        if(allStudentPWs.isEmpty()){
          model.addAttribute("studentPWs", null);
          model.addAttribute("msg", "No Completed practical work at that moment");
       

        }else{
        model.addAttribute("professorPage", pwpage);
        model.addAttribute("pageInfo", pwpage.getPageable());
        model.addAttribute("type", "a");
        model.addAttribute("studentPWs", pws);
        
        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }
      }
      }
    }

    return "studentpw";
  }

  @GetMapping("/valider/{studentId}/{pwId}")
  public String editPw(Authentication authentication, Model model, @PathVariable("pwId") Long pwid,
      @PathVariable("studentId") Long studentid, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      List<String> pwencodedPhotos1 = new ArrayList<>();
      List<String> pwencodedPhotos2 = new ArrayList<>();
      List<String> pwencodedPhotos3 = new ArrayList<>();
      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);

        List<StudentPW> allStudentPWs = new ArrayList<>();

        for (Groupe grp : groupes) {
          List<Student> students = studentRepository.findByGroupe(grp);

          for (Student student : students) {
            Pageable pageable = PageRequest.of(page, 6);
            Page<StudentPW> pwpage = spr.findByStudentIdContaining(student.getId(), pageable);

            List<StudentPW> pws = pwpage.getContent();
            model.addAttribute("professorPage", pwpage);
            model.addAttribute("pageInfo", pwpage.getPageable());
            allStudentPWs.addAll(pws);
          }
        }
        model.addAttribute("studentPWs", allStudentPWs);

        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }

      }
      model.addAttribute("studentId", studentid);
      model.addAttribute("pwId", pwid);
      model.addAttribute("mode", "valider");
    }
    return "studentpw";
  }

  // ..........................................
  @GetMapping("/voir/{studentId}/{pwId}")
  public String voirPw(Authentication authentication, Model model, @PathVariable("pwId") Long pwid,
      @PathVariable("studentId") Long studentid, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      List<String> pwencodedPhotos1 = new ArrayList<>();
      List<String> pwencodedPhotos2 = new ArrayList<>();
      List<String> pwencodedPhotos3 = new ArrayList<>();
      StudentPWPK studentPWPK = new StudentPWPK();
      studentPWPK.setStudent_id(studentid);
      studentPWPK.setPw_id(pwid);
      StudentPW spww = spr.getById(studentPWPK);
      byte[] p1 = spww.getImage1();
      if (p1 != null) {
        String p11 = Base64.getEncoder().encodeToString(p1);
        model.addAttribute("p1", p11);
      }
      byte[] p2 = spww.getImage2();
      if (p2 != null) {
        String p22 = Base64.getEncoder().encodeToString(p2);
        model.addAttribute("p2", p22);
      }
      byte[] p3 = spww.getImage3();
      if (p3 != null) {
        String p33 = Base64.getEncoder().encodeToString(p3);
        model.addAttribute("p3", p33);
      }
      model.addAttribute("spww", spww);

      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);

        List<StudentPW> allStudentPWs = new ArrayList<>();

        for (Groupe grp : groupes) {
          List<Student> students = studentRepository.findByGroupe(grp);

          for (Student student : students) {
            Pageable pageable = PageRequest.of(page, 6);
            Page<StudentPW> pwpage = spr.findByStudentIdContaining(student.getId(), pageable);

            List<StudentPW> pws = pwpage.getContent();
            model.addAttribute("professorPage", pwpage);
            model.addAttribute("pageInfo", pwpage.getPageable());
            allStudentPWs.addAll(pws);
          }
        }
        model.addAttribute("studentPWs", allStudentPWs);

        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }

      }
      model.addAttribute("studentId", studentid);
      model.addAttribute("pwId", pwid);
      model.addAttribute("mode", "voir");
    }
    return "studentpw";
  }

  // ..........................................

  @GetMapping("/valider1/{studentId}/{pwId}")
  public String editPw1(Authentication authentication, Model model, @PathVariable("pwId") Long pwid,
      @PathVariable("studentId") Long studentid, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      model.addAttribute("id", studentid);
      List<String> pwencodedPhotos1 = new ArrayList<>();
      List<String> pwencodedPhotos2 = new ArrayList<>();
      List<String> pwencodedPhotos3 = new ArrayList<>();
      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);

        List<StudentPW> allStudentPWs = new ArrayList<>();
        model.addAttribute("page", "p");

        for (Groupe grp : groupes) {
          List<Student> students = studentRepository.findByGroupe(grp);

          for (Student student : students) {
            Pageable pageable = PageRequest.of(page, 6);
            Page<StudentPW> pwpage = spr.findByStudentIdContaining(student.getId(), pageable);

            List<StudentPW> pws = pwpage.getContent();
            model.addAttribute("professorPage", pwpage);
            model.addAttribute("pageInfo", pwpage.getPageable());
            allStudentPWs.addAll(pws);
          }
        }
        model.addAttribute("studentPWs", allStudentPWs);
        List<String> names = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          names.add(stpw.getPw().getTitle());

        }
        List<Integer> nbrs = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          nbrs.add(stpw.getNote());

        }
        model.addAttribute("names", names);
        model.addAttribute("nbrs", nbrs);

        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }

      }
      model.addAttribute("studentId", studentid);
      model.addAttribute("pwId", pwid);
      model.addAttribute("mode", "valider");
    }
    return "studentDetails";
  }

  @GetMapping("/voirdetail/{studentId}/{pwId}")
  public String voirdetail(Authentication authentication, Model model, @PathVariable("pwId") Long pwid,
      @PathVariable("studentId") Long studentid, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      model.addAttribute("id", studentid);
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      List<String> pwencodedPhotos1 = new ArrayList<>();
      List<String> pwencodedPhotos2 = new ArrayList<>();
      List<String> pwencodedPhotos3 = new ArrayList<>();
      StudentPWPK studentPWPK = new StudentPWPK();
      studentPWPK.setStudent_id(studentid);
      studentPWPK.setPw_id(pwid);
      StudentPW spww = spr.getById(studentPWPK);
      byte[] p1 = spww.getImage1();
      if (p1 != null) {
        String p11 = Base64.getEncoder().encodeToString(p1);
        model.addAttribute("p1", p11);
      }
      byte[] p2 = spww.getImage2();
      if (p2 != null) {
        String p22 = Base64.getEncoder().encodeToString(p2);
        model.addAttribute("p2", p22);
      }
      byte[] p3 = spww.getImage3();
      if (p3 != null) {
        String p33 = Base64.getEncoder().encodeToString(p3);
        model.addAttribute("p3", p33);
      }
      model.addAttribute("spww", spww);
      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);

        List<StudentPW> allStudentPWs = new ArrayList<>();
        model.addAttribute("page", "p");

        for (Groupe grp : groupes) {
          List<Student> students = studentRepository.findByGroupe(grp);

          for (Student student : students) {
            Pageable pageable = PageRequest.of(page, 6);
            Page<StudentPW> pwpage = spr.findByStudentIdContaining(student.getId(), pageable);

            List<StudentPW> pws = pwpage.getContent();
            model.addAttribute("professorPage", pwpage);
            model.addAttribute("pageInfo", pwpage.getPageable());
            allStudentPWs.addAll(pws);
          }
        }
        model.addAttribute("studentPWs", allStudentPWs);
        List<String> names = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          names.add(stpw.getPw().getTitle());

        }
        List<Integer> nbrs = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          nbrs.add(stpw.getNote());

        }
        model.addAttribute("names", names);
        model.addAttribute("nbrs", nbrs);

        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }

      }
      model.addAttribute("studentId", studentid);
      model.addAttribute("pwId", pwid);
      model.addAttribute("mode", "voir");
    }

    return "studentDetails";

  }

  @PostMapping("/validerremarque/{id1}/{id2}")
  public String ajouterStudentPW(Authentication authentication, Model model, @PathVariable("id1") Long studentId,
      @PathVariable("id2") Long pwId, @RequestParam("remarque") String remarque) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());

      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
        model.addAttribute("groupes", groupes);

        List<StudentPW> allStudentPWs = new ArrayList<>();
        List<String> pwencodedPhotos1 = new ArrayList<>();
        List<String> pwencodedPhotos2 = new ArrayList<>();
        List<String> pwencodedPhotos3 = new ArrayList<>();

        for (Groupe grp : groupes) {
          List<Student> students = studentRepository.findByGroupe(grp);

          for (Student student : students) {
            List<StudentPW> studentPWs = spr.findByStudentId(student.getId());
            allStudentPWs.addAll(studentPWs);
          }
        }
        List<String> names = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          names.add(stpw.getPw().getTitle());

        }
        List<Integer> nbrs = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          nbrs.add(stpw.getNote());

        }
        System.out.println(names);
        System.out.println(nbrs);
        model.addAttribute("names", names);
        model.addAttribute("nbrs", nbrs);
        model.addAttribute("studentPWs", allStudentPWs);
        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }

      }

      StudentPWPK studentPWPK = new StudentPWPK();
      studentPWPK.setStudent_id(studentId);
      studentPWPK.setPw_id(pwId);
      StudentPW studentpw = spr.getById(studentPWPK);
      studentpw.setRemarque(remarque);
      studentpw.setId(studentPWPK);
      spr.save(studentpw);
    }
    return "studentpw";

  }

  // ...........................................................................

  // ..........................................................................

  @PostMapping("/validertp/{id1}/{id2}")
  public String ajouterStudenttp(Authentication authentication, Model model, @PathVariable("id1") Long studentId,
      @PathVariable("id2") Long pwId, @RequestParam("remarque") String remarque,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      model.addAttribute("id", studentId);
      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);
        model.addAttribute("page", "p");

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);

        List<StudentPW> allStudentPWs = new ArrayList<>();
        List<String> pwencodedPhotos1 = new ArrayList<>();
        List<String> pwencodedPhotos2 = new ArrayList<>();
        List<String> pwencodedPhotos3 = new ArrayList<>();

        for (Groupe grp : groupes) {
          List<Student> students = studentRepository.findByGroupe(grp);

          for (Student student : students) {
            Pageable pageable = PageRequest.of(page, 6);
            Page<StudentPW> pwpage = spr.findByStudentIdContaining(student.getId(), pageable);

            List<StudentPW> pws = pwpage.getContent();
            model.addAttribute("professorPage", pwpage);
            model.addAttribute("pageInfo", pwpage.getPageable());
            allStudentPWs.addAll(pws);
          }
        }
        List<String> names = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          names.add(stpw.getPw().getTitle());

        }
        List<Integer> nbrs = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          nbrs.add(stpw.getNote());

        }
        System.out.println(names);
        System.out.println(nbrs);
        model.addAttribute("names", names);
        model.addAttribute("nbrs", nbrs);
        model.addAttribute("studentPWs", allStudentPWs);
        for (StudentPW spw : allStudentPWs) {
          byte[] photo1 = spw.getImage1();
          byte[] photo2 = spw.getImage2();
          byte[] photo3 = spw.getImage3();

          if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
              || (photo3 != null && photo3.length > 0)) {
            String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
            pwencodedPhotos1.add(encodedPhot1);
            model.addAttribute("photo1", pwencodedPhotos1);
            String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
            pwencodedPhotos2.add(encodedPhot2);
            model.addAttribute("photo2", pwencodedPhotos2);
            String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
            pwencodedPhotos3.add(encodedPhot3);
            model.addAttribute("photo3", pwencodedPhotos3);
          }
        }

      }

      StudentPWPK studentPWPK = new StudentPWPK();
      studentPWPK.setStudent_id(studentId);
      studentPWPK.setPw_id(pwId);
      StudentPW studentpw = spr.getById(studentPWPK);
      studentpw.setRemarque(remarque);
      studentpw.setId(studentPWPK);
      spr.save(studentpw);
    }
    return "studentDetails";

  }

  @GetMapping("/getetudiants")
  public String getbygroupe(Authentication authentication, Model model, @RequestParam("id") Long id,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());

      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
        model.addAttribute("groupes", groupes);

        Optional<Groupe> groupeoptinal = groupeRepository.findById(id);
        Groupe groupe = groupeoptinal.get();
        model.addAttribute("id", id);

        List<String> pwencodedPhotos1 = new ArrayList<>();
        List<String> pwencodedPhotos2 = new ArrayList<>();
        List<String> pwencodedPhotos3 = new ArrayList<>();
        List<Student> students = new ArrayList<>();

        students = studentRepository.findByGroupe(groupe);
        model.addAttribute("students", students);

        Pageable pageable = PageRequest.of(page, 1);
        Page<StudentPW> pwpage = spr.findByGroupe(groupe, pageable);
        List<StudentPW> pws = pwpage.getContent();
        if (pws.isEmpty()) {
          model.addAttribute("msg", "No Completed Assignments for this Group");

        } else {
          model.addAttribute("professorPage", pwpage);
          model.addAttribute("pageInfo", pwpage.getPageable());

          model.addAttribute("type", "b");

          model.addAttribute("studentPWs", pws);

          for (StudentPW spw : pws) {
            byte[] photo1 = spw.getImage1();
            byte[] photo2 = spw.getImage2();
            byte[] photo3 = spw.getImage3();

            if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
                || (photo3 != null && photo3.length > 0)) {
              String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
              pwencodedPhotos1.add(encodedPhot1);
              model.addAttribute("photo1", pwencodedPhotos1);
              String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
              pwencodedPhotos2.add(encodedPhot2);
              model.addAttribute("photo2", pwencodedPhotos2);
              String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
              pwencodedPhotos3.add(encodedPhot3);
              model.addAttribute("photo3", pwencodedPhotos3);
            }
          }
        }
      }
    }

    return "studentpw";

  }

  @GetMapping("/gettpetudiants")
  public String getbygroupeetudiant(Authentication authentication, Model model, @RequestParam("idd") Long idd,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());

      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);
        Optional<Student> student = studentRepository.findById(idd);
        Student std = student.get();

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
        model.addAttribute("groupes", groupes);

        List<StudentPW> allStudentPWs = new ArrayList<>();
        List<String> pwencodedPhotos1 = new ArrayList<>();
        List<String> pwencodedPhotos2 = new ArrayList<>();
        List<String> pwencodedPhotos3 = new ArrayList<>();
        List<Student> students = studentRepository.findByGroupeStudents(std);
        model.addAttribute("students", students);

        // Fetch professors with pagination
        Pageable pageable = PageRequest.of(page, 6);
        Page<StudentPW> pwpage = spr.findByStudent(std, pageable);
        List<StudentPW> pws = pwpage.getContent();
        if (pws.isEmpty()) {
          model.addAttribute("msg", "No Completed Assignments for this Student");
        } else {
          model.addAttribute("professorPage", pwpage);
          model.addAttribute("pageInfo", pwpage.getPageable());

          model.addAttribute("type", "c");
          model.addAttribute("idd", idd);

          model.addAttribute("studentPWs", pws);
          for (StudentPW spw : pws) {
            byte[] photo1 = spw.getImage1();
            byte[] photo2 = spw.getImage2();
            byte[] photo3 = spw.getImage3();

            if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
                || (photo3 != null && photo3.length > 0)) {
              String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
              pwencodedPhotos1.add(encodedPhot1);
              model.addAttribute("photo1", pwencodedPhotos1);
              String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
              pwencodedPhotos2.add(encodedPhot2);
              model.addAttribute("photo2", pwencodedPhotos2);
              String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
              pwencodedPhotos3.add(encodedPhot3);
              model.addAttribute("photo3", pwencodedPhotos3);
            }
          }
        }
      }
    }

    return "studentpw";

  }

  @GetMapping("/gettpstudent")
  public String gettpstudent(Authentication authentication, Model model,
      @RequestParam(value = "id", required = false) Long id) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      model.addAttribute("id", id);
      
      
      if (prof.isPresent()) {
        Professor prof1 = prof.get();
        byte[] photo = prof1.getPhoto();
        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo", encodedPhoto);
        }
        model.addAttribute("professor", prof1);

        List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
        model.addAttribute("groupes", groupes);

        List<StudentPW> allStudentPWs = new ArrayList<>();
        List<String> pwencodedPhotos1 = new ArrayList<>();
        List<String> pwencodedPhotos2 = new ArrayList<>();
        List<String> pwencodedPhotos3 = new ArrayList<>();

        List<StudentPW> studentPWs = spr.findByStudentId(id);
        allStudentPWs.addAll(studentPWs);
        List<String> names = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          names.add(stpw.getPw().getTitle());

        }
        List<Integer> nbrs = new ArrayList<>();
        for (StudentPW stpw : allStudentPWs) {
          nbrs.add(stpw.getNote());

        }
        System.out.println(names);
        System.out.println(nbrs);
        if (allStudentPWs.isEmpty()) {
          model.addAttribute("msg", "No Completed Assignments for this Student");

        } else {

          model.addAttribute("names", names);
          model.addAttribute("nbrs", nbrs);
          model.addAttribute("page", "p");

          model.addAttribute("studentPWs", allStudentPWs);
          // model.addAttribute("students", students);
          for (StudentPW spw : allStudentPWs) {
            byte[] photo1 = spw.getImage1();
            byte[] photo2 = spw.getImage2();
            byte[] photo3 = spw.getImage3();

            if ((photo1 != null && photo1.length > 0) || (photo2 != null && photo2.length > 0)
                || (photo3 != null && photo3.length > 0)) {
              String encodedPhot1 = Base64.getEncoder().encodeToString(photo1);
              pwencodedPhotos1.add(encodedPhot1);
              model.addAttribute("photo1", pwencodedPhotos1);
              String encodedPhot2 = Base64.getEncoder().encodeToString(photo2);
              pwencodedPhotos2.add(encodedPhot2);
              model.addAttribute("photo2", pwencodedPhotos2);
              String encodedPhot3 = Base64.getEncoder().encodeToString(photo3);
              pwencodedPhotos3.add(encodedPhot3);
              model.addAttribute("photo3", pwencodedPhotos3);
            }
          }
        }
      }
    }

    return "studentDetails";

  }

  // ...............................................gestion du tp avec les
  // groupes...............................................

  @GetMapping("/tpgroupe")
  public String tpgroupe(Model model, Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }

      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      List<PW> pws = new ArrayList<>();
      for (Groupe g : groupes) {
        List<PW> pwws = PWRepository.findByGroupes(g);
        pws.addAll(pwws);

      }
      model.addAttribute("pws", pws);
    }

    return "tpgroupe";

  }

  @GetMapping("/showaffectation")
  public String showaffectation(Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      Pageable pageable = PageRequest.of(page, 300); // 10 éléments par page, ajustez selon vos besoins

      // Fetch professors with pagination
      Page<PW> pwpage = PWRepository.findAll(pageable);

      List<PW> pws = pwpage.getContent();
      model.addAttribute("pws", pws);
      model.addAttribute("professorPage", pwpage);
      model.addAttribute("pageInfo", pwpage.getPageable());
      model.addAttribute("groupes", groupes);
      model.addAttribute("mode", "affect");
    }

    return "PW";
  }

  @PostMapping("/affecter")
  public String affecter(@RequestParam("groupe") Long groupe, @RequestParam("pw") Long pw,
      Authentication authentication, Model model, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);

      Optional<Groupe> groupee = groupeRepository.findById(groupe);
      Groupe groupeee = groupee.get();
      Optional<PW> pwww = PWRepository.findById(pw);
      PW pww = pwww.get();
      if (!pww.getGroupes().contains(groupeee)) {
        pww.getGroupes().add(groupeee);
        groupeee.getPws().add(pww);
        PWRepository.save(pww);
        model.addAttribute("success", "PW has been successfully assigned");
      } else {
        model.addAttribute("error", "This assignment is already allocated to this group");
      }

      List<Groupe> groupes = groupeRepository.findByProfessor(prof1);
      List<PW> pwss = PWRepository.findAll();
      Pageable pageable = PageRequest.of(page, 6); // 10 éléments par page, ajustez selon vos besoins

      // Fetch professors with pagination
      Page<PW> pwpage = PWRepository.findAll(pageable);

      List<PW> pws = pwpage.getContent();
      model.addAttribute("pws", pws);
      model.addAttribute("professorPage", pwpage);
      model.addAttribute("pageInfo", pwpage.getPageable());

      model.addAttribute("groupes", groupes);

    }
    return "PW";
  }

  // ................gestion prepa.........................

  @GetMapping("/prepa")
  public String prepa(Preparation prepa, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      Pageable pageable = PageRequest.of(page, 16);
      Page<Preparation> professorPage = prepaRepository.findAll(pageable);
      List<Preparation> prepas = professorPage.getContent();
      model.addAttribute("professor", prof1);
      if (!prepas.isEmpty()){
        model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      model.addAttribute("prepas", prepas);
        

      }else {
        model.addAttribute("msg", "No preparation for the moment");
      }

      
      
      

    }
    return "Preparation";
  }

  @GetMapping("/addpre")
  public String addprepa(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();

      Pageable pageable = PageRequest.of(page, 16);
      Page<Preparation> professorPage = prepaRepository.findAll(pageable);
      List<Preparation> prepas = professorPage.getContent();

      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());

      model.addAttribute("prepas", prepas);
      model.addAttribute("professor", prof1);

      model.addAttribute("mode", "add");
      model.addAttribute("prepa", new Preparation());

    }
    return "Preparation";

  }

  @PostMapping("/addprepa")
  public String saveprepa(Preparation prepa, Model model, Authentication authentication) {
    prepaRepository.save(prepa);
    return "redirect:/prof/prepa";
  }

  @GetMapping("/editprepa/{id}")
  public String editprepa(@PathVariable("id") long id, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      model.addAttribute("professor", prof1);
    }

    Preparation prepa = prepaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Prepa Id:" + id));

    Pageable pageable = PageRequest.of(page, 16);
    Page<Preparation> professorPage = prepaRepository.findAll(pageable);
    List<Preparation> prepas = professorPage.getContent();

    model.addAttribute("professorPage", professorPage);
    model.addAttribute("pageInfo", professorPage.getPageable());

    model.addAttribute("prepas", prepas);

    model.addAttribute("prepa", prepa);
    model.addAttribute("mode", "update");

    return "Preparation";
  }

  @PostMapping("/updateprepa/{id}")
  public String updateprepa(@PathVariable("id") long id, Preparation prepa, Model model,
      Authentication authentication) {
    Preparation prepaa = prepaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid tooth Id:" + id));

    if (prepaa != null) {
      prepaa.setType(prepa.getType());
    }
    prepaRepository.save(prepaa);

    return "redirect:/prof/prepa";
  }

  @GetMapping("/deleteprepa/{id}")
  public String deleteprepa(@PathVariable("id") long id, Model model, Authentication authentication,
      HttpServletRequest request) {
    try {
      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Optional<Professor> prof = professorRepository.findById(userDetails.getId());
        Professor prof1 = prof.get();
        model.addAttribute("professor", prof1);
      }

      Preparation prepa = prepaRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid Tooth Id:" + id));
      prepaRepository.delete(prepa);

      String referer = request.getHeader("Referer");
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(referer);
      String pageParam = request.getParameter("page");
      if (pageParam != null) {
        builder.replaceQueryParam("page", pageParam);
      }

      // Redirection vers la page précédente avec le même numéro de page
      return "redirect:" + builder.toUriString();
    } catch (Exception e) {
      e.printStackTrace();
      return "error500";
    }
  }

  @GetMapping("/prepa/searchByNom")
  public String searchByNomprepa(@RequestParam(name = "a") String nom, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Professor> prof = professorRepository.findById(userDetails.getId());
      Professor prof1 = prof.get();
      byte[] photo = prof1.getPhoto();
      if (photo != null) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        model.addAttribute("photo", encodedPhoto);
      }
      model.addAttribute("professor", prof1);
    }
    if (nom.isEmpty()) {
      Pageable pageable = PageRequest.of(page, 16);
      Page<Preparation> professorPage = prepaRepository.findAll(pageable);
      List<Preparation> prepas = professorPage.getContent();

      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());

      model.addAttribute("prepas", prepas);

    } else {
      Pageable pageable = PageRequest.of(page, 16);
      Page<Preparation> professorPage = prepaRepository.findByTypeContaining(nom, pageable);
      List<Preparation> prepas = professorPage.getContent();

      if (prepas.isEmpty()) {
        model.addAttribute("msg", "No prepation  with this type");
      } else {
        model.addAttribute("professorPage", professorPage);
        model.addAttribute("pageInfo", professorPage.getPageable());

        model.addAttribute("prepas", prepas);

      }
    }
    return "Preparation";
  }

}

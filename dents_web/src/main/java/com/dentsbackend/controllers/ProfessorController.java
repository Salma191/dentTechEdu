package com.dentsbackend.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.dentsbackend.entities.Admin;
import com.dentsbackend.entities.Professor;
import com.dentsbackend.entities.Role;
import com.dentsbackend.repositories.AdminRepository;
import com.dentsbackend.repositories.ProfessorRepository;
import com.dentsbackend.repositories.RoleRepository;
import com.dentsbackend.services.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/admin")
public class ProfessorController {

  @Autowired
  ProfessorRepository professorRepository;

  @Autowired
  RoleRepository roleRepository;
  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private AdminRepository adminRepository;

  // page admin

  @GetMapping
  public String admin(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Admin> adminOptional = adminRepository.findById(userDetails.getId());

      if (adminOptional.isPresent()) {
        Admin admin = adminOptional.get();
        byte[] photo = admin.getPhoto();

        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo1", encodedPhoto);
        }
      }

      model.addAttribute("userdetail", userDetails);
    }

    // Configure pagination
    // Configure pagination
    Pageable pageable = PageRequest.of(page, 8); // 10 éléments par page, ajustez selon vos besoins

    // Fetch professors with pagination
    Page<Professor> professorPage = professorRepository.findAll(pageable);

    List<Professor> professors = professorPage.getContent();

    List<String> encodedPhotos = new ArrayList<>();
    for (Professor professor : professors) {
      byte[] photo = professor.getPhoto();
      if (photo != null && photo.length > 0) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        encodedPhotos.add(encodedPhoto);
      } else {
        encodedPhotos.add("");
      }
    }
    if(professors.isEmpty()){
      model.addAttribute("msg", "No professors for the moment");
    }else{

    

    // Add pagination information to model
    model.addAttribute("professors", professors);
    model.addAttribute("encodedPhotos", encodedPhotos);

    model.addAttribute("professorPage", professorPage);
    model.addAttribute("pageInfo", professorPage.getPageable());
    }

    return "admin";
  }

  @GetMapping("/searchByNom")
  public String searchByNom(@RequestParam(name = "a") String nom, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Optional<Admin> adminOptional = adminRepository.findById(userDetails.getId());

      if (adminOptional.isPresent()) {
        Admin admin = adminOptional.get();
        byte[] photo = admin.getPhoto();

        if (photo != null) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          model.addAttribute("photo1", encodedPhoto);
        }
      }
      model.addAttribute("userdetail", userDetails);
    }

    List<String> encodedPhotos = new ArrayList<>();
    if (nom.isEmpty()) {
      Pageable pageable = PageRequest.of(page, 8);
      Page<Professor> professorPage = professorRepository.findAll(pageable);
      List<Professor> professors = professorPage.getContent();
      model.addAttribute("professors", professors);
      model.addAttribute("professorPage", professorPage);
      model.addAttribute("pageInfo", professorPage.getPageable());
      for (Professor professor : professors) {
        byte[] photo = professor.getPhoto();
        if (photo != null && photo.length > 0) {
          String encodedPhoto = Base64.getEncoder().encodeToString(photo);
          encodedPhotos.add(encodedPhoto);
        } else {
          encodedPhotos.add("");
        }
      }
      model.addAttribute("encodedPhotos", encodedPhotos);
    } else {
      Pageable pageable = PageRequest.of(page, 8); // 10 éléments par page, ajustez selon vos besoins
      Page<Professor> professorPage = (professorRepository.findByLastNameContainingIgnoreCase(nom, pageable));
      if(professorPage.isEmpty()){
        professorPage = (professorRepository.findByFirstNameContainingIgnoreCase(nom, pageable));
      }
      List<Professor> professors = professorPage.getContent();
      if (professors.isEmpty()) {
        model.addAttribute("msg", "No teacher with this last or name");
      } else {
        model.addAttribute("professors", professors);
        model.addAttribute("professorPage", professorPage);
        model.addAttribute("pageInfo", professorPage.getPageable());
        for (Professor professor : professors) {
          byte[] photo = professor.getPhoto();
          if (photo != null && photo.length > 0) {
            String encodedPhoto = Base64.getEncoder().encodeToString(photo);
            encodedPhotos.add(encodedPhoto);
          } else {
            encodedPhotos.add("");
          }
        }
        model.addAttribute("encodedPhotos", encodedPhotos);
      }
    }
    return "admin";
  }

  // show popup pour ajouter professeur

  @GetMapping("/add")
public String showAddForm(Model model, Authentication authentication, @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("userdetail", userDetails);
    }

    // Fetch professors for the current page only
    Pageable pageable = PageRequest.of(page, 8); // 8 éléments par page, ajustez selon vos besoins
    Page<Professor> professorPage = professorRepository.findAll(pageable);

    List<Professor> professors = professorPage.getContent();
    List<String> encodedPhotos = new ArrayList<>();
    for (Professor professor : professors) {
        byte[] photo = professor.getPhoto();
        if (photo != null && photo.length > 0) {
            String encodedPhoto = Base64.getEncoder().encodeToString(photo);
            encodedPhotos.add(encodedPhoto);
        } else {
            encodedPhotos.add("");
        }
    }
    model.addAttribute("professors", professors);
    model.addAttribute("encodedPhotos", encodedPhotos);

    model.addAttribute("professorPage", professorPage);
    model.addAttribute("pageInfo", professorPage.getPageable());

    model.addAttribute("mode", "add");
    model.addAttribute("professor", new Professor());
    return "admin";
}


  // ajouter professeur

  @PostMapping("/addProf")
  public String save(Professor professor, Model model, @RequestParam("file") MultipartFile photoFile) {
    Optional<Role> role = roleRepository.findByName("ROLE_PROFESSOR");
    Role roole = role.get();
    professor.setRoles(Set.of(roole));

    String a = passwordEncoder.encode(professor.getPassword());
    professor.setPassword(a);
    if (photoFile != null && !photoFile.isEmpty()) {
      try {
        byte[] photoBytes = photoFile.getBytes();
        professor.setPhoto(photoBytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    professorRepository.save(professor);

    return "redirect:/admin";
  }

  // show popup pour modifier professeur

  @GetMapping("/edit/{id}")
  public String editProfesssor(@PathVariable("id") long id, Model model, Authentication authentication,
      @RequestParam(defaultValue = "0") int page) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      model.addAttribute("userdetail", userDetails);
    }
    Professor professor = professorRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    if (professor.getPhoto() != null) {
      String encodedPhoto = Base64.getEncoder().encodeToString(professor.getPhoto());
      model.addAttribute("encodedPhoto", encodedPhoto);

    }
    Pageable pageable = PageRequest.of(page, 8); // 10 éléments par page, ajustez selon vos besoins

    // Fetch professors with pagination
    Page<Professor> professorPage = professorRepository.findAll(pageable);

    List<Professor> professors = professorPage.getContent();
    List<String> encodedPhotos = new ArrayList<>();
    for (Professor professor1 : professors) {
      byte[] photo = professor1.getPhoto();
      if (photo != null && photo.length > 0) {
        String encodedPhoto = Base64.getEncoder().encodeToString(photo);
        encodedPhotos.add(encodedPhoto);
      } else {
        encodedPhotos.add("");
      }
    }
    model.addAttribute("professors", professors);
    model.addAttribute("encodedPhotos", encodedPhotos);

    model.addAttribute("professorPage", professorPage);
    model.addAttribute("pageInfo", professorPage.getPageable());
    model.addAttribute("professor", professor);
    model.addAttribute("mode", "update");

    return "admin";
  }











  // @GetMapping("/edit/admine")
  // public String editAdmin(@RequestParam("username") String username, Model model, Authentication authentication,
  //     @RequestParam(defaultValue = "0") int page) {
  //   if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
  //     UserDetails userDetails = (UserDetails) authentication.getPrincipal();
  //     model.addAttribute("userdetail", userDetails);
  //   }
  //   Admin admin = AdminRepository.findByUserName(id)
  //       .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
  //   if (professor.getPhoto() != null) {
  //     String encodedPhoto = Base64.getEncoder().encodeToString(professor.getPhoto());
  //     model.addAttribute("encodedPhoto", encodedPhoto);

  //   }
  //   Pageable pageable = PageRequest.of(page, 8); // 10 éléments par page, ajustez selon vos besoins

  //   // Fetch professors with pagination
  //   Page<Professor> professorPage = professorRepository.findAll(pageable);

  //   List<Professor> professors = professorPage.getContent();
  //   List<String> encodedPhotos = new ArrayList<>();
  //   for (Professor professor1 : professors) {
  //     byte[] photo = professor1.getPhoto();
  //     if (photo != null && photo.length > 0) {
  //       String encodedPhoto = Base64.getEncoder().encodeToString(photo);
  //       encodedPhotos.add(encodedPhoto);
  //     } else {
  //       encodedPhotos.add("");
  //     }
  //   }
  //   model.addAttribute("professors", professors);
  //   model.addAttribute("encodedPhotos", encodedPhotos);

  //   model.addAttribute("professorPage", professorPage);
  //   model.addAttribute("pageInfo", professorPage.getPageable());
  //   model.addAttribute("professor", professor);
  //   model.addAttribute("mode", "update");

  //   return "admin";
  // }


  // modifier professeur

  @PostMapping("/update/{id}")
public String updateProfessor(@PathVariable("id") long id, Professor professor, Model model,
        @RequestParam(value = "file", required = false) MultipartFile photoFile) {
    Optional<Role> role = roleRepository.findByName("ROLE_PROFESSOR");
    Role roole = role.get();
    professor.setRoles(Set.of(roole));
    professor.setId(id);
    Optional<Professor> prf = professorRepository.findById(professor.getId());
    professor.setPassword(prf.get().getPassword());

    // Vérifier si un nouveau fichier d'image est fourni
    if (photoFile != null && !photoFile.isEmpty()) {
        try {
            byte[] photoBytes = photoFile.getBytes();
            professor.setPhoto(photoBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        // Aucun fichier n'a été téléchargé, conservez l'image existante du professeur
        professor.setPhoto(prf.get().getPhoto());
    }

    professorRepository.save(professor);

    return "redirect:/admin";
}


  // supprimer professeur

  @GetMapping("/delete/{id}")
  public String deleteProfessor(@PathVariable("id") long id, Model model, Authentication authentication,HttpServletRequest request) {
    try {
      if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        model.addAttribute("userdetail", userDetails);
      }

      Professor professor = professorRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
      professorRepository.delete(professor);

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

}
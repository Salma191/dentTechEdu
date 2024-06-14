package com.dentsbackend.controllers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dentsbackend.entities.EmailRequest;
import com.dentsbackend.entities.Groupe;
import com.dentsbackend.entities.PW;
import com.dentsbackend.entities.Preparation;
import com.dentsbackend.entities.Professor;
import com.dentsbackend.entities.Student;
import com.dentsbackend.entities.StudentPW;
import com.dentsbackend.entities.StudentPWPK;
import com.dentsbackend.entities.User;
import com.dentsbackend.repositories.GroupeRepository;
import com.dentsbackend.repositories.PWRepository;
import com.dentsbackend.repositories.PrepaRepository;
import com.dentsbackend.repositories.StudentPWRepository;
import com.dentsbackend.repositories.StudentRepository;
import com.dentsbackend.repositories.UserRepository;
import com.dentsbackend.services.CustomUserDetails;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RestController
@RequestMapping("/etudiant")
public class EtudiantController {

    @Autowired
    PWRepository PWRepository;

    @Autowired
    GroupeRepository groupeRepository;

    @Autowired
    StudentRepository sr;

    @Autowired
    UserRepository ur;

    @Autowired
    GroupeRepository gr;

    @Autowired
    StudentPWRepository spr;

    @Autowired
    PWRepository pwr;


    @Autowired
    PrepaRepository pr;

    @Autowired
    private JavaMailSender emailSender;

    @GetMapping("/allpw")
    public List<String> getPW() {
        List<Preparation> pws = pr.findAll();
        List<String> types = new ArrayList<>();
       for (Preparation p : pws){
             types.add(p.getType());
       }
       return types;
    }




    @PostMapping("/getstudent")
    public ResponseEntity<Object> getstudent(@RequestBody Student student) {
        Student st = sr.findByUserName(student.getUserName());
        return  ResponseEntity.ok(st);

    }


    

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            Student s = sr.findByEmail(request.getTo());
            if (s == null) {
                return new ResponseEntity<>("email non valide", HttpStatus.UNAUTHORIZED);
            }
            SimpleMailMessage message = new SimpleMailMessage();
            System.out.println(request.getTo());
            System.out.println(request.getSubject());
            System.out.println(request.getText());
            message.setTo(request.getTo());
            message.setSubject(request.getSubject());
            message.setText(request.getText());
            emailSender.send(message);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Paramètres invalides", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/checkpw")
    public ResponseEntity<Object> checktp(@RequestParam("params") String params) {
        boolean exists = false;
        List<PW> PWs = new ArrayList<>();
        String[] paramsArray = params.split(",");
        if (paramsArray.length != 2) {
            return new ResponseEntity<>("Paramètres invalides", HttpStatus.BAD_REQUEST);
        }

        String typeprep = paramsArray[0];
        String code = paramsArray[1];
        int groupId = Integer.parseInt(code);
        List<PW> pws = pwr.findByGroupesId(groupId);
        for (PW pw : pws) {

            if (pw.getPreparation().getType().equals(typeprep)) {
                System.out.println(pw.getTitle());
                exists = true;
                PWs.add(pw);
            }
        }
        if (exists) {
            return ResponseEntity.ok(PWs);
        } else {
            return new ResponseEntity<>("pas de tp correspondant", HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/courbe")
    public ResponseEntity<Object> getCourbe(@RequestParam("id") long id) {
        List<StudentPW> studentpws = spr.findByStudentId(id);
        List<String> names = new ArrayList<>();
        List<Integer> nbrs = new ArrayList<>();
        for (StudentPW stpw : studentpws) {
            names.add(stpw.getPw().getTitle());
            nbrs.add(stpw.getNote());
        }
        Map<String, Integer> data = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            data.put(names.get(i), nbrs.get(i));
        }

        // Returning the data
        return ResponseEntity.ok(data);
    }

    // login etudiant
    @PostMapping("/register")
    public ResponseEntity<Object> ajouterService(@RequestBody Student student) {
        Student st = sr.findByUserName(student.getUserName());
        if (st == null || !student.getPassword().equals(st.getPassword())) {
            return new ResponseEntity<>("User Name ou Password Incorrect", HttpStatus.FORBIDDEN);
        } else {
            return ResponseEntity.ok(st);
        }
    }

    // verifier existence de l etudiant

    @PostMapping("/check")
    public ResponseEntity<String> check(@RequestParam("email") String email) {
        try {
            List<User> users = ur.findAll();

            for (User user : users) {
                if (user.getEmail().equals(email)) {

                    return ResponseEntity.ok("Email trouvé dans la base de données");
                }
            }
            return ResponseEntity.ok("Email non trouvé dans la base de données");

        } catch (Exception e) {

            return new ResponseEntity<>("Erreur lors de la recherche dans la base de données",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // all students

    @GetMapping("/all")
    public List<PW> getPWByGroupe(@RequestParam("code") String code) {
        return pwr.findByGroupesCode(code);
    }

    // changer mdp

    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody Student student) {

        if (student.getPassword() != null) {
            Student st = sr.findByEmail(student.getEmail());
            st.setPassword(student.getPassword());
            return ResponseEntity.ok(sr.save(st));

        } else {
            return new ResponseEntity<>("Requête invalide : le mot de passe est null", HttpStatus.FORBIDDEN);
        }

    }

    // changer profile

    @PostMapping("/changeProfil")
    public ResponseEntity<Object> changeProfil(@RequestBody Student student, @RequestParam("email") String email) {
        if (student.getEmail() != null && student.getFirstName() != null && student.getLastName() != null
                && student.getUserName() != null) {
            Student st = sr.findByEmail(email);

            if (st != null) {
                st.setEmail(student.getEmail());
                st.setFirstName(student.getFirstName());
                st.setLastName(student.getLastName());
                st.setUserName(student.getUserName());
                st.setNumber(student.getNumber());
                if (student.getPhoto() != null) {
                    st.setPhoto(student.getPhoto());
                }
                return ResponseEntity.ok(sr.save(st));
            } else {
                return new ResponseEntity<>("Aucun étudiant trouvé avec cet e-mail", HttpStatus.FORBIDDEN);
            }
        } else {

            return new ResponseEntity<>("Requête invalide : Les champs ne peuvent pas être vides",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // save profile

    @PostMapping("/saveStudentPW")
    public ResponseEntity<StudentPW> ajouterStudentPW(@RequestBody StudentPW studentPW) {
        try {
            if (studentPW != null && studentPW.getStudent() != null && studentPW.getPw() != null) {

                StudentPWPK studentPWPK = new StudentPWPK();
                studentPWPK.setStudent_id(studentPW.getStudent().getId());
                studentPWPK.setPw_id(studentPW.getPw().getId());
                studentPW.setId(studentPWPK);

                StudentPW savedStudentPW = spr.save(studentPW);
                System.out.print(savedStudentPW);
                return ResponseEntity.ok(savedStudentPW);

            } else {
                System.out.print("System.out.print(savedStudentPW)");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.print(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // tp d un etudiant

    @GetMapping("/studentpw")
    public List<StudentPW> getPw(@RequestParam("id") Long id) {

        List<StudentPW> studentpws = spr.findByStudentId(id);
        return studentpws;

    }

}

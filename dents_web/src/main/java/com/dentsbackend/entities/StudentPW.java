package com.dentsbackend.entities;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPW {
    @EmbeddedId
    private StudentPWPK id;
    @Column(name = "image1", columnDefinition = "LONGBLOB")
    private byte[] image1;
    @Column(name = "image2", columnDefinition = "LONGBLOB")
    private byte[] image2;
    @Column(name = "image3", columnDefinition = "LONGBLOB")
    private byte[] image3;
    private String internes;
    private String externes;
    private String depouilles;
    private String convergence;
    private String date;
    private String time;
    private int note;
    private String remarque;
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Student student;
    @ManyToOne
    @JoinColumn(name = "pw_id", referencedColumnName = "id", insertable = false, updatable = false)
    private PW pw;
}

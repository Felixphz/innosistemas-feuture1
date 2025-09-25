package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "courses", schema="public")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Courses_id_gen")
    @SequenceGenerator(name = "Courses_id_gen", sequenceName = "Courses_id_course_seq", allocationSize = 1)
    @Column(name = "id_course", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_course", nullable = false)
    private String nameCourse;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameCourse() {
        return nameCourse;
    }

    public void setNameCourse(String nameCourse) {
        this.nameCourse = nameCourse;
    }

}
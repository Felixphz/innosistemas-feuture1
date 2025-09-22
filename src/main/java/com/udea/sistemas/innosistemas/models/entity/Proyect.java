package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "\"Proyects\"")
public class Proyect {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Proyects_id_gen")
    @SequenceGenerator(name = "Proyects_id_gen", sequenceName = "Proyects_id_proyect_seq", allocationSize = 1)
    @Column(name = "id_proyect", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Size(max = 255)
    @NotNull
    @Column(name = "name_project", nullable = false)
    private String nameProject;

    @Size(max = 255)
    @NotNull
    @Column(name = "descriptions", nullable = false)
    private String descriptions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

}
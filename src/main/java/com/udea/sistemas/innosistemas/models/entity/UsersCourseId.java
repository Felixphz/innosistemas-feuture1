package com.udea.sistemas.innosistemas.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersCourseId implements Serializable {
    private static final long serialVersionUID = -3628777389934727406L;
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;


    @NotNull
    @Column(name = "course_id", nullable = false)
    private Integer courseId;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersCourseId entity = (UsersCourseId) o;
        return Objects.equals(this.email, entity.email) &&
                Objects.equals(this.courseId, entity.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, courseId);
    }

}
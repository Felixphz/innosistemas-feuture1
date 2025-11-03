package com.udea.sistemas.innosistemas.exceptions;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(int id) {
        super("No se encontró el curso con ID: " + id);
    }
}

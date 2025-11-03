package com.udea.sistemas.innosistemas.exceptions;

public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String name) {
        super("Ya existe un curso con el nombre: " + name);
    }
}

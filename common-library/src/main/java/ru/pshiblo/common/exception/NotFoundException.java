package ru.pshiblo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Maxim Pshiblo
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends ApelsinException {

    public NotFoundException(String id, Class<?> clazz) {
        super(clazz.getName() + " not found with id " + id);
    }

    public NotFoundException(String id, String clazz) {
        super(clazz + " not found with id " + id);
    }

    public NotFoundException(long id, String clazz) {
        this(Integer.toString((int) id), clazz);
    }

    public NotFoundException(int id, String clazz) {
        this(Integer.toString(id), clazz);
    }

    public NotFoundException(int id, Class<?> clazz) {
        this(Integer.toString(id), clazz);
    }

    public NotFoundException(long id, Class<?> clazz) {
        this(Integer.toString((int) id), clazz);
    }

    public NotFoundException() {
        super();
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}

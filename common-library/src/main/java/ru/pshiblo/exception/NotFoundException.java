package ru.pshiblo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Maxim Pshiblo
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String id, Class<?> clazz) {
        super(clazz.getName() + " not found with id " + id);
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

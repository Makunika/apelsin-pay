package ru.pshiblo.mappings;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface ResponseMapper<RS, E> {
    RS toDTO(E request);
    List<RS> toDTOs(List<E> request);
}

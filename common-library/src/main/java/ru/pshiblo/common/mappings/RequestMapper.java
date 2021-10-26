package ru.pshiblo.common.mappings;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface RequestMapper<RQ, E> {
    E toEntity(RQ request);
    List<E> toEntities(List<RQ> request);
}

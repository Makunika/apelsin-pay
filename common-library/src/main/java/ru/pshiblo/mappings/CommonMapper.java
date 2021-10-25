package ru.pshiblo.mappings;

/**
 * @author Maxim Pshiblo
 */
public interface CommonMapper<RQ, RS, E> extends RequestMapper<RQ, E>, ResponseMapper<RS, E> {
}

--liquibase formatted sql
--changeset maxim:1
CREATE TABLE users
(
    id              serial              PRIMARY KEY,
    first_name      varchar(100)        NOT NULL,
    last_name       varchar(100)        NOT NULL,
    middle_name     varchar(100),
    email           varchar(100)        NOT NULL,
    phone           varchar(100)        NOT NULL,
    birthday        date                NOT NULL,
    passport_series int                 NOT NULL,
    passport_number int                 NOT NULL
)
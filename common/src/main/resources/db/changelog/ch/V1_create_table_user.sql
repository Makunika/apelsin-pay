--liquibase formatted sql
--changeset maxim:1
CREATE TABLE users
(
    id              serial              PRIMARY KEY,
    first_name      varchar(100)        NOT NULL,
    last_name       varchar(100)        NOT NULL,
    middle_name     varchar(100),
    password_hash   varchar(255)        NOT NULL,
    email           varchar(100)        NOT NULL,
    birthday        date                NOT NULL,
    passport_series int                 NOT NULL,
    passport_number int                 NOT NULL,
    img             text                NOT NULL DEFAULT 'default.png',
    created         timestamp           NOT NULL DEFAULT NOW(),
    updated         timestamp           NOT NULL DEFAULT NOW()
)
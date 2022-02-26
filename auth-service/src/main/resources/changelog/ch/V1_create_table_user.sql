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
);

INSERT INTO public.users (id, first_name, last_name, middle_name, email, phone, birthday, passport_series, passport_number) VALUES (1, 'Maxim', 'Pshiblo', 'Yu', 'pshblo.max@gmail.com', '+79529656046', '2005-08-09', 3443, 124007);

--liquibase formatted sql
--changeset maxim:2
CREATE TABLE roles
(
    id              serial              PRIMARY KEY,
    name            varchar(100)        NOT NULL UNIQUE
);


CREATE TABLE user_roles (
    id          serial      PRIMARY KEY,
    user_id     int         NOT NULL,
    role_id     int         NOT NULL,

    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_roles FOREIGN KEY (role_id) REFERENCES roles (id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

INSERT INTO roles(name)
VALUES('ROLE_ADMIN');

INSERT INTO roles(name)
VALUES('ROLE_APP');

INSERT INTO roles(name)
VALUES('ROLE_USER');

INSERT INTO public.user_roles (id, user_id, role_id) VALUES (1, 1, 3);


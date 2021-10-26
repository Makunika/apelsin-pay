--liquibase formatted sql
--changeset maxim:3
CREATE TABLE passwords
(
    id              varchar(36)         PRIMARY KEY,
    password_hash   varchar(200)        NOT NULL,
    login           varchar(100)        NOT NULL UNIQUE,


    user_id         int                 NOT NULL UNIQUE,

    CONSTRAINT fk_users_videos FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)


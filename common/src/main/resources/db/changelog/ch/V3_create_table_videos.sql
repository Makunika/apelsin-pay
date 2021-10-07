--liquibase formatted sql
--changeset maxim:3
CREATE TABLE videos
(
    id              varchar(36)         PRIMARY KEY,
    name            varchar(100)        NOT NULL,
    about           text,
    video           varchar(255)        NOT NULL DEFAULT 'default.mp4',
    is_private      bool                NOT NULL DEFAULT false,
    views           numeric             NOT NULL DEFAULT 0,


    user_id         int                 NOT NULL,
    created         timestamp           NOT NULL DEFAULT NOW(),
    updated         timestamp           NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_users_videos FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)


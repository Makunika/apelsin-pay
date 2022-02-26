--liquibase formatted sql
--changeset maxim:3
CREATE TABLE passwords
(
    id              SERIAL              PRIMARY KEY,
    password_hash   varchar(200)        NOT NULL,
    login           varchar(100)        NOT NULL UNIQUE,

    user_id         int                 NOT NULL UNIQUE,

    CONSTRAINT fk_users_videos FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

INSERT INTO public.passwords (id, password_hash, login, user_id) VALUES (1, '4VNwbVujdTjVNgj8cfzbZIBOXH+N9gEQBoHIDItRlCSv2WNzMkZ7rPCnu+2q53YGHH7nfFnyNfDtPQOrc0yx+A==', 'mpshiblo', 1);





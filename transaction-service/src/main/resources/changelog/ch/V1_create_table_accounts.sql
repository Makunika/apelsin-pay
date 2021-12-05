--liquibase formatted sql
--changeset maxim:1
CREATE TABLE accounts
(
    id          SERIAL      PRIMARY KEY,
    user_id     INTEGER     NOT NULL,
    number      varchar(20) NOT NULL UNIQUE,
    type        VARCHAR(50) NOT NULL,
    balance     DECIMAL     NOT NULL DEFAULT 0,
    lock        BOOLEAN     NOT NULL DEFAULT FALSE,
    currency    VARCHAR(3)  NOT NULL
);

GRANT SELECT ON accounts TO "maxbank-personal";
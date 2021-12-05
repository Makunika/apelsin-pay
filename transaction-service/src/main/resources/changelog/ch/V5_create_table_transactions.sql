--liquibase formatted sql
--changeset maxim:5

CREATE TABLE transactions (
    id              SERIAL PRIMARY KEY ,
    status          VARCHAR(30) NOT NULL ,
    to_number       VARCHAR(100) NOT NULL,
    from_number     VARCHAR(100) NOT NULL,
    is_inner        BOOLEAN NOT NULL DEFAULT true,
    is_to_card      BOOLEAN NOT NULL DEFAULT true,
    commission      INTEGER NOT NULL DEFAULT 0,
    owner_user_id   INTEGER NOT NULL,
    to_user_id      INTEGER,
    currency        VARCHAR(3) NOT NULL,
    money           BIGINT NOT NULL
)
--liquibase formatted sql
--changeset maxim:2
CREATE TABLE cards
(
    id              serial              PRIMARY KEY,
    user_id         INTEGER             NOT NULL,
    number          VARCHAR(16)         NOT NULL UNIQUE,
    type            VARCHAR(50)         NOT NULL,
    type_pay        varchar(50)         NOT NULL,
    account_id      INTEGER             NOT NULL,
    pin             VARCHAR(4)            NOT NULL,
    cvc             INT                 NOT NULL,
    expired         DATE                NOT NULL,
    lock            BOOLEAN             NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
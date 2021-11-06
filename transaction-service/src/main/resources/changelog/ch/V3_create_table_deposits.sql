--liquibase formatted sql
--changeset maxim:3
CREATE TABLE deposits
(
    id              serial              PRIMARY KEY,
    user_id         INTEGER             NOT NULL,
    number          INTEGER             NOT NULL,
    type            VARCHAR(50)         NOT NULL,
    account_id      INTEGER             NOT NULL,

    CONSTRAINT fk_deposits_account FOREIGN KEY (account_id) REFERENCES accounts (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)
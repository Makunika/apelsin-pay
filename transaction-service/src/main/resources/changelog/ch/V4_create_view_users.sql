--liquibase formatted sql
--changeset maxim:4

--GRANT EXECUTE ON FUNCTION dblink_connect_u(text) TO "maxbank-transactions";
--GRANT EXECUTE ON FUNCTION dblink_connect_u(text, text) TO "maxbank-transactions";
--CREATE EXTENSION dblink;
select dblink_connect_u('users','host=127.0.0.1 dbname=maxbank-users user=maxbank-users password=maxbank-users');

CREATE OR REPLACE VIEW v_users AS
    SELECT * FROM
    dblink('users', 'SELECT id, email, phone FROM users')
    AS t1(id int, email varchar, phone varchar);
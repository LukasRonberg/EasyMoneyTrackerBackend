create extension if not exists pgcrypto;

alter table users
    add column if not exists password_hash varchar(255);

update users
set password_hash = crypt('ChangeMe123!', gen_salt('bf'))
where password_hash is null;

alter table users
    alter column password_hash set not null;

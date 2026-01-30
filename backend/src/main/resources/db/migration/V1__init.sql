create extension if not exists "uuid-ossp";

create table if not exists users (
    id uuid primary key default uuid_generate_v4(),
    email varchar(255) unique,
    created_at timestamp not null default now()
    );

create table if not exists accounts (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid not null references users(id),
    name varchar(255) not null,
    currency varchar(10) not null,
    created_at timestamp not null default now()
    );

create table if not exists categories (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid references users(id),
    name varchar(255) not null,
    type varchar(20) not null,
    created_at timestamp not null default now()
    );

create table if not exists transactions (
    id uuid primary key default uuid_generate_v4(),
    user_id uuid not null references users(id),
    account_id uuid not null references accounts(id),
    category_id uuid references categories(id),
    amount numeric(19,2) not null,
    transaction_date date not null,
    description text,
    merchant text,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
    );

create index if not exists idx_transactions_user_date
    on transactions(user_id, transaction_date);

create index if not exists idx_transactions_account_date
    on transactions(account_id, transaction_date);
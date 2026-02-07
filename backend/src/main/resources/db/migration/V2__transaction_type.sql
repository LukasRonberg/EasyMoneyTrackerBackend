alter table transactions
    add column transaction_type varchar(20);

update transactions t
set transaction_type = c.type
from categories c
where t.category_id = c.id;

update transactions
set transaction_type = 'EXPENSE'
where transaction_type is null;

alter table transactions
    alter column transaction_type set not null;

alter table transactions
    alter column category_id set not null;

alter table categories
    drop column type;

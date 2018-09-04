begin;
alter table defaults add column cluster boolean not null default 'true';
commit;

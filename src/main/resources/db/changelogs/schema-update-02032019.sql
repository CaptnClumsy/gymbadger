begin;
create table regions (
  id bigint not null,
  name varchar(50) not null,
  displayname varchar(50) not null,
  init_pos_zoom integer,
  init_pos_lat double precision,
  init_pos_long double precision,
  primary key (id)
);
insert into regions (id, name, displayname, init_pos_zoom, init_pos_lat, init_pos_long) values (0, 'all', 'All', 15, 51.7519741, -0.3370427);
insert into regions (id, name, displayname, init_pos_zoom, init_pos_lat, init_pos_long) values (1, 'stalbans', 'St Albans', 15, 51.7519741, -0.3370427);
insert into regions (id, name, displayname, init_pos_zoom, init_pos_lat, init_pos_long) values (2, 'potters', 'Potters Bar', 15, 51.6966124, -0.1760931);
alter table areas add column region bigint references regions(id);
alter table defaults add column region bigint references regions(id);
update defaults set region=0;
insert into areas (id, name, region) values (nextval('areaids'), 'Welham Green', 2);
update gyms set areaid=(select id from areas where name='Welham Green') where name='The Hope & Anchor';
update areas set region=1;
update areas set region=2 where name='Potters Bar';
update areas set region=2 where name='Brookmans Park';
commit;
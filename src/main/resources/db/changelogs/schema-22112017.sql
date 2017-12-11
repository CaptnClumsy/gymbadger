create sequence hibernate_sequence START WITH 1 INCREMENT BY 1;

create table users (
  id bigint not null,
  name varchar(50) not null,
  displayname varchar(200) not null,
  admin boolean not null,
  primary key (id)
);

create table defaults (
  userid bigint not null references users(id),
  init_pos_zoom integer,
  init_pos_lat double precision,
  init_pos_long double precision,
  primary key (userid)
);

create table areas (
  id bigint not null,
  name varchar(200) not null,
  primary key (id)
);

create table gyms (
  id bigint not null,
  name varchar(200) not null,
  lat double precision not null,
  long double precision not null,
  park boolean not null,
  areaid bigint not null references areas(id),
  primary key (id)
);

create table user_gym_props (
  id bigint not null,
  userid bigint not null references users(id),
  gymid bigint not null references gyms(id),
  badge_status integer not null,
  badge_percent integer,
  last_raid timestamp,
  primary key (id)
);

create index ugp_by_user on user_gym_props(userid);
create unique index ugp_by_user_gym on user_gym_props(userid, gymid);

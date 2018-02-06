create sequence hibernate_sequence START WITH 1 INCREMENT BY 1;
create sequence gymids START WITH 1 INCREMENT BY 1;
create sequence pokemonids START WITH 1 INCREMENT BY 1;
create sequence areaids START WITH 1 INCREMENT BY 1;

create table users (
  id bigint not null,
  name varchar(50) not null,
  displayname varchar(200) not null,
  admin boolean not null,
  sharedata boolean not null default 'false',
  team integer,
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
  image_url varchar(400),
  deleted boolean not null default 'false',
  primary key (id)
);

create table pokemon (
  id bigint not null,
  name varchar(100) not null,
  raidboss boolean not null,
  primary key (id)
);

create table user_gym_props (
  id bigint not null,
  userid bigint not null references users(id),
  gymid bigint not null references gyms(id),
  badge_status integer not null,
  badge_percent integer,
  last_raid timestamp,
  pokemonid bigint references pokemon(id),
  caught boolean,
  primary key (id)
);
    
create index ugp_by_user on user_gym_props(userid);
create unique index ugp_by_user_gym on user_gym_props(userid, gymid);

create table comments (
  id bigint not null,
  createdate timestamp not null,
  gymid bigint not null references gyms(id),
  userid bigint not null references users(id),
  public boolean not null,
  comment text not null,
  primary key (id)
);

create table user_gym_history (
  id bigint not null,
  datetime timestamp not null,
  userid bigint not null references users(id),
  gymid bigint not null references gyms(id),
  historyid bigint not null,
  type bigint not null,
  primary key (id)
);

create index ugh_by_user on user_gym_history(userid);
create index ugh_by_user_gym on user_gym_history(userid, gymid);

create table user_raid_history (
  id bigint not null,
  last_raid timestamp,
  pokemonid bigint references pokemon(id),
  caught boolean,
  primary key (id)
);

create table user_badge_history (
  id bigint not null,
  badge_status integer not null,
  primary key (id)
);

create table announcements (
  id bigint not null,
  userid bigint references users(id),
  message text,
  type bigint not null,
  primary key (id)
);

create table user_announcements (
  id bigint not null,
  userid bigint references users(id),
  announcementid bigint references announcements(id),
  primary key(id)
);


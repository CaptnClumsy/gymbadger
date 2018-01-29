BEGIN;

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

create table temp_historyids
  as select 
    nextval('hibernate_sequence') as id, userid, gymid, last_raid, pokemonid, caught FROM user_gym_props
    WHERE last_raid is not NULL;

insert into user_gym_history(id, datetime, userid, gymid, historyid, type) (
  select nextval('hibernate_sequence'), last_raid, userid, gymid, id, 1 FROM temp_historyids
);

insert into user_raid_history(id, last_raid, pokemonid, caught) (
  select id, last_raid, pokemonid, caught FROM temp_historyids
); 

drop table temp_historyids;

create table temp_historyids
  as select 
    nextval('hibernate_sequence') as id, userid, gymid, badge_status FROM user_gym_props;

insert into user_gym_history(id, datetime, userid, gymid, historyid, type) (
  select nextval('hibernate_sequence'), current_timestamp, userid, gymid, id, 0 FROM temp_historyids
);
    
insert into user_badge_history(id, badge_status) (
  select id, badge_status FROM temp_historyids
);

drop table temp_historyids;

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

COMMIT;
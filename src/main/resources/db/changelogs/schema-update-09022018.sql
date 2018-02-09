begin;

alter table user_gym_props drop column last_raid;
alter table user_gym_props drop column pokemonid;
alter table user_gym_props drop column caught;

alter table user_gym_props add column lastraidid bigint references user_raid_history(id);

create table temp_last_raid as
  select r.id, h.userid, h.gymid from user_raid_history r, user_gym_history h
  where
  r.id=h.historyid and
  h.id = (
    select MAX(h1.id) from user_raid_history r1, user_gym_history h1
      where r1.id=h1.historyid and
      h1.gymid=h.gymid and
      h1.userid=h.userid and
      r1.last_raid = (select MAX(r2.last_raid) from user_raid_history r2, user_gym_history h2
      where r2.id=h2.historyid and 
      h2.gymid=h1.gymid and 
      h2.userid=h1.userid 
    )
);

update user_gym_props g set lastraidid = l.id from temp_last_raid l where l.userid=g.userid and l.gymid=g.gymid;

drop table temp_last_raid;

commit;
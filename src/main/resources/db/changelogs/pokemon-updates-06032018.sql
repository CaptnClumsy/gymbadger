BEGIN;

insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Exeggutor', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Slowbro', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Aerodactyl', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Claydol', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Starmie', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Charizard', true);

update gyms set name='Park Street War Memorial' where name='War Memorial';

INSERT INTO gyms (id, name, lat, long, park, areaid, image_url) VALUES (nextval('gymids'), 'Wheathampstead Library', 51.811122, -0.28979, false, (SELECT id FROM areas WHERE name='Wheathampstead'), 'https://lh4.ggpht.com/rXwAuur-BS2hzyEbFTKy6Fa2vLd5C2rsBpKFiw_3B0UEWGcWRP7VOb_iz4uuafRpSCxWRWIdNeEGE31o77M');

INSERT INTO gyms (id, name, lat, long, park, areaid) VALUES (nextval('gymids'), 'Everest Spice Lounge',51.763879,-0.238762, false, (SELECT id FROM areas WHERE name='Hatfield'));
INSERT INTO gyms (id, name, lat, long, park, areaid) VALUES (nextval('gymids'), 'Galleria Lions',51.762528,-0.239446, false, (SELECT id FROM areas WHERE name='Hatfield'));
INSERT INTO gyms (id, name, lat, long, park, areaid) VALUES (nextval('gymids'), 'Hatfield Aerodrome Heritage Trail Number 7',51.769754,-0.236256, false, (SELECT id FROM areas WHERE name='Hatfield'));
INSERT INTO gyms (id, name, lat, long, park, areaid) VALUES (nextval('gymids'), 'Hatfield Aerodrome Heritage Trail Number 8',51.765381,-0.237251, false, (SELECT id FROM areas WHERE name='Hatfield'));
INSERT INTO gyms (id, name, lat, long, park, areaid) VALUES (nextval('gymids'), 'The Hopfields',51.771296,-0.223925, false, (SELECT id FROM areas WHERE name='Hatfield'));
INSERT INTO gyms (id, name, lat, long, park, areaid) VALUES (nextval('gymids'), 'The Galleria',51.760499,-0.241231, false, (SELECT id FROM areas WHERE name='Hatfield'));

COMMIT;

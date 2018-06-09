begin;
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Manectric', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Electabuzz', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Jolteon', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Latias', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Pinsir',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Granbull',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Houndoom',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Walrein',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Sneasel',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Misdreavus',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Duskull',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Shuppet',true);

insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Combusken',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Primape',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Breloom',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Hitmonchan',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Hitmonlee',true);

insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Vaporeon',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Sharpedo',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Marshtomp',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Croconaw',true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Shellder',true);

insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Waste Awareness Mural', 51.807341, -0.339049, false, (SELECT id FROM areas WHERE name='Harpenden'), 'http://lh6.ggpht.com/RHiuFqYfdl-dQ-Kn-vnFRTOvmyjc2WE5MsXI10-yNDRxO_QesRQIus1EPm-312cmO45A9VS4K-orD88j-iQr');
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'St Albans Spiritualist Church', 51.750750, -0.325334, false, (SELECT id FROM areas WHERE name='Hatfield Road'), null);
insert into gyms (id, name, short_name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'A Long History of Cricket', 'A Long History of Cricket', 51.808449, -0.352935, false, (SELECT id FROM areas WHERE name='Harpenden'), 'https://lh5.ggpht.com/4aFZb33VbojG54tJdQO6MF7mskwnN0nYpkpMb1G7Bnnaq0CFHco7uYHRPRrLJWwkfItwrRbSiB6s2rH3z67s=s130');

commit;
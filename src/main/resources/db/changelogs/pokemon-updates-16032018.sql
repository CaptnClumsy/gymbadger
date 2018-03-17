begin;
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Manectric', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Electabuzz', true);
insert into pokemon (id, name, raidboss) values (nextval('pokemonids'), 'Jolteon', true);

insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Waste Awareness Mural', 51.807341, -0.339049, false, (SELECT id FROM areas WHERE name='Harpenden'), 'http://lh6.ggpht.com/RHiuFqYfdl-dQ-Kn-vnFRTOvmyjc2WE5MsXI10-yNDRxO_QesRQIus1EPm-312cmO45A9VS4K-orD88j-iQr');

commit;
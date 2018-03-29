begin;
update gyms set deleted=true where name='Colney Heath Community Park';
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Colney Heath Community Park', 51.741881, -0.267596, false, (SELECT id FROM areas WHERE name='Colney Heath'), 'http://lh5.ggpht.com/ulbgIf2AoHScIPMSpqwFxz-HbWSjx14Jo-Tmv29Ra0aeks11AULH1ht3RAUWncId25BxcUCvBwv4F-u7PkeB2Q');
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Woodbury Field Entry Marker', 51.709461, -0.363993, false, (SELECT id FROM areas WHERE name='Bricket Wood'), null);
commit;
BEGIN;
INSERT INTO areas (id, name) VALUES (nextval('areaids'),'Potters Bar');

insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Our Lady and St Vincent Catholic Church', 51.696106, -0.195243, false, (SELECT id FROM areas WHERE name='Potters Bar'), 'http://lh6.ggpht.com/BDmRe3BQQ4EyhkRaZVoUNkERk9WLLPAynbxzPPKIpDMWaB-VvucZemBglL7YaaPL5X1l6A_nalFkuz5wj3RZ');
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Door of Old Ladbrooke School', 51.694582, -0.177404, false, (SELECT id FROM areas WHERE name='Potters Bar'), 'http://lh3.ggpht.com/83D29XflFiw8c7Vk4R3d5VckVCws-KFXT3LbOhICHFy3H6Z-s8_Y9uVrrMjF7_O2Bi1jl7Z7BfaHlnnbZLcqAYFFMgLX0NTfrs1Cmsz7-4Yz0WhF');
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Potter''s Bar Station', 51.697793, -0.193562, false, (SELECT id FROM areas WHERE name='Potters Bar'), 'http://lh5.ggpht.com/v48Ab_fa0WBGm0H8jd9Vm05H20b_y583dHPj2eGdU142sMTgHvE6KIc61W7RO6OWKVnS0JdjLuDajQ5syxoaEA');
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'The virgin And All Saints', 51.69504, -0.178621, false, (SELECT id FROM areas WHERE name='Potters Bar'), 'http://lh6.ggpht.com/mhi7tuYTkAFy8mUz6h38UnQ550uNZ7cGJmGm4r15trO7uAuDlXH8R6i9ug_P3VHiKVzVCqdmrEl53dqh6k8');

COMMIT;
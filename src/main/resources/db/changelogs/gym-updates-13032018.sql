begin;
insert into gyms (id, name, lat, long, park, areaid, image_url) values (nextval('gymids'), 'Brookmans Park Train Station', 51.721402, -0.205144, false, (SELECT id FROM areas WHERE name='Brookmans Park'), 'http://lh5.ggpht.com/yY0zCN2vBEqyy2Ic84fdWv0LspDnaPvfUScQ-a9CTkYanmXEl8QBOI_XJ9ua8jZi_9MUo1ZhGuhsjd4E0fSRKg');
commit;
BEGIN;

INSERT INTO users (id, name, displayname, admin, sharedata) VALUES (0, 'none', 'none', false, false);

INSERT INTO defaults (userid, init_pos_zoom, init_pos_lat, init_pos_long) VALUES (0, 15, 51.7519741, -0.3370427);

COMMIT;
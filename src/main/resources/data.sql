DELETE
FROM users;
DELETE
FROM requests;
DELETE
FROM items;
DELETE
FROM bookings;
DELETE
FROM comments;

ALTER TABLE users
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE requests
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE bookings
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments
    ALTER COLUMN id RESTART WITH 1;

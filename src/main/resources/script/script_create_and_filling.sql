CREATE SCHEMA IF NOT EXISTS cloud_database;

CREATE TABLE IF NOT EXISTS cloud_database.users
(
    id         int4 primary key auto_increment,
    login      varchar(50),
    password   varchar(100),
    auth_token varchar(100)
);

CREATE TABLE IF NOT EXISTS cloud_database.files
(
    id      int4 primary key auto_increment,
    content longblob     not null,
    size    int          not null,
    name    varchar(100) not null unique,
    user_id int4,
    FOREIGN KEY (user_id) references users (id)
);

INSERT INTO cloud_database.users (login, password)
VALUES ('ivan@mail.com', 'HwKc2ObFsoZMSymKxMdJIg=='),
       ('petr@mail.com', 'oaoTOm3hRjS4FcaUyomOIw=='),
       ('sidor@mail.com', 'XHa+fQP8G1ra5beDirdNTA=='),
       ('olga@mail.com', 'u7Ipgw3H0Uk4Fsmuw/urLQ=='),
       ('john@mail.com', 'CXwcLfcj/EVW059a7cwD3w=='),
       ('irina@mail.com', 'u2ec22BM9U318BMSgqKWHA==');

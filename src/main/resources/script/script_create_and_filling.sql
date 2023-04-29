CREATE SCHEMA IF NOT EXISTS cloud_database;

CREATE TABLE IF NOT EXISTS cloud_database.users
(
    id int4 primary key auto_increment,
    login varchar(50),
    password varchar(100),
    auth_token varchar(100)
);

CREATE TABLE IF NOT EXISTS cloud_database.files
(
    id int4 primary key auto_increment,
    content longblob not null,
    size int not null ,
    name varchar(50) not null unique ,
    user_id int4,
    FOREIGN KEY (user_id) references users(id)
);

INSERT INTO cloud_database.users (login, password)
VALUES ('ivan@mail.com', '{bcrypt}$2a$12$3PLC5VYw1Wp.pViZGJ2hH.84VTUgKrUYpPcBTw86bdntlkSFOE0Sm'),
       ('test@mail.com', 'test'),
       ('petr@mail.com', '{bcrypt}$2a$12$w3PKKFfQtamHJ.xoOLCTC.vx7AUU3H83mFkMyWAxLp4q1GOI3Hs4C'),
       ('sidor@mail.com', '{bcrypt}$2a$12$3NBsUqcl6mPdhrYhP5X31.LME.E12y0/53bQ1xamcqqiKdKWfa5.q'),
       ('olga@mail.com', '{bcrypt}$2a$12$cYk5ypHtpNYXTlzZhihXauTpxVeDhIBXasvw/VwYfY41TNhXhUCH2'),
       ('john@mail.com', '{bcrypt}$2a$12$gzbkRcR3iNTJZkN8mmP10e9.a1iJOW4LbNtQ0JlCvP6QnyfZxkqFu'),
       ('irina@mail.com', '{bcrypt}$2a$12$YRbpqOLPp5pZQ1K.sA6wIu0O016JpyG16gXtxOW9hLTQCUADEIhdS');

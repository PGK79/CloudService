CREATE TABLE users
(
    id         int4 primary key auto_increment,
    login      varchar(50),
    password   varchar(100),
    auth_token varchar(100)
);

CREATE TABLE files
(
    id      int4 primary key auto_increment,
    content longblob     not null,
    size    int          not null,
    name    varchar(100) not null unique,
    user_id int4,
    FOREIGN KEY (user_id) references users (id)
);
INSERT INTO users (login, password,auth_token)
VALUES ('ivan@mail.com', 'ivan', 'token'),
       ('petr@mail.com', 'petr','token2');

INSERT INTO files (content, size, name, user_id)
VALUE ('file content', 12, 'filename', 1),
      ('file content two', 16, 'newfilename', 1);
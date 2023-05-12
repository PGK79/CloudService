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

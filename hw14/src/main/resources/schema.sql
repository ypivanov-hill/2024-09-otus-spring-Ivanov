create sequence authors_seq start with 100;
create table authors (
    id bigserial NOT NULL SEQUENCE authors_seq,
    full_name varchar(255),
    primary key (id)
);

create sequence genres_seq start with 100;
create table genres (
    id bigserial NOT NULL SEQUENCE genres_seq,
    name varchar(255),
    primary key (id)
);

create sequence books_seq start with 100;
create table books (
    id bigserial  NOT NULL SEQUENCE books_seq,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);

create table books_genres (
    book_id bigint references books(id) on delete cascade,
    genre_id bigint references genres(id) on delete cascade,
    primary key (book_id, genre_id)
);

create sequence comments_seq start with 100;
create table comments (
    id bigserial  NOT NULL SEQUENCE comments_seq,
    book_id bigint references books(id) on delete cascade,
    text varchar(255),
    primary key (id)
);

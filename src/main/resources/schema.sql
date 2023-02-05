create table if not exists users
(
    id    bigint generated by default as identity not null,
    name  varchar(255)                            not null,
    email varchar(512)                            not null,
    constraint pk_user primary key (id),
    constraint uq_user_email unique (email)
);
create table if not exists items
(
    id          bigint generated by default as identity not null,
    name        varchar(255)                            not null,
    description varchar(4000)                           not null,
    available   boolean                                 not null,
    owner_id    bigint                                  not null
        constraint items_users_id_fk
            references users,
    constraint pk_item primary key (id),
    request     bigint
);
create table if not exists bookings
(
    id         bigint generated by default as identity
        constraint pk_booking
            primary key,
    start_date timestamp without time zone not null,
    end_date   timestamp without time zone not null,
    item_id    bigint                      not null
        constraint bookings_items_id_fk
            references items,
    booker_id  bigint                      not null
        constraint bookings_users_id_fk
            references users,
    status     varchar                     not null
);
create table if not exists comments
(
    id        bigint generated by default as identity
        constraint pk_comment primary key,
    text      varchar(500)                not null,
    item_id   bigint                      not null
        constraint comments_items__fk
            references items,
    author_id bigint                      not null
        constraint comments_users_id_fk
            references users,
    created   timestamp without time zone not null
);
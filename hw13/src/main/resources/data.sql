insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);

insert into comments(book_id, text)
values (1, 'BookComment_1'), (2, 'BookComment_2'), (3, 'BookComment_3'), (2, 'BookComment_4');


insert into users(username, password)
values ('user','$2a$05$Pwkrjb88Bt5LXAPx/8Y2VutxmpKa/bLmMSplKCvI7dCBEjc1PpCbi'),
('admin','$2a$05$Pwkrjb88Bt5LXAPx/8Y2VutxmpKa/bLmMSplKCvI7dCBEjc1PpCbi'),
('guest','$2a$05$Pwkrjb88Bt5LXAPx/8Y2VutxmpKa/bLmMSplKCvI7dCBEjc1PpCbi');

insert into user_roles(user_id, role)
values (1, 'USER'), (2, 'ADMIN'), (3, 'USER');

INSERT INTO acl_sid (id, principal, sid) VALUES
(1, 1, 'admin'),
(2, 1, 'user'),
(3, 1, 'guest'),
(4, 0, 'ROLE_USER'),
(5, 0, 'ROLE_ADMIN');

INSERT INTO acl_class (id, class) VALUES
(1, 'ru.otus.hw.models.Comment'),
(2, 'ru.otus.hw.models.Book'),
(3, 'ru.otus.hw.models.Author'),
(4, 'ru.otus.hw.models.Genre');

INSERT INTO acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting) VALUES
( 1, 1, NULL, 3, 0),
( 1, 2, NULL, 5, 0),
( 1, 3, NULL, 5, 0),
( 1, 4, NULL, 5, 0),

( 2, 1, NULL, 5, 0),
( 2, 2, NULL, 5, 0),
( 2, 3, NULL, 5, 0),

( 3, 1, NULL, 5, 0),
( 3, 2, NULL, 5, 0),
( 3, 3, NULL, 5, 0),

( 4, 1, NULL, 5, 0),
(4, 2, NULL, 5, 0),
( 4, 3, NULL, 5, 0),
( 4, 4, NULL, 5, 0),
( 4, 5, NULL, 5, 0),
( 4, 6, NULL, 5, 0);


--INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) VALUES
--(1, 1, 1, 4, 1, 1, 1, 1),
--(2, 1, 2, 4, 2, 1, 1, 1),
--(3, 1, 3, 3, 1, 1, 1, 1),
--(4, 2, 1, 4, 1, 1, 1, 1),
--(5, 2, 2, 4, 1, 1, 1, 1),
--(6, 3, 1, 4, 1, 1, 1, 1),
--(7, 3, 2, 4, 2, 1, 1, 1);
--Добавить права админу
INSERT INTO acl_entry ( acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT  aoi.id acl_object_identity,
        ROW_NUMBER() OVER (PARTITION BY aoi.id ORDER BY acs.id, asd.id) as ace_order,
        asd.id sid,
        asd.acc_level  mask,
        1 granting,
        1 audit_success,
        1 audit_failure FROM ACL_CLASS acs
CROSS JOIN ACL_SID asd
CROSS join (select 1 acc_level from dual union
            select 2 acc_level from dual union
            select 4 acc_level from dual union
            select 8 acc_level from dual union
            select 16 acc_level from dual  ) asd
JOIN ACL_OBJECT_IDENTITY aoi  on aoi.OBJECT_ID_CLASS  = acs.ID
where asd.sid in ( 'ROLE_ADMIN' )
order by acs.id, asd.id;

--Добавить права роли пользователя для чтения всего
INSERT INTO acl_entry ( acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT  aoi.id acl_object_identity,
        ROW_NUMBER() OVER (PARTITION BY aoi.id ORDER BY acs.id, asd.id) + (SELECT max(ae_x.ACE_ORDER ) FROM ACL_ENTRY ae_x where ae_x.ACL_OBJECT_IDENTITY  =  aoi.id ) as ace_order,
        asd.id sid,
        1 mask,
        1 granting,
        1 audit_success,
        1 audit_failure FROM ACL_CLASS acs
CROSS JOIN ACL_SID asd
JOIN ACL_OBJECT_IDENTITY aoi  on aoi.OBJECT_ID_CLASS  = acs.ID
where asd.sid in ('ROLE_USER')
 and acs.class != 'ru.otus.hw.models.Comment'
order by acs.id, asd.id;

--Добавить права user для комментариев
INSERT INTO acl_entry ( acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
SELECT  aoi.id acl_object_identity,
        ROW_NUMBER() OVER (PARTITION BY aoi.id ORDER BY acs.id, asd.id) + (SELECT max(ae_x.ACE_ORDER ) FROM ACL_ENTRY ae_x where ae_x.ACL_OBJECT_IDENTITY  =  aoi.id ) as ace_order,
        asd.id sid,
        asd.acc_level  mask,
        1 granting,
        1 audit_success,
        1 audit_failure FROM ACL_CLASS acs
CROSS JOIN ACL_SID asd
CROSS JOIN (select 1 acc_level from dual union select 2 acc_level from dual union select 8 acc_level from dual  ) asd
JOIN ACL_OBJECT_IDENTITY aoi  on aoi.OBJECT_ID_CLASS  = acs.ID
where asd.sid in ('user')
 and acs.class = 'ru.otus.hw.models.Comment'
order by acs.id, asd.id;


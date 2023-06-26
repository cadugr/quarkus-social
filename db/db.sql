create database quarkus-social;

CREATE TABLE USERS (
	id bigserial NOT NULL PRIMARY KEY,
	name varchar(100) NOT NULL,
	age integer NOT NULL
);

CREATE TABLE POSTS (
	id bigserial NOT NULL PRIMARY KEY,
	post_text varchar(150) NOT NULL,
	dateTime timestamp not null, 
  user_id bigint not null references USERS (id)
);

create table FOLLOWERS (
	id bigserial not null primary key,
	user_id bigint not null references USERS(id),
	follower_id bigint not null references USERS(id)
);
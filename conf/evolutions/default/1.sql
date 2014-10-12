# Category schema

# --- !Ups

CREATE SEQUENCE category_id_seq;
CREATE TABLE category (
    id integer NOT NULL DEFAULT nextval('category_id_seq'),
    name varchar(255)
);

CREATE SEQUENCE expense_id_seq;
CREATE TABLE expense (
    id integer NOT NULL DEFAULT nextval('expense_id_seq'),
    amount double NOT NULL,
    date date NOT NULL,
    category integer NOT NULL
);

# --- !Downs

DROP TABLE category;
DROP SEQUENCE category_id_seq;

DROP TABLE expense;
DROP SEQUENCE expense_id_seq;

-- �ShoppingAssistant�- create an app which will have a shopping list tagged
-- with different categories and a list with information about shops. Using
-- location services, identify when the user is around a shop and notify him
-- about items from shopping list which can be found in the shop.�

DROP TABLE STORES_CATEGORIES;
DROP TABLE STORES;
DROP TABLE PRODUCTS;
DROP TABLE CATEGORIES;
DROP TABLE LISTS;
DROP TABLE USERS;
COMMIT;
/

CREATE TABLE USERS(
  "ID" NUMBER(10, 0) NOT NULL,
  "USERNAME" VARCHAR2(255) NOT NULL,
  "PASSWORD" VARCHAR2(255) NOT NULL,
  "NAME" VARCHAR2(255),
  "PHOTO" BLOB,
  CONSTRAINT users_unique UNIQUE (ID)
);
/
CREATE TABLE LISTS(
  "ID" NUMBER(10, 0) NOT NULL,
  "NAME" VARCHAR2(255) NOT NULL,
  "USER_ID" NUMBER(10, 0) NOT NULL,
  CONSTRAINT fk_users
    FOREIGN KEY (user_id)
    REFERENCES USERS(ID),
  CONSTRAINT lists_unique UNIQUE(ID)
);
/
CREATE TABLE CATEGORIES(
  "ID" NUMBER(10, 0) NOT NULL,
  "NAME" VARCHAR2(255) NOT NULL,
  CONSTRAINT categories_unique UNIQUE(ID)
);
/
CREATE TABLE PRODUCTS(
  "ID" NUMBER(10, 0) NOT NULL,
  "NAME" VARCHAR2(255),
  "LIST_ID" NUMBER(10,0) NOT NULL,
  "CATEGORY_ID" NUMBER(10,0) NOT NULL,
  CONSTRAINT fk_lists
    FOREIGN KEY (list_id)
    REFERENCES LISTS(ID),
  CONSTRAINT fk_categories
    FOREIGN KEY (category_id)
    REFERENCES CATEGORIES(ID)
);
/
CREATE TABLE STORES(
  "ID" NUMBER(10, 0) NOT NULL,
  "NAME" VARCHAR2(255) NOT NULL,
  "COORDINATES_LAT" DECIMAL(9,6) NOT NULL,
  "COORDINATES_LNG" DECIMAL(9,6) NOT NULL,
  CONSTRAINT stores_unique UNIQUE(ID)
);
/
CREATE TABLE STORES_CATEGORIES(
  "STORE_ID" NUMBER(10,0) NOT NULL,
  "CATEGORY_ID" NUMBER(10,0) NOT NULL,
  CONSTRAINT fk_stores_categories
    FOREIGN KEY (STORE_ID)
    REFERENCES STORES(ID),
  CONSTRAINT fk_categories_stores
    FOREIGN KEY (CATEGORY_ID)
    REFERENCES CATEGORIES(ID)
);
/
COMMIT;
/

declare
  v_id  number := 1;
  v_username varchar2(20);
  v_password varchar2(20);
begin

while (v_id < 1000)
  loop
    v_username := 'username' || v_id;
    v_password := 'password' || v_id;

    INSERT INTO USERS
    (ID,USERNAME,PASSWORD)
     values(v_id, v_username, v_password);

    v_id := v_id +1;
  end loop;
commit;
end;
/

declare
  v_id  number := 1;
  v_name varchar2(20);
begin
while (v_id < 100)
  loop
    v_name := 'Categorie' || v_id;

    INSERT INTO CATEGORIES
    (ID,NAME)
     values(v_id, v_name);

    v_id := v_id +1;
  end loop;
commit;
end;
/
declare
  v_id  number := 200;
  v_name varchar2(20);
  v_user_id users.id%TYPE;
begin
while (v_id < 300)
  loop
    v_name := 'Lista' || v_id;
    
    SELECT ID
    INTO v_user_id
    FROM (
        SELECT ID
        FROM USERS
        ORDER BY DBMS_RANDOM.VALUE
        )
    WHERE ROWNUM = 1;

    INSERT INTO LISTS
    (ID,NAME,USER_ID)
     values(v_id, v_name, v_user_id);

    v_id := v_id +1;
  end loop;
commit;
end;
/

declare
  v_id  number := 1;
  v_name varchar2(20);
  v_coordinates_lat Decimal(9,6) := 47.154559;
  v_coordinates_lng Decimal(9,6) := 27.154559;
begin

while (v_id < 100)
  loop
    v_name := 'Magazin' || v_id;
    v_coordinates_lat := v_coordinates_lat + 0.1;
    v_coordinates_lng := v_coordinates_lng + 0.1;

    INSERT INTO STORES
    (ID,NAME,COORDINATES_LAT, COORDINATES_LNG)
     values(v_id, v_name, v_coordinates_lat, v_coordinates_lng);

    v_id := v_id +1;
  end loop;
commit;
end;
/

declare
  v_id number := 0;
  v_store_id stores.id%TYPE;
  v_category_id categories.id%TYPE;
begin
while (v_id < 100)
  loop
    SELECT ID
    INTO v_store_id
    FROM (
        SELECT ID
        FROM STORES
        ORDER BY DBMS_RANDOM.VALUE
        )
    WHERE ROWNUM=1;
    
    SELECT ID
    INTO v_category_id
    FROM (
        SELECT ID
        FROM CATEGORIES
        ORDER BY DBMS_RANDOM.VALUE)
    WHERE ROWNUM=1;

    INSERT INTO STORES_CATEGORIES
    (STORE_ID,CATEGORY_ID)
     values(v_store_id, v_category_id);

    v_id := v_id +1;
  end loop;
commit;
end;
/

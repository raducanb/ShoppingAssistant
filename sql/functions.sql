CREATE OR REPLACE PACKAGE auth IS
    FUNCTION login_with_credentials (v_username USERS.USERNAME%TYPE, 
                                        v_password USERS.PASSWORD%TYPE)
        RETURN NUMBER;
END auth;

CREATE OR REPLACE PACKAGE BODY auth IS
    FUNCTION login_with_credentials(v_username USERS.USERNAME%TYPE, 
                                    v_password USERS.PASSWORD%TYPE)
    RETURN NUMBER AS
        username_inexistent exception;
        PRAGMA EXCEPTION_INIT(username_inexistent, -20001);
        password_incorrect exception;
        PRAGMA EXCEPTION_INIT(password_incorrect, -20002);
        v_user_id USERS.USERNAME%TYPE;
        v_user_password USERS.PASSWORD%TYPE;
    BEGIN
        SELECT id, password
        INTO v_user_id, v_user_password
        FROM users
        WHERE username = v_username;
        
        IF v_user_password = v_password THEN
            RETURN v_user_id;
        ELSE
            raise_application_error (-20002,'Parola gresita pentru user-ul cu username ' || v_username || '.');
        END IF;
    EXCEPTION
        WHEN no_data_found THEN
            raise_application_error (-20001,'User-ul cu username ' || v_username || ' nu exista.');
    END;
END auth;

DROP PACKAGE shopping_lists;
CREATE OR REPLACE PACKAGE shopping_lists IS
    FUNCTION get_for_user_id(v_user_id USERS.USERNAME%TYPE, v_page NUMBER)
        RETURN SYS_REFCURSOR;
END shopping_lists;

CREATE OR REPLACE PACKAGE BODY shopping_lists IS
    FUNCTION get_for_user_id(v_user_id USERS.USERNAME%TYPE, v_page NUMBER)
    RETURN SYS_REFCURSOR AS
        l_rc SYS_REFCURSOR;
    BEGIN
        OPEN l_rc
        FOR 
        SELECT id, name, user_id
        FROM (SELECT id, name, user_id, rownum rn
              FROM (SELECT *
                    FROM LISTS l
                    WHERE user_id = v_user_id
                    ORDER BY ID)
              WHERE rownum < 20 * v_page)
        WHERE rn >= 20 * (v_page - 1);
        
        RETURN l_rc;
    END get_for_user_id;
END shopping_lists;

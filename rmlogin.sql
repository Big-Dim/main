USE test_saturn;

DELIMITER $$

CREATE DEFINER = 'root'@'localhost'
PROCEDURE rmlogin (IN in_user varchar(20), IN in_password varchar(20), IN in_token varchar(40), IN in_type varchar(25))
BEGIN
  SELECT
    u.login,
    u.password,
    @cnt := COUNT(*)
  FROM users AS u
  WHERE u.login = in_user
  AND u.password = in_password;
  SELECT
    u.login,
    @usr := COUNT(*)
  FROM users AS u
  WHERE u.login = in_user;

  IF in_type = 'LOGIN_CUSTOMER'
  THEN
    SET @msg := 'PASSWORD_ERROR';
    IF @usr < 1
    THEN
      SET @msg := 'ERROR_CUSTOMER';
    END IF;
    IF @cnt = 1
    THEN
      SET @msg := 'CUSTOMER_LOGIN';
      SELECT
        @token := token,
        @token_date := DATE_ADD(timest, INTERVAL 1 DAY)
      FROM tokens
      WHERE login = in_user
      AND timest = (SELECT
          MAX(timest)
        FROM tokens
        WHERE login = in_user);
    END IF;
  ELSEIF in_type = 'REGISTER_CUSTOMER'
  THEN
    SET @msg := 'CUSTOMER_REGISTER';
    IF @usr > 0
    THEN
      SET @msg := 'CUSTOMER_RELOGIN';
    END IF;
    IF EXISTS (SELECT
          *
        FROM tokens
        WHERE token = in_token)
    THEN
      SET @msg := 'TOKEN_DUPLICATE';
    ELSE
      REPLACE users (login, password)
        VALUES (in_user, in_password);
      INSERT INTO tokens (login, token)
        VALUES (in_user, in_token);
      SELECT
        @token := token,
        @token_date := DATE_ADD(timest, INTERVAL 1 DAY)
      FROM tokens
      WHERE login = in_user
      AND timest = (SELECT
          MAX(timest)
        FROM tokens
        WHERE login = in_user);
    END IF;
  ELSE
    SET @msg := 'TYPE_ERROR';
  END IF;

  SELECT
    'MSG',
    @msg,
    @token,
    @token_date;
END
$$

DELIMITER ;

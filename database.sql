--
-- Скрипт сгенерирован Devart dbForge Studio for MySQL, Версия 6.3.358.0
-- Домашняя страница продукта: http://www.devart.com/ru/dbforge/mysql/studio
-- Дата скрипта: 30/01/2016 10:58:52 PM
-- Версия сервера: 5.1.28-rc-community
-- Версия клиента: 4.1
--


-- 
-- Отключение внешних ключей
-- 
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- 
-- Установить режим SQL (SQL mode)
-- 
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 
-- Установка кодировки, с использованием которой клиент будет посылать запросы на сервер
--
SET NAMES 'utf8';

-- 
-- Установка базы данных по умолчанию
--
USE test_saturn;

--
-- Описание для таблицы users
--
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  login VARCHAR(20) NOT NULL,
  password VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (login)
)
ENGINE = INNODB
AVG_ROW_LENGTH = 4096
CHARACTER SET cp1251
COLLATE cp1251_general_ci;

--
-- Описание для таблицы tokens
--
DROP TABLE IF EXISTS tokens;
CREATE TABLE tokens (
  login VARCHAR(20) NOT NULL,
  token VARCHAR(40) NOT NULL,
  timest TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (login, token),
  CONSTRAINT FK_tokens_users_login FOREIGN KEY (login)
    REFERENCES users(login) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE = INNODB
AVG_ROW_LENGTH = 606
CHARACTER SET cp1251
COLLATE cp1251_general_ci;

DELIMITER $$

--
-- Описание для процедуры rmlogin
--
DROP PROCEDURE IF EXISTS rmlogin$$
CREATE DEFINER = 'root'@'localhost'
PROCEDURE rmlogin(IN in_user VARCHAR(20), IN in_password VARCHAR(20), IN in_token VARCHAR(40), IN in_type VARCHAR(25))
BEGIN
  SELECT u.login, u.password, @cnt := COUNT(*)   FROM users as u WHERE u.login = in_user AND u.password = in_password;
  SELECT u.login, @usr := COUNT(*)   FROM users as u WHERE u.login = in_user ;

  IF in_type = 'LOGIN_CUSTOMER' THEN
    SET @msg := 'PASSWORD_ERROR'; 
    IF @usr < 1 THEN
      SET @msg := 'ERROR_CUSTOMER';
    END IF;
    IF @cnt = 1 THEN
      SET @msg := 'CUSTOMER_LOGIN';
      SELECT @token :=token, @token_date :=  DATE_ADD(timest, INTERVAL 1 DAY) FROM tokens WHERE login = in_user  AND timest = (SELECT MAX(timest) FROM tokens WHERE login = in_user);
    END IF;
  ELSEIF  in_type = 'REGISTER_CUSTOMER' THEN
    SET @msg := 'CUSTOMER_REGISTER';
    IF @usr>0 THEN
      SET @msg := 'CUSTOMER_RELOGIN';
    END IF;
    IF EXISTS(SELECT * FROM tokens WHERE token = in_token) THEN
        SET @msg := 'TOKEN_DUPLICATE';
    ELSE  
      REPLACE users (login, password) VALUES (in_user,in_password);
      INSERT INTO tokens (login, token) VALUES (in_user, in_token);
      SELECT @token :=token, @token_date :=  DATE_ADD(timest, INTERVAL 1 DAY) FROM tokens WHERE login = in_user  AND timest = (SELECT MAX(timest) FROM tokens WHERE login = in_user);
    END IF;
  ELSE
    SET @msg := 'TYPE_ERROR';
  END IF;

  SELECT 'MSG', @msg, @token, @token_date;
END
$$

DELIMITER ;

-- 
-- Вывод данных для таблицы users
--
INSERT INTO users VALUES
('first', 'first'),
('new', 'new'),
('second', 'second'),
('third', 'third');

-- 
-- Вывод данных для таблицы tokens
--
INSERT INTO tokens VALUES
('First', '01df1bea-c212-4bf3-bd03-68ba7abcbb56', '2016-01-30 11:49:40'),
('First', '02cbf2e0-0477-4223-9da2-e987c00f238c', '2016-01-28 19:47:59'),
('First', '12345', '2016-01-28 15:44:41'),
('First', '123456', '2016-01-28 16:07:36'),
('First', '123456789', '2016-01-30 10:36:08'),
('First', '1234567890', '2016-01-30 10:36:48'),
('First', '234567890', '2016-01-30 10:37:15'),
('First', '272b511c-1125-479b-8e32-46f78b05f87b', '2016-01-28 20:09:14'),
('First', '2db3f2fe-fc5e-4a95-8b43-df33c19a607a', '2016-01-28 20:04:11'),
('First', '34567890', '2016-01-30 11:12:59'),
('First', '376a7b92-b177-4f68-b7a1-c59985b730ae', '2016-01-28 18:36:14'),
('First', '3d403b6f-aa48-45a1-963b-086e567f2bcf', '2016-01-28 20:09:40'),
('First', '4567890', '2016-01-30 11:13:31'),
('First', '4635d239-2705-40af-8029-41afd064f48c', '2016-01-28 22:01:14'),
('First', '52d11099-0313-4024-9989-8f6c0e74e096', '2016-01-28 18:48:21'),
('First', '567890', '2016-01-30 11:16:03'),
('First', '67890', '2016-01-30 11:16:31'),
('first', '7890', '2016-01-30 21:00:59'),
('First', '8ed18ad2-f6b7-4c8e-b05c-c838a15176d6', '2016-01-28 19:21:01'),
('First', 'c11ae4d2-e8d6-4d6a-ba5c-fc596f67efbe', '2016-01-28 19:21:48'),
('first', 'c13fb9ff-65ed-4d9c-b6e5-7c3a269b1c89', '2016-01-30 21:13:14'),
('first', 'c5961f30-a310-4d03-81f7-0bc974db6611', '2016-01-30 21:10:33'),
('First', 'ea5f3558-d066-4683-819a-7e2f3b66e434', '2016-01-28 18:48:47'),
('first', 'f0005cef-e3d9-4c7c-80fb-8f9622e3475f', '2016-01-30 21:10:59'),
('First', 'f985db7a-dbc8-4bc4-80e7-6409d7d2492a', '2016-01-28 19:46:44'),
('new', '7890', '2016-01-30 21:01:15'),
('second', '4ae63460-c6b1-4213-b145-0b2cde5aee1b', '2016-01-30 21:26:40');

-- 
-- Восстановить предыдущий режим SQL (SQL mode)
-- 
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

-- 
-- Включение внешних ключей
-- 
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

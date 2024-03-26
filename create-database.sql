DROP DATABASE IF EXISTS `CompanyDB`;
CREATE DATABASE `CompanyDB`; 
USE `CompanyDB`;

SET NAMES utf8 ;
SET character_set_client = utf8mb4 ;

CREATE TABLE `employees` (
  `employee_id` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `lastname` varchar(50) NOT NULL,
  `date_of_birth` date NOT NULL,
  `salary` decimal(9,2) NOT NULL,
--   `condition` enum ('OBECNY', 'DELEGACJA', 'CHORY', 'NIEOBECNY'),
  `e_condition` varchar(50) NOT NULL,
  PRIMARY KEY (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `employees` (`name`, `lastname`, `date_of_birth`, `salary`, `e_condition`) VALUES 
("Jan", "Kowalski", '1998-01-25', 8000, 'OBECNY'),
("Kim", "Kardashian", '1980-10-21', 7000, 'NIEOBECNY'),
("Jennifer", "Lawrence", '1990-08-15', 9500, 'DELEGACJA'),
("Christian", "Bale", '1974-01-30', 6000, 'CHORY'),
("Chris", "Evans", '1981-06-13', 7000, 'NIEOBECNY'),
("Angelina", "Jolie", '1975-06-04', 8000, 'OBECNY'),
("Tom", "Cruise", '1962-06-03', 5000, 'OBECNY'),
("Robert", "Downey Jr.", '1998-04-04', 7500, 'DELEGACJA'),
("Meryl", "Streep", '1998-06-22', 8500, 'OBECNY'),
("Selena", "Gomez", '1992-07-22', 9000, 'OBECNY'); 
SELECT * FROM `employees`;

CREATE TABLE `e_groups` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `max` int(11) NOT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `e_groups` (`name`, `max`) VALUES 
("All", 100),
("Programmers", 5),
("Managers", 5),
("Testers", 3);
SELECT * FROM `e_groups`;

CREATE TABLE `employee_group` (
  `employee_id` tinyint(4) NOT NULL,
  `group_id` int(11) NOT NULL,
  FOREIGN KEY (`employee_id`) REFERENCES `employees`(`employee_id`),
  FOREIGN KEY (`group_id`) REFERENCES `e_groups`(`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `employee_group` (`employee_id`, `group_id`) VALUES 
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1),
(5, 2), (4, 2), (9, 2), (10, 2), 
(2, 3), (3, 3), (9, 3),
(1, 4), (7, 4);
SELECT * FROM `employee_group`;

CREATE TABLE `rates` (
    `rate_id` INT(11) NOT NULL AUTO_INCREMENT,
    `value` INT NOT NULL CHECK (`value` >= 0 AND `value` <= 6),
    `group_id` INT NOT NULL,
    `date` DATE NOT NULL,
    `comment` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`rate_id`),
    FOREIGN KEY (`group_id`) REFERENCES `e_groups`(`group_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
INSERT INTO `rates` (`value`, `group_id`,`date`, `comment`) VALUES 
(3, 1,'2023-06-12', "Nadgodziny"), 
(2, 2, '2023-09-10', "Trzymanie sie deadline'ow"),
(5, 3,'2023-12-10', "Praca w weekend"),
(6, 3,'2022-12-24', "Praca w swieta");
SELECT * FROM `rates`;
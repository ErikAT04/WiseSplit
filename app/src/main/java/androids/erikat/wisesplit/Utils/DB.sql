DROP DATABASE IF EXISTS `P3_ANDROID_STUDIO`;
CREATE DATABASE `P3_ANDROID_STUDIO`;
USE `P3_ANDROID_STUDIO`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`(
	email VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    image VARCHAR(255) DEFAULT 'https://i.discogs.com/_kK2FFfyrhNnbdTjCGMqfy_2gsMw120aUhKTb3M9kyE/rs:fit/g:sm/q:40/h:300/w:300/czM6Ly9kaXNjb2dz/LWRhdGFiYXNlLWlt/YWdlcy9SLTEzMTk5/NTg3LTE1NDk4MTEy/MjMtNTczMC5qcGVn.jpeg'
);

DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(255) NOT NULL,
    image VARCHAR(255) DEFAULT 'https://static.vecteezy.com/system/resources/previews/023/547/344/non_2x/group-icon-free-vector.jpg'
);

DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group`(
	user_email VARCHAR(255) REFERENCES `user`(email)
    ON UPDATE CASCADE ON DELETE CASCADE,
    group_id VARCHAR(255) REFERENCES `group`(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    is_admin BOOLEAN DEFAULT FALSE,
    PRIMARY KEY(user_email, group_id)
);

DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`(
	notif_id INTEGER PRIMARY KEY AUTO_INCREMENT,
	user_email VARCHAR(255) REFERENCES `user`(email)
    ON UPDATE CASCADE ON DELETE CASCADE,
    group_id VARCHAR(255) REFERENCES `group`(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    notif_date DATE
);

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`(
	payment_id INTEGER PRIMARY KEY AUTO_INCREMENT,
    payer_email VARCHAR(255) REFERENCES `user`(email)
    ON UPDATE CASCADE ON DELETE CASCADE,
    group_id VARCHAR(255) REFERENCES `group`(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    payment_date DATE,
    payment_args VARCHAR(255) NOT NULL,
    total_payment DOUBLE NOT NULL
);

DROP TABLE IF EXISTS `user_payment`;
CREATE TABLE `user_payment`(
	payment_id INTEGER REFERENCES `payment`(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    user_email VARCHAR(255) REFERENCES `user`(email)
    ON UPDATE CASCADE ON DELETE CASCADE,
    quantity DOUBLE DEFAULT 0,
    paid BOOLEAN DEFAULT FALSE,
    PRIMARY KEY(payment_id, user_email)
);

INSERT INTO `user`(email, username, password) values ('erik@gmail.com', 'ErikAT', sha2('erik', 256));
INSERT INTO `group`(group_name) values('Grupo de prueba');
INSERT INTO `user_group`(user_email, group_id, is_admin) values ('erik@gmail.com', 1, true);

INSERT INTO `user`(email, username, password) values
('prueba1@gmail.com', 'Prueba1', sha2('prueba', 256)),
('prueba2@gmail.com', 'Prueba2', sha2('prueba', 256)),
('prueba3@gmail.com', 'Prueba3', sha2('prueba', 256)),
('prueba4@gmail.com', 'Prueba4', sha2('prueba', 256)),
('prueba5@gmail.com', 'Prueba5', sha2('prueba', 256));

INSERT INTO `user_group`(user_email, group_id) values
('prueba1@gmail.com', 1),
('prueba2@gmail.com', 1),
('prueba3@gmail.com', 1),
('prueba4@gmail.com', 1);

INSERT INTO `payment`(payer_email, group_id, payment_date, payment_args, total_payment) values ('prueba1@gmail.com', 1, NOW(), "Concepto de Prueba", 45);

INSERT INTO `user_payment`(payment_id, user_email, quantity, paid) VALUES
(1, 'prueba2@gmail.com', 15, false),
(1, 'prueba3@gmail.com', 15, false),
(1, 'prueba4@gmail.com', 15, true);

INSERT INTO `notification`(user_email, group_id, notif_date) values('prueba5@gmail.com', 1, NOW());
create table user (username varchar(50) primary key, 
								password varchar(50) not null);

create table mail (id int primary key, 
								sender varchar(50), 
								addressee varchar(50), 
								subject varchar(100) , 
								content text, 
								senddate timestamp);

create table attachment (id int primary key, 
											position varchar(100), 
											mail_id int, 
											serial_number int, 
											offset varchar(50), 
											addressee_name varchar(50));
											
alter table mail add constraint sender_fk foreign key (sender)  references user (username);
alter table mail add constraint addressee_fk foreign key (addressee)  references user (username);

alter table attachment add constraint mail_id_fk foreign key (mail_id)  references mail (id);
alter table attachment add constraint offset_fk foreign key (offset)  references user (username);
alter table attachment add constraint addressee_name_fk foreign key (addressee_name)  references user (username);


CREATE TABLE `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `position` varchar(100) DEFAULT NULL,
  `mail_id` int(11) DEFAULT NULL,
  `offset` int(11) DEFAULT NULL COMMENT '在附件列表中的序号，每个邮件有一个附件列表',
  `sender_name` varchar(50) DEFAULT NULL,
  `addressee_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mail_id_fk` (`mail_id`),
  KEY `sender_name_fk` (`sender_name`),
  KEY `addressee_name_fk` (`addressee_name`),
  CONSTRAINT `addressee_name_fk` FOREIGN KEY (`addressee_name`) REFERENCES `user` (`username`),
  CONSTRAINT `mail_id_fk` FOREIGN KEY (`mail_id`) REFERENCES `mail` (`id`),
  CONSTRAINT `sender_name_fk` FOREIGN KEY (`sender_name`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE `mail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender` varchar(50) DEFAULT NULL,
  `addressee` varchar(50) DEFAULT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `content` text,
  `sendtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `attachment_1` varchar(50) DEFAULT NULL,
  `attachment_2` varchar(50) DEFAULT NULL,
  `attachment_3` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sender_fk` (`sender`),
  KEY `addressee_fk` (`addressee`),
  CONSTRAINT `addressee_fk` FOREIGN KEY (`addressee`) REFERENCES `user` (`username`),
  CONSTRAINT `sender_fk` FOREIGN KEY (`sender`) REFERENCES `user` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `bysj`.`mail` 
DROP COLUMN `attachment_3`,
DROP COLUMN `attachment_2`,
CHANGE COLUMN `attachment_1` `attachments` TEXT NULL DEFAULT NULL ;


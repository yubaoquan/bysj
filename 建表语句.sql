CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mail` (
  `id` int(11) NOT NULL,
  `sender` varchar(50) DEFAULT NULL,
  `addressee` varchar(50) DEFAULT NULL,
  `subject` varchar(100) DEFAULT NULL,
  `content` text,
  `sendtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `attachments` text,
  PRIMARY KEY (`id`),
  KEY `sender_fk` (`sender`),
  KEY `addressee_fk` (`addressee`),
  CONSTRAINT `sender_fk` FOREIGN KEY (`sender`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `location` varchar(100) DEFAULT NULL,
  `mail_id` int(11) DEFAULT NULL,
  `offset` int(11) DEFAULT NULL COMMENT '在附件列表中的序号，每个邮件有一个附件列表',
  PRIMARY KEY (`id`),
  KEY `mail_id_fk` (`mail_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

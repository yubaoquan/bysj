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


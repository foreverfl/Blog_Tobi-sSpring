create table member (
	id int primary key,
	name varchar(100) not null,
	point double not null
);

create table register (
	id int primary key auto_increment,
	name varchar(100) not null
);

DELIMITER $$
create function find_name(in_id INT) 
returns varchar(255) 
begin
	declare out_name varchar(255);
	select name 
		into out_name
		from member	
		where id = in_id;
	return out_name;
end $$
DELIMITER ;
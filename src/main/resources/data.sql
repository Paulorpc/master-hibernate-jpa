insert into course(id, name, created_date, updated_date) 
values(10001,'JPA in 50 Steps', sysdate(), sysdate());
insert into course(id, name, created_date, updated_date) 
values(10002,'Spring in 50 Steps', sysdate(), sysdate());
insert into course(id, name, created_date, updated_date) 
values(10003,'Spring Boot in 100 Steps', sysdate(), sysdate());


insert into passport(id,number)
values(40001,'E123456');
insert into passport(id,number)
values(40002,'N123457');
insert into passport(id,number)
values(40003,'L123890');

insert into student(id,name,passport_id)
values(20001,'Ranga',40001);
insert into student(id,name,passport_id)
values(20002,'Adam',40002);
insert into student(id,name,passport_id)
values(20003,'Jane',40003);

insert into review(id,rating,description)
values(50001,'FIVE', 'Great Course');
insert into review(id,rating,description)
values(50002,'FOUR', 'Wonderful Course');
insert into review(id,rating,description)
values(50003,'FIVE', 'Awesome Course');
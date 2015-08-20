drop table page;

create table page (id string, list array<struct<a:int,b:string>>)
row format delimited fields terminated by '|' stored as textfile;

LOAD DATA LOCAL INPATH '../../data/files/page.txt' INTO TABLE page;

select * from page lateral view inline(page.list) page;

select * from page;
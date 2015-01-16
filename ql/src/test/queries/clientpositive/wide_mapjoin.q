set hive.auto.convert.join=true;
set hive.auto.convert.join.noconditionaltask=true;
set hive.auto.convert.join.noconditionaltask.size=1000000;

create table alltypesorc1 STORED AS ORC as
select ctinyint as c1, cint as c2, cbigint as c3, ctinyint as c4, cint as c5, cbigint as c6, cint as c7, cbigint as c8, cbigint as c9, cstring1 as c10 from alltypesorc;
create table alltypesorc2 STORED AS ORC as
select ctinyint as c11, cint as c12, cbigint as c13, ctinyint as c14, cint as c15, cbigint as c16, cint as c17, cbigint as c18, cbigint as c19, cstring1 as c20 from alltypesorc where ctinyint is not null and cint is not null and cbigint is not null limit 0;


explain
select unix_timestamp(), alltypesorc1.* from alltypesorc1 join alltypesorc2 on (c1 = c11 and c2 = c12 and c3 = c13 and c4 = c14 and c5 = c15 and c6 = c16 and c7 = c17 and c8 = c18 and c9 = c19);

select unix_timestamp(), alltypesorc1.* from alltypesorc1 join alltypesorc2 on (c1 = c11 and c2 = c12 and c3 = c13 and c4 = c14 and c5 = c15 and c6 = c16 and c7 = c17 and c8 = c18 and c9 = c19);
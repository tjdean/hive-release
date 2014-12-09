SET hive.vectorized.execution.enabled=true;
set hive.fetch.task.conversion=minimal;

create table match_txt (
  num int,
  ts timestamp
) row format delimited fields terminated by '|' stored as textfile;

load data local inpath '../../data/files/small_ts.txt' overwrite into table match_txt;

create table match_orc stored as orc as select * from match_txt;

explain
select * from match_txt;

select * from match_txt;

explain
select * from match_orc;

select * from match_orc;

explain
select to_date(ts) from match_txt;

select to_date(ts) from match_txt;

explain
select to_date(ts) from match_orc;

select to_date(ts) from match_orc;

explain
select distinct to_date(ts) from match_txt;

select distinct to_date(ts) from match_txt;

explain
select distinct to_date(ts) from match_orc;

select distinct to_date(ts) from match_orc; 

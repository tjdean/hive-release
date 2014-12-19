SET hive.vectorized.execution.enabled=false;
set hive.fetch.task.conversion=minimal;

create table str_str_txt (str1 string, str2 string) 
row format delimited fields terminated by '|' 
stored as textfile;

LOAD DATA LOCAL INPATH '../../data/files/string_coalesce' OVERWRITE INTO TABLE str_str_txt;

create table str_str_orc stored as orc as select * from str_str_txt;

EXPLAIN
SELECT
   str2, ROUND(sum(cast(COALESCE(str1, 0) as int))/60, 2) as result
from str_str_orc
GROUP BY str2;

SELECT
   str2, ROUND(sum(cast(COALESCE(str1, 0) as int))/60, 2) as result
from str_str_orc
GROUP BY str2;

EXPLAIN
SELECT COALESCE(str1, 0) as result
from str_str_orc;

SELECT COALESCE(str1, 0) as result
from str_str_orc;

SET hive.vectorized.execution.enabled=true;

EXPLAIN
SELECT
   str2, ROUND(sum(cast(COALESCE(str1, 0) as int))/60, 2) as result
from str_str_orc
GROUP BY str2;

SELECT
   str2, ROUND(sum(cast(COALESCE(str1, 0) as int))/60, 2) as result
from str_str_orc
GROUP BY str2;

EXPLAIN
SELECT COALESCE(str1, 0) as result
from str_str_orc;

SELECT COALESCE(str1, 0) as result
from str_str_orc;

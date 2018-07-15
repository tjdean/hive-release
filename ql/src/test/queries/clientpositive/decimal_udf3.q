DROP TABLE IF EXISTS DECIMAL_UDF3;

CREATE TABLE DECIMAL_UDF3_TXT (key decimal(20,10), value int)
ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ' '
STORED AS TEXTFILE;

LOAD DATA LOCAL INPATH '../../data/files/kv7.txt' INTO TABLE DECIMAL_UDF3_TXT;

CREATE TABLE DECIMAL_UDF3 STORED AS ORC AS SELECT * FROM DECIMAL_UDF3_TXT;

select * from DECIMAL_UDF3_TXT;

explain
select lpad(key, 22, ' '), lpad(cast(key as string), 22, ' '), lpad(cast(key as char(22)), 22, ' '), lpad(cast(key as varchar(22)), 22, ' ') from DECIMAL_UDF3_TXT;

select lpad(key, 22, ' '), lpad(cast(key as string), 22, ' '), lpad(cast(key as char(22)), 22, ' '), lpad(cast(key as varchar(22)), 22, ' ') from DECIMAL_UDF3_TXT;

explain
select
  substring(concat('          ', key), (10 + length(key) - 22)+1, 22),
  substring(concat('          ', cast(key as string)), (10 + length(cast(key as string)) - 22)+1, 22),
  substring(concat('          ', cast(key as char(22))), (10 + length(cast(key as char(22))) - 22)+1, 22),
  substring(concat('          ', cast(key as varchar(22))), (10 + length(cast(key as varchar(22))) - 22)+1, 22)
from DECIMAL_UDF3;

select
  substring(concat('          ', key), (10 + length(key) - 22)+1, 22),
  substring(concat('          ', cast(key as string)), (10 + length(cast(key as string)) - 22)+1, 22),
  substring(concat('          ', cast(key as char(22))), (10 + length(cast(key as char(22))) - 22)+1, 22),
  substring(concat('          ', cast(key as varchar(22))), (10 + length(cast(key as varchar(22))) - 22)+1, 22)
from DECIMAL_UDF3;

set hive.vectorized.execution.enabled=true;

explain
select
  substring(concat('          ', key), (10 + length(key) - 22)+1, 22),
  substring(concat('          ', cast(key as string)), (10 + length(cast(key as string)) - 22)+1, 22),
  substring(concat('          ', cast(key as char(22))), (10 + length(cast(key as char(22))) - 22)+1, 22),
  substring(concat('          ', cast(key as varchar(22))), (10 + length(cast(key as varchar(22))) - 22)+1, 22)
from DECIMAL_UDF3;

select
  substring(concat('          ', key), (10 + length(key) - 22)+1, 22),
  substring(concat('          ', cast(key as string)), (10 + length(cast(key as string)) - 22)+1, 22),
  substring(concat('          ', cast(key as char(22))), (10 + length(cast(key as char(22))) - 22)+1, 22),
  substring(concat('          ', cast(key as varchar(22))), (10 + length(cast(key as varchar(22))) - 22)+1, 22)
from DECIMAL_UDF3;

DROP TABLE IF EXISTS DECIMAL_UDF3;

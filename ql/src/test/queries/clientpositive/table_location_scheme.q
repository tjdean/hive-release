dfs ${system:test.dfs.mkdir} hdfs:///tmp/t1;
dfs ${system:test.dfs.mkdir} hdfs:///tmp/t2;

DROP TABLE IF EXISTS T1;

-- table location has scheme
CREATE EXTERNAL TABLE T1 (key STRING, val STRING)
STORED AS TEXTFILE
LOCATION 'hdfs:///tmp/t1';

SELECT * FROM T1;

DROP TABLE IF EXISTS T2;

-- table location doesn't have scheme
CREATE TABLE T2 (key STRING, val STRING)
STORED AS TEXTFILE
LOCATION '/tmp/t2';

SELECT * FROM T2;

DROP TABLE T1;
DROP TABLE T2;

SET hive.enforce.sorting=true;
SET hive.enforce.bucketing=true;
SET hive.exec.dynamic.partition=true;
SET mapreduce.reduce.import.limit=-1;

SET hive.optimize.bucketmapjoin=true;
SET hive.optimize.bucketmapjoin.sortedmerge=true;
SET hive.auto.convert.join=true;
SET hive.auto.convert.sortmerge.join=true;

CREATE DATABASE IF NOT EXISTS tmp;
USE tmp;

CREATE  TABLE `test1` (
  `foo` bigint )
CLUSTERED BY (
  foo)
SORTED BY (
  foo ASC)
INTO 384 BUCKETS
stored as orc;

CREATE  TABLE `test2`(
  `foo` bigint )
CLUSTERED BY (
  foo)
SORTED BY (
  foo ASC)
INTO 384 BUCKETS
STORED AS ORC;

-- Initialize ONE table of the two tables with any data.
INSERT overwrite table test1 SELECT key FROM default.src LIMIT 100;

SELECT t1.foo, t2.foo
FROM test1 t1 INNER JOIN test2 t2 
ON (t1.foo = t2.foo);

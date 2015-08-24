set hive.auto.convert.join=true;
set hive.auto.convert.join.noconditionaltask=true;
set hive.auto.convert.join.noconditionaltask.size=10000;
set hive.auto.convert.sortmerge.join.bigtable.selection.policy = org.apache.hadoop.hive.ql.optimizer.TableSizeBasedBigTableSelectorForAutoSMJ;

drop table if exists srcbucket_mapjoin;
drop table if exists tab_part;
drop table if exists srcbucket_mapjoin;

CREATE TABLE srcbucket_mapjoin(key int, value string) partitioned by (ds decimal(5,2)) CLUSTERED BY (key) INTO 2 BUCKETS STORED AS TEXTFILE;
CREATE TABLE tab_part (key int, value string) PARTITIONED BY(ds decimal(5,2)) CLUSTERED BY (key) SORTED BY (key) INTO 4 BUCKETS STORED AS TEXTFILE;
CREATE TABLE srcbucket_mapjoin_part (key int, value string) partitioned by (ds decimal(5,2)) CLUSTERED BY (key) INTO 4 BUCKETS STORED AS TEXTFILE;

load data local inpath '../../data/files/srcbucket20.txt' INTO TABLE srcbucket_mapjoin partition(ds=100);
load data local inpath '../../data/files/srcbucket22.txt' INTO TABLE srcbucket_mapjoin partition(ds=100);

load data local inpath '../../data/files/srcbucket20.txt' INTO TABLE srcbucket_mapjoin_part partition(ds=100);
load data local inpath '../../data/files/srcbucket21.txt' INTO TABLE srcbucket_mapjoin_part partition(ds=100);
load data local inpath '../../data/files/srcbucket22.txt' INTO TABLE srcbucket_mapjoin_part partition(ds=100);
load data local inpath '../../data/files/srcbucket23.txt' INTO TABLE srcbucket_mapjoin_part partition(ds=100);

set hive.enforce.bucketing=true;
set hive.enforce.sorting = true;
set hive.optimize.bucketingsorting=false;
insert overwrite table tab_part partition (ds=100)
select key,value from srcbucket_mapjoin_part;

CREATE TABLE tab(key int, value string) PARTITIONED BY(ds STRING) CLUSTERED BY (key) SORTED BY (key) INTO 2 BUCKETS STORED AS TEXTFILE;
insert overwrite table tab partition (ds=100)
select key,value from srcbucket_mapjoin;

set hive.convert.join.bucket.mapjoin.tez = true;
set hive.auto.convert.sortmerge.join = true;

set hive.auto.convert.join.noconditionaltask.size=500;

set hive.vectorized.execution.enabled=false;

explain
select s1.key, s1.value, s3.ds from tab s1 join tab s3 on s1.key=s3.key order by s1.key limit 10;

select s1.key, s1.value, s3.ds from tab s1 join tab s3 on s1.key=s3.key order by s1.key limit 10;


set hive.vectorized.execution.enabled=true;

explain
select s1.key, s1.value, s3.ds from tab s1 join tab s3 on s1.key=s3.key order by s1.key limit 10;

select s1.key, s1.value, s3.ds from tab s1 join tab s3 on s1.key=s3.key order by s1.key limit 10;

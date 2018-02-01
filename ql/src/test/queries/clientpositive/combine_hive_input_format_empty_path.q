set hive.tez.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.cli.print.header=true;
set hive.mapred.mode=nonstrict;
set hive.fetch.task.conversion=none;

CREATE TABLE empty_path_table(insert_num int) PARTITIONED BY(part INT) location '${hiveconf:hive.metastore.warehouse.dir}/empty_path_table';

insert into table empty_path_table partition(part=1)
    values (1), (2), (3), (4);

dfs -ls ${hiveconf:hive.metastore.warehouse.dir}/empty_path_table;

select count(1) from empty_path_table where part in (2, 3) order by 1;
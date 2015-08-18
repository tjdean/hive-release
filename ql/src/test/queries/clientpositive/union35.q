set hive.cbo.enable=true;

select * from (
     select * from ( select 1 as id , 'foo' as str_1 from src limit 5) f
 union all
     select * from ( select 2 as id , 'bar' as str_2 from src limit 5) g
) e ;

set hive.cbo.enable=false;

select * from (
     select * from ( select 1 as id , 'foo' as str_1 from src limit 5) f
 union all
     select * from ( select 2 as id , 'bar' as str_2 from src limit 5) g
) e ;

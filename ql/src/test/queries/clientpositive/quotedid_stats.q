set hive.mapred.mode=nonstrict;

set hive.support.quoted.identifiers=column;


create table t(x string, y string);

insert into t values ('vmi', 'vmi');

describe formatted t;

analyze table t compute statistics for columns x;

describe formatted t;



-- escaped back ticks
create table t4(`x+1``` string, `y&y` string);

insert into t4 values ('vmi2', 'vmi2');

describe formatted t4;

analyze table t4 compute statistics for columns `x+1```;

describe formatted t4;

describe formatted t4 `x+1```;

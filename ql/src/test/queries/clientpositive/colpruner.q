select count(*) from (SELECT c1, c2 FROM (SELECT key as c1, CAST(NULL AS INT) AS c2 FROM src  UNION ALL SELECT key as c1, value as c2 FROM src) x)v1 WHERE v1.c2 = 0;

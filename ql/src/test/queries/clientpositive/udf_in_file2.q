SELECT * from src where in_file("303", "../../data/files/test2.dat") limit 1;

SELECT * from src where in_file(cast(null as string), "../../data/files/test2.dat") limit 1;

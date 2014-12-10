set hive.security.authorization.createtable.owner.grants=INSERT,SELECT,UPDATE,DELETE;

create table default_std_auth_table_creator_priv_test(i int);

-- Table owner (hive_test_user) should have ALL privileges
show grant on table default_std_auth_table_creator_priv_test;

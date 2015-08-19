CREATE TABLE kafka (contents STRING);
LOAD DATA LOCAL INPATH '../../data/files/text-en.txt' INTO TABLE kafka;
set mapred.reduce.tasks=1;
set hive.exec.reducers.max=1;

SELECT context_ngrams(sentences(lower(contents)), array(null), 100, 1000).estfrequency FROM kafka;
SELECT context_ngrams(sentences(lower(contents)), array("he",null), 100, 1000) FROM kafka;
SELECT context_ngrams(sentences(lower(contents)), array(null,"salesmen"), 100, 1000) FROM kafka;
SELECT context_ngrams(sentences(lower(contents)), array("what","i",null), 100, 1000) FROM kafka;
SELECT context_ngrams(sentences(lower(contents)), array(null,null), 100, 1000).estfrequency FROM kafka;

DROP TABLE kafka;

CREATE TABLE ngramtest (col1 INT, col2 STRING);
INSERT INTO TABLE ngramtest VALUES
(0, 'I am a boy'),
(0, 'I am a girl'),
(0, 'I am an apple'),
(1, 'I am a banana'),
(2, 'We are not animals'),
(0, null);
SELECT explode(context_ngrams(sentences(lower(t.col2)), array("i","am",null), 10, 1000)) as x FROM (SELECT col2 FROM ngramtest WHERE col1=0) t;
DROP TABLE ngramtest;
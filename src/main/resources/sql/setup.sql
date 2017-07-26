/*
    This is a collection of different SQL statement that are used
    for this application.

    This application requires a mysql db which holds the sql.dump generated
    by the old mathosphere programm from 2014. This dump included all
    formulas and queries from the NTCIR-11 test collection.
*/

-- Create a backup from the previous results
CREATE TABLE results_bkp AS SELECT * FROM results;
-- Create a new result table
CREATE TABLE results_new LIKE results;


-- only use the exact same result as the backup and takeover the votes
CREATE TABLE results_new_a (
  SELECT rst.queryNum, rst.queryFormulaId, rst.patternName,
    rst.cdMatch,
    rst.dataMatch,
    rst.matchDepth,
    rst.queryCoverage,
    rst.isFormulae,
    rst.pageID,
    bkp.vote
  FROM results bkp, results_new rst WHERE
	rst.queryNum = bkp.queryNum
	AND rst.queryFormulaId = bkp.queryFormulaId
	AND rst.patternName = bkp.patternName
	AND rst.pageID = bkp.pageID
);
/**
 Results of the query overview table. List all relevant
 summerazations of each query.

 Based on the filtered result table: results_new_a
 */
SELECT
  vt.id,
  vt0.v0, -- avg factor 1
  vt1.v1, -- avg factor 2
  vt2.v2, -- avg factor 3
  vt3.v3, -- avg factor 4
  vt4.v4, -- avg factor 5
  ft1.f1, -- # functions matched by query under factor 1
  ft2.f2, -- # functions matched by query under factor 2
  ft3.f3, -- # functions matched by query under factor 3
  ft4.f4, -- # functions matched by query under factor 4
  ft5.f5 -- # functions matched by query under factor 5
FROM
  (SELECT DISTINCT concat(LPAD(queryNum,2,0),queryFormulaID) id FROM results_new_a) vt
    LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, count(patternName) v0 FROM results_new_a WHERE vote = 0 GROUP BY 1) vt0 ON vt.id = vt0.id
    LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, count(patternName) v1 FROM results_new_a WHERE vote = 1 GROUP BY 1) vt1 ON vt.id = vt1.id
    LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, count(patternName) v2 FROM results_new_a WHERE vote = 2 GROUP BY 1) vt2 ON vt.id = vt2.id
    LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, count(patternName) v3 FROM results_new_a WHERE vote = 3 GROUP BY 1) vt3 ON vt.id = vt3.id
    LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, count(patternName) v4 FROM results_new_a WHERE vote = 4 GROUP BY 1) vt4 ON vt.id = vt4.id
    LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, sum(cdMatch) f1 FROM results_new_a GROUP BY 1) ft1 ON vt.id = ft1.id
	LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, sum(dataMatch) f2 FROM results_new_a GROUP BY 1) ft2 ON vt.id = ft2.id
	LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, count(matchDepth) f3 FROM results_new_a WHERE matchDepth > 0 GROUP BY 1) ft3 ON vt.id = ft3.id
	LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, ROUND(AVG(queryCoverage), 2) f4 FROM results_new_a GROUP BY 1) ft4 ON vt.id = ft4.id
	LEFT JOIN (SELECT concat(LPAD(queryNum,2,0),queryFormulaID) id, sum(isFormulae) f5 FROM results_new_a GROUP BY 1) ft5 ON vt.id = ft5.id
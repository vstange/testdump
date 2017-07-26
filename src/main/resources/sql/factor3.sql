-- Similarity factor: matchDepth
select
 depth "$d$",
 ROUND(avg(RP/(RP+FN)),2) as "$r$",
 ROUND(avg(RP/(RP+FP)),2) as "$p$",
 ROUND(avg(RN/(RN+FP)),2) as "$s$",
 sum(RP+FN) "$\\#$"
from (
 select
   depth, count(d) cnt, patternName,
   sum(F*R) RP, sum(neg(F)*R) FN,
   sum(neg(R)*F) FP, sum(neg(R)*neg(F)) RN
 from (
   Select
     depth,
     concat(LPAD(queryNum,2,0),queryFormulaID) patternName,
     min(matchDepth) d,
     min(vote) v,
     not ISNULL(min(matchDepth)) and min(matchDepth)<=dpt.depth as F,
     min(vote) > 0 as R
   From
     results_new_a,
     (select distinct matchDepth depth from results_new_a where matchDepth > 0 order by matchDepth) dpt
   where concat(queryNum,queryFormulaID) in
     (Select distinct concat(queryNum,queryFormulaId) pID from results_new_a where matchDepth > 0 group by queryNum,queryFormulaId)
   group by depth, pageID, queryNum, queryFormulaID
   order by queryNum, queryFormulaID, ISNULL(min(matchDepth)), d asc) t
 group by depth, patternName) rel
   ##where (FR+FI) >0
   ##group by ID
group by depth
order by depth
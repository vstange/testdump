-- Similarity factor: isFormulae
select
  v as "$v$",
  ROUND(avg(RP/(RP+FN)),2) as "$r$",
  ROUND(avg(RP/(RP+FP)),2) as "$p$",
  ROUND(avg(RN/(RN+FP)),2) as "$s$",
  sum(RP+FN) "$\\#$"
from (
  select  v, patternName,
    sum(F) P,
    sum(F*R)      RP, sum(F*neg(R))      FP,
    sum(neg(F)*R) FN, sum(neg(F)*neg(R)) RN
  from (
    SELECT
      vt.v,
      patternName,
      neg(max(isFormulae)) AS F,
      (min(rst.vote) >= v) AS R
    FROM results_new_a rst, (Select distinct vote v from results_new_a) vt
    GROUP BY pageID, patternName, vt.v
    ORDER BY queryNum, queryFormulaId, max(isFormulae)
    ) t
  group by v
) rel
group by v
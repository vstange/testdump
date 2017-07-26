-- Similarity factor: coverage
select  c "$c$",
  ROUND(avg(RP/(RP+FN)),2) as "$r$",
  ROUND(avg(RP/(RP+FP)),2) as "$p$",
  ROUND(avg(RN/(RN+FP)),2) as "$s$",
  sum(P) "$\\#$"
FROM (
  select c,  patternName,
    sum(F) P,
    sum(F*R)      RP, sum(F*neg(R))      FP,
    sum(neg(F)*R) FN, sum(neg(F)*neg(R)) RN
  from coverages
  group by c
) rel
group by c
order by c

-- TODO Coverage Table still unknown.
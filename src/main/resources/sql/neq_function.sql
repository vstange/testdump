-- drop previous function
DROP FUNCTION NEG;

-- create new function "neg": binary negative
CREATE FUNCTION NEG (N_IN DOUBLE)
 RETURNS DOUBLE
 BEGIN
  DECLARE N_OUT DOUBLE;
  IF N_IN = 0 THEN RETURN 1;
  ELSE RETURN 0;
  END IF;
 END;
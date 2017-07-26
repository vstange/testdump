# MathML Similarity Test

Test project to recreate and compare the similarity measurement from the mathosphere
project.

## Requierements ##

This application requires a **mysql db** which holds the sql.dump generated
by the old mathosphere program from 2014. This dump included all 
formulas and queries from the NTCIR-11 test collection.

The collection and dump can be found here:
 https://github.com/TU-Berlin/mathosphere-history/releases 
 
## Configuration ##

  1. Change the database credentials to your local db.
    Configuration file: `application.yaml`.
  2. Select a table to write comparison result into. 
    Class file: `TestRunner.java` holds the name of the result table
    in the constant `RESULT_TABLE_NAME`.

## Usage ##

Start the test via the `ApplicationStart.java` class.

### SQLs ###

Changes to the database structure must be manually applied via SQL
statements. For example, this includes the creation of a new result table.

  * Statements can be found at `src/main/resources`
    * Preparation statements in `setup.sql`.
    * Similarity factors in `factor1.sql` to `factor5.sql`
    * Necessary binary negation funtion in `neq_function.sql`
    * Statistic statement for all queries in `query_table.sql` 


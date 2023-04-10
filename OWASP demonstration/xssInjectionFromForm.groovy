/*
 * Author: 
 * DESC
 * Date
*/

// BWS ID Example : 438949

// INITIALISATION GENERAL
def excelTemplate = asCSNode(5418355 as long)    //Fichier de données
def csPathSeparator = ":"
def languageSeparator = ";"
def itemNumberSeparator = ";"

for (line in xlsx.loadSpreadsheet(excelTemplate).getWorksheet(0).readRows(1)) {

    sqlRequest = """SELECT dt.DataID, dt.Name 
    FROM DTree dt INNER JOIN DTreeAncestors dta ON dt.DataID = dta.DataID
    WHERE dta.AncestorID = ${line.value.A}
    AND dt.Subtype = 144        
    """
    out << sqlRequest 
    out << "<br><br>"

    result = sql.runSQLFast(sqlRequest, true, false, 10).rows

    out << result 
    out << "<br><br>"

    result.each{
        out << it
        out << "<br>"
    }
}
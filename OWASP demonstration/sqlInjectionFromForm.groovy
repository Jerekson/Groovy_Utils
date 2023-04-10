/*
 * Author: 
 * DESC
 * Date
*/

// BWS ID Example : 438949

def myParam = params.myParam

// HTML FORM
out << """
<br>
<form action="" method="post">

    <p>Rechercher 10 documents d'un BWS -> Exemple 438949</p>

    <input type="hidden" name="func" value="ll"/>
    <input type="hidden" name="objId" value="${self.ID}"/>
    <input type="hidden" name="objAction" value="Execute"/>

    <label for="myParam">entez l'ID d'un BWS : </label>
    <input type="text" name="myParam" id="myParam" required>

    <input type="submit" value="Rechercher">
</form>
<br><br>
"""

if(myParam){
    
    sqlRequest = """SELECT dt.DataID, dt.Name 
    FROM DTree dt INNER JOIN DTreeAncestors dta ON dt.DataID = dta.DataID
    WHERE dta.AncestorID = ${myParam}
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
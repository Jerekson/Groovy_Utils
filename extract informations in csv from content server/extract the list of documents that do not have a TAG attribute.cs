try{
    def noeudRacineID = docman.getNodeByPath(docman.getEnterpriseWS(), "RH:Collaborateurs").ID
    def batchSize = 1
    def index = 2
    def sheet = 0
    
    // Xls Creation
    def myDoc = xlsx.createSpreadsheet("Attribut TAG vide")
    myDoc.A1 = "Nom du document"
    myDoc.B1 = "ID du document"
    myDoc.C1 = "Propriétaire du document"
    
    // First condition
    String sqlRequest = """
SELECT dt.OwnerID, dt.DataID, dt.Name
    FROM Dtree dt INNER JOIN DTreeAncestors dta ON dt.DataID = dta.DataID LEFT JOIN LLAttrData ll ON ll.ID = dt.DataID
    WHERE dta.AncestorID = %1
    AND dt.SubType = 144
    AND ll.DefID IS NULL
"""
    result = sql.runSQL(sqlRequest, false, false, batchSize, *[noeudRacineID]).rows
    if(result){
        result.each(){ row ->
            // add row
            myDoc.newRow(sheet, index, [row.Name, 
                                        row.DataID,
                                        asCSNode(row.OwnerID as long).name
                                       ])
            index++
                }        
    }
    
    
    // Second condition
    def contentServerCategories = docman.getNode(2006 as long)
    def pathTocategory = "Collaborateur:Document Collaborateur"
    def categoryID = docman.getCategory(contentServerCategories, pathTocategory)
    def attributeID = categoryID.getAttributeID("TAG") as long
    sqlRequest = """
SELECT ll.ID, dt.Name, dt.OwnerID, dt.DataID
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID AND ll.VerNum = dt.VersionNum
WHERE DefID = ${categoryID.ID}
AND AttrID = ${attributeID}
AND dt.SubType = 144
AND ValStr = ''
"""
    result = sql.runSQL(sqlRequest).rows
    if(result){
        result.each(){ row ->
            // add row
            myDoc.newRow(sheet, index, [row.Name, 
                                        row.DataID,
                                        asCSNode(row.OwnerID).name
                                       ])
            index++
                }        
    }
    
    // Export
    return myDoc.save()
}catch(e){
    out << "error : ${e.message}"
}


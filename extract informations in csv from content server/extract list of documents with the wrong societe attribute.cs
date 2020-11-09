try{
    def contentServerCategories = docman.getNode(2006 as long)
    def pathTocategory = "Collaborateur:Informations Collaborateur"
    def categoryID = docman.getCategory(contentServerCategories, pathTocategory)
    def attributeSocietyID = categoryID.getAttributeID("Société") as long
    def attributeAgence = categoryID.getAttributeID("Agence") as long
    def attributeAgenceID = categoryID.getAttributeID("Agence ID") as long


        def myDoc = xlsx.createSpreadsheet("attribut Société est mal renseigné")
        File file = docman.getNodeByNickname("society_list").content.content // read properties. all society are in this document. 
        def listSociety = file.readLines() // read all lines.
                 

                String sqlRequest = """
SELECT DISTINCT(ll.ID), dt.Name, ll.ValStr
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID
WHERE DefID = ${categoryID.ID}
AND AttrID = ${attributeSocietyID} 
AND dt.SubType = 144

""" 
    listSociety.each(){
        newString = it.replace("'","&#39")
        sqlRequest += " AND ll.ValStr != '${newString}' "
    }

    result = sql.runSQL(sqlRequest).rows

    // xlsx Creation
    myDoc.A1 = "ID du document"
    myDoc.B1 = "Nom du document"
    myDoc.C1 = "Agence"
    myDoc.D1 = "ID de l'agence"
    myDoc.E1 = "Nom de la Société"


    // user
    def index = 2
    def sheet = 0 

    result.each(){ row ->
        sqlRequest = """
    SELECT AttrID, ValStr
    FROM LLAttrData 
    WHERE ID =  ${row.ID}
    AND DefID = ${categoryID.ID}
    AND (AttrID = ${attributeAgence} OR AttrID = ${attributeAgenceID})
    """
        newResult = sql.runSQL(sqlRequest).rows

        newResult.each{
            if(it.AttrID == attributeAgence){agenceName = it.ValStr}
            if(it.AttrID == attributeAgenceID){agenceID = it.ValStr}
        }

        myDoc.newRow(sheet, index, [row.ID, 
                                    row.Name, 
                                    agenceName,
                                    agenceID,
                                    row.ValStr])
        index++
            }

    // Save
    return myDoc.save()

}catch(e){
    out << "error : ${e.message}"
}





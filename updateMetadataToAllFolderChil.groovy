/* * * * *
Les choses à modifier manuellement. 
L'id du dossier racine : bwiD
la valeur de la variable pathTocategory
* * * * * 
Actuellement, ce script ne fonctionne que pour une catégorie à la fois. 
* * * * */


// Entrer ici l'id du Business Workspace
bwID = 756398 as long;
bwNode =  docman.getNode("-${bwID}" as long)

Map mapStr = [:]
Map mapDate = [:]

// Récupération de la catégorie 
def contentServerCategories = docman.getNode(2006 as long)
def pathTocategory = "Collaborateur:Informations Collaborateur"
def category = docman.getCategory(contentServerCategories, pathTocategory) 
def attributes = category.getAttributes()

// Récupération de toutes les infos de la catégorie pour le BW concerné 
String sqlRequestAttribute = """
SELECT ll.ValStr, ll.ValDate, ll.AttrType, ll.AttrID 
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID
WHERE DefID = ${category.ID}
AND dt.DataID = ${bwID}
AND ll.AttrID != 1
"""
resultAttribute = sql.runSQL(sqlRequestAttribute).rows

retrieveAttrName = { AttributeID ->
    name = ""
    attributes.each{
        if(it.key == AttributeID){
            name = it.value
        }
    }
    return name
}

resultAttribute.each{
    List maliste = []
    if(it.AttrType != -7){
        mapStr["${it.AttrID}"] = [retrieveAttrName(it.AttrID), it.ValStr] // attribute name / value
    }else{
        mapStr["${it.AttrID}"] = [retrieveAttrName(it.AttrID), it.ValDate] // attribute name / value
    }
}

// Récupérer les IDs de tous les enfants dossier de ce BW 
String sqlRequest = """
SELECT dt.DataID
FROM Dtree dt INNER JOIN DTreeAncestors dta ON dt.DataID = dta.DataID
WHERE dta.AncestorID = ${bwID}
AND dt.SubType = 0
"""
result = sql.runSQL(sqlRequest).rows

// Appliquer le changement des métadonnées pour chaqu'un des noeuds récupéré 
result.each{
    try{
        targetNode = docman.getNode(it.DataID as long)
        mapStr.each{
            docman.setMetadataAttribute(targetNode, category.name, true, it.value[0] as String, it.value[1] as String)
        }
        docman.updateNode(targetNode)
    }catch(e){
        log.debug("error ${e}")
        out << "error ${e}"
    }
}
//docman.setMetadataAttribute(CSNode node, String category, boolean commit, String attributeName, Object[] attributeValues)

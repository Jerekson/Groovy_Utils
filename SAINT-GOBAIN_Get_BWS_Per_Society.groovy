/*
* Script Name: Liste des BWS d'une société
* @@@Author: AEROW KHA
* Date: 27/04/2021
* Description: List and details of BWS
*/


import java.util.Date

def contentServerCategories = docman.getNode(2006 as long);
def pathTocategory = "Collaborateur:Informations Collaborateur";
def category = docman.getCategory(contentServerCategories, pathTocategory);

def attributeSGID = category.getAttributeID("SGID") as long;
def attributePrenom = category.getAttributeID("Prenom") as long;
def attributeNom = category.getAttributeID("Nom") as long;
def attributeDentree = category.getAttributeID("Date d'entrée") as long;
def attributeDsortie = category.getAttributeID("Date de sortie") as long;

def societyCode = params.societyId

if(societyCode){
    out << """
    Bonjour ${users.CURRENTUSER().name} la recherche actuelle concerne la société ${societyCode}<br>
    """
}

out << """
<br>
<form action="" method="get" >
    <input type="hidden" name="func" value="ll"/>
    <input type="hidden" name="objId" value="${self.ID}"/>
    <input type="hidden" name="objAction" value="Execute"/>

    <p>
    Il est possible de faire la recherche sur : <br>
     - Une société<br>
     - Plusieurs sociétés en les séparant uniquement par un point virgule ' ; '<br>
     - Toutes les sociétés en entrant uniquement ' * '<br>
    </p>
    <label for="society">Entrer le code d'une société : </label>
    <input type="text" name="societyId" id="societyId" required>

    <input type="submit" value="Rechercher">
</form>
<br><br>
"""

// Xls Creation
def myDoc = xlsx.createSpreadsheet("Node Information")
myDoc.A1 = "SGID"
myDoc.B1 = "Prénom"
myDoc.C1 = "Nom"
myDoc.D1 = "Date d'entrée"
myDoc.E1 = "Date de sortie"

// initialize rows
def index = 3 // row number
def sheet = 0 // sheet number

def isInt = { myInt ->
    try{        
        if(myInt.matches("[0-9]+") && myInt.length() > 2) {
            return true;
        }else{
            return false
        }  
    }catch(e){
        out << e
    }
}

// Fonction appelée afin de restituer les valeurs d'attributs d'un noeud. 
def actualAttribute = { categoryID, dataID ->
    String sqlRequest = """
SELECT ll.ValStr, ll.AttrID, ll.ValDate
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID
WHERE DefID = ${categoryID}
AND dt.DataID = ${dataID}
""" 
    map = [:]

    sql.runSQL(sqlRequest).rows.each{
        switch(it.AttrID){
            case(attributeSGID):
            map.put("SGID",it.ValStr)
            break;
            case(attributePrenom):
            map.put("Prénom",it.ValStr)
            break;
            case(attributeNom):
            map.put("Nom",it.ValStr)
            break;
            case(attributeDentree):
            map.put("Date d entrée",it.ValDate?.format("dd-MM-yyyy"))
            break;
            case(attributeDsortie):
            map.put("Date de sortie",it.ValDate?.format("dd-MM-yyyy"))
            break;
        } 
    } 

    // add rows
    myDoc.newRow(sheet, index, [
        "${map['SGID']}",
        "${map['Prénom']}",
        "${map['Nom']}",
        "${map['Date d entrée']}",
        "${map['Date de sortie']}"
    ])

    //return map
}

if(societyCode == "*"){
    def collabFolderID = docman.getNodeByPath(asCSNode(2000), "RH:Collaborateurs").ID;

    String sqlRequest = """
SELECT dt.DataID
FROM DTree dt INNER JOIN DTreeAncestors dta ON dt.DataID = dta.DataID
WHERE dta.AncestorID = ${collabFolderID}
AND dt.SubType = 848
"""

    result = sql.runSQL(sqlRequest).rows

    result.each{ row ->
        actualAttribute(category.ID, row.DataID)
    }  

    // Save
    return myDoc.save()

}else if (societyCode){
    societyCodeSplited = societyCode.replace(" ","").split(";")
    societyCodeSize = societyCodeSplited.size()
    myBoolean = true

    out << "liste des sociétés saisies : <br>"
    societyCodeSplited.each{
        out << " - ${it}<br>"
        if(isInt(it) == false){
            myBoolean = false

        }
    }

    if(!myBoolean){
        out << "<br><h2>Il y a une erreur dans la saisie des sociétés</h2>"
    }else{
        societyCodeSplited.each{
            String sqlRequest = """
SELECT ID
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID 
WHERE DefID = 45259 
AND ll.AttrID = 17 
AND dt.SubType = 848
AND ll.ValStr = (
SELECT DISTINCT Name
FROM SGDBF_COMPANIES
WHERE StructureID = '${it}'
)
"""

            result = sql.runSQL(sqlRequest, false, false, 2).rows

            result.each{ row ->
                actualAttribute(category.ID, row.ID)
            }  
        }
        // Save
        return myDoc.save()
    }
}



/*
if(societyCode){

    String sqlRequest = """
SELECT ID
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID 
WHERE DefID = 45259 
AND ll.AttrID = 17 
AND dt.SubType = 848
AND ll.ValStr = (
SELECT DISTINCT Name
FROM SGDBF_COMPANIES
WHERE StructureID = '${societyCode}'
)
"""

    result = sql.runSQL(sqlRequest, false, false, 2).rows
    data = []

    result.each{
        data.add(actualAttribute(category.ID, it.ID))
    }  

    // Save
    return myDoc.save()

}
*/























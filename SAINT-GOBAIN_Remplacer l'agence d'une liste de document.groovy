/* Koceila HADDOUCHE - 01/03/21
 * Ce script récupère dans un xls une liste comprenant : un noeud, un attribut à remplacer, le nouvel attribut
 * L'objectif de remplacer la valeur de l'attribut "Agence" de tous les noeuds. 
 * 
*/

// INITIALISATION GENERALE
def excelTemplate = asCSNode(nickname:"DIGIDOC_SOCIETE_MAL_TAGUES");    //Fichier de données

def contentServerCategories = docman.getNode(2006 as long);
def pathTocategory = "Collaborateur:Informations Collaborateur";
def category = docman.getCategory(contentServerCategories, pathTocategory);

def attributeAgence = category.getAttributeID("Agence") as long; 
def attributeAgenceID = category.getAttributeID("Agence ID") as long;
def attributeRegion = category.getAttributeID("Region") as long;
def attributSecteur = category.getAttributeID("Secteur") as long;
def attributeSite = category.getAttributeID("Site") as long;
def attributeSociete = category.getAttributeID("Société") as long;
def attributeEtablissement = category.getAttributeID("Etablissement") as long;

out << "action effectuée par ${users.current.name}"
out << "<br><br>"


def requestSqlStructure = { structureID, type ->
    return sql.runSQL("""
    SELECT Name, ParentID 
    FROM SGDBF_STRUCTURES
    WHERE Type = '${type}'
    AND StructureID = '${structureID}'
    """).rows[0]
}

def actualAttribute = { categoryID, dataID ->
    String sqlRequest = """
SELECT ll.ValStr, ll.AttrID
FROM LLAttrData ll INNER JOIN DTree dt ON ll.ID = dt.DataID
WHERE DefID = ${categoryID}
AND dt.DataID = ${dataID}
""" 
    sql.runSQL(sqlRequest).rows.each{
        switch(it.AttrID){
            case(attributeAgence):
            out << "Agence : ${it.ValStr}"
            out << "<br>"
            break;
            case(attributeAgenceID):
            out << "Agence ID : ${it.ValStr}"
            out << "<br>"
            break;
            case(attributeRegion):
            out << "Region : ${it.ValStr}"
            out << "<br>"
            break;
            case(attributSecteur):
            out << "Secteur : ${it.ValStr}"
            out << "<br>"
            break;
            case(attributeSite):
            out << "Site : ${it.ValStr}"
            out << "<br>"
            break;
            case(attributeSociete):
            out << "Société : ${it.ValStr}"
            out << "<br>"
            break;
            case(attributeEtablissement):
            out << "Etablissement : ${it.ValStr}"
            out << "<br>"
            break;
        } 
    }   
    out << "<br>"
}

try{ 
    for (line in xlsx.loadSpreadsheet(excelTemplate).getWorksheet(0).readRows(2)) { // Pour chaque ligne du fichier Excel 
        def docID = line.value.A // Document ID
        def new_agenceID = line.value.F // new agence 9000
        
        out << "<h6>document ID = ${docID}</h6>"
        // par Document ID, afficher ses attributs avant modification (Agence - Agence ID - Region - Secteur - Site - Société - Etablissement (Si une seule valeur est disponible))
        actualAttribute(category.ID, docID)


        // Remplacer l'agence en fonction de l'agence ID récupéré dans l'Excel.
        String new_agence
        String new_region
        String new_secteur
        String new_site
        String new_societe 
        String new_etablissement 
        def tmp_list = []

        // // récupérer les autres attributs correspondant à l'agence ID

        result = sql.runSQL("""SELECT * FROM SGDBF_COMPANIES WHERE ChildID = '${new_agenceID}'""").rows.each{ // Récupère la société et l'établissement
            switch(it.Type){
                case("CO"):
                new_societe = it.Name
                break;
                case("ET"):
                tmp_list.add(it.Name)
            }
        }
        if(tmp_list.size() == 1){
            new_etablissement = tmp_list[0]
        }
        out << "<br>"

        // Récupéré tous les autres attributs (requête dans la fonction requestSqlStructure)
        result = requestSqlStructure(new_agenceID,"AG")
        new_agence = result.Name 

        result = requestSqlStructure(result.ParentID,"SI")
        new_site = result.Name

        result = requestSqlStructure(result.ParentID,"SE")
        new_secteur = result.Name

        result = requestSqlStructure(result.ParentID,"RE")
        new_region = result.Name

        // // Appliquer les nouvelles valeurs aux attributs (Agence - Agence ID - Region - Secteur - Site - Société - Etablissement (Si une seule valeur est disponible))
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Agence", new_agence)
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Agence ID", new_agenceID)
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Region", new_region)
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Secteur", new_secteur)
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Site", new_site)
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Société", new_societe)
        docman.setMetadataAttribute(asCSNode(docID as long), category.name, true, "Etablissement", new_etablissement)

        docman.updateNode(asCSNode(docID as long))
        
        // après modification 
        actualAttribute(category.ID, docID)
    }
}catch(e){
    out << e
}



//docman.setMetadataAttribute(CSNode node, String category, boolean commit, String attributeName, Object[] attributeValues)



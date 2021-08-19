/* Koceila HADDOUCHE - 07/06/21
 * 
 *  
 * 
*/

// Constant
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

def collabFolderID = docman.getNodeByPath(asCSNode(2000), "RH:Collaborateurs");

try{
    // retrieve all attribute for all doc that concerned
    String listDocumentRequest = """
SELECT 
dt.DataID as dataID,  
llreg.ValStr  as ac_region, 
llsec.ValStr as ac_secteur,  
llAgID.ValStr as ac_agenceID, 
llSi.ValStr as ac_site,  
llSo.ValStr as ac_societe, 
llAg.ValStr as ac_agence,
llEt.ValStr as ac_etablissement,
strAg.StructureID as db_agenceID,
strAg.Name as db_agence,
strSi.Name as db_site,
strSe.Name as db_secteur,
strRe.Name as db_region,
comCo.Name as db_societe
FROM DTree dt INNER JOIN DTreeAncestors dta ON dt.DataID = dta.DataID 
    -- Recuperer les valeurs de chacun des attributs sans avoir toutes les lignes a chaque fois
INNER JOIN LLAttrData llReg ON llReg.ID = dt.DataID 
    AND llReg.VerNum = dt.VersionNum 
    AND llReg.DefID = ${category.ID} AND llReg.attrID = ${attributeRegion} 

INNER JOIN LLAttrData llSec ON llSec.ID = dt.DataID 
    AND llSec.VerNum = dt.VersionNum 
    AND llSec.DefID = ${category.ID} AND llsec.attrID = ${attributSecteur} 

INNER JOIN LLAttrData llAgID ON llAgID.ID = dt.DataID 
    AND llAgID.VerNum = dt.VersionNum 
    AND llAgID.DefID = ${category.ID} AND llAgID.attrID = ${attributeAgenceID} 

INNER JOIN LLAttrData llSi ON llSi.ID = dt.DataID 
    AND llSi.VerNum = dt.VersionNum 
    AND llSi.DefID = ${category.ID} AND llSi.attrID = ${attributeSite} 

INNER JOIN LLAttrData llSo ON llSo.ID = dt.DataID 
    AND llSo.VerNum = dt.VersionNum 
    AND llSo.DefID = ${category.ID} AND llSo.attrID = ${attributeSociete} 

INNER JOIN LLAttrData llAg ON llAg.ID = dt.DataID 
    AND llAg.VerNum = dt.VersionNum 
    AND llAg.DefID = ${category.ID} AND llAg.attrID = ${attributeAgence}

INNER JOIN LLAttrData llEt ON llEt.ID = dt.DataID 
    AND llEt.VerNum = dt.VersionNum 
    AND llEt.DefID = ${category.ID} AND llEt.attrID = ${attributeEtablissement}

    -- Recuperer tous les sous-elements d une agence
INNER JOIN SGDBF_STRUCTURES strAg ON llAg.ValStr = strAg.Name
INNER JOIN SGDBF_STRUCTURES strSi ON strAg.ParentID = strSi.StructureID AND strSi.Type = 'SI'
INNER JOIN SGDBF_STRUCTURES strSe ON strSi.ParentID = strSe.StructureID AND strSe.Type = 'SE'
INNER JOIN SGDBF_STRUCTURES strRe ON strSe.ParentID = strRe.StructureID AND strRe.Type = 'RE'
INNER JOIN SGDBF_COMPANIES comCo ON strAg.StructureID = comCo.ChildID AND comCo.Type = 'CO'

WHERE dta.AncestorID = ${collabFolderID.ID}
AND dt.SubType = 144
AND dt.Name LIKE 'Test%'
AND (
    (ISNULL(llreg.ValStr, '') <> strRe.Name) 
    OR 
    (ISNULL(llsec.ValStr, '') <> strSe.Name) 
    OR 
    (ISNULL(llAgID.ValStr, '') <> strAg.StructureID) 
    OR 
    (ISNULL(llSi.ValStr, '') <> strSi.Name) 
    OR 
    (ISNULL(llSo.ValStr, '') <> comCo.Name) 
    OR
    (llEt.ValStr IS NULL)
)
"""

    resultListDocument = sql.runSQL(listDocumentRequest, false, false, 1).rows



    resultListDocument.each{ row -> // pour chacun des résultats. remplacer les valeurs d'attributs sauf l'établissement
        node = asCSNode(row.dataID as long);

        // Si pas d'établissement, vérifier s'il y a une ou plusieurs valeurs. 

        if(!row.ac_etablissement){
            String new_etablissement;
            tmp_list = [];
            result = sql.runSQL("""SELECT * FROM SGDBF_COMPANIES WHERE ChildID = '${row.db_agenceID}' AND Type='ET'""").rows.each{ // Récupère la société et l'établissement
                tmp_list.add(it.Name)
            }

            if(tmp_list.size() == 1){
                new_etablissement = tmp_list[0]
            }else{
                out << """
            Le document ${row.dataID} n'a pas de valeur pour l'attribut 'Etablissement'. Cause : Plusieurs choix d'établissements
            """
            }
            docman.setMetadataAttribute(node, category.name, true, "Etablissement", new_etablissement)
        }

        // // Appliquer les nouvelles valeurs aux attributs (Agence - Agence ID - Region - Secteur - Site - Société - Etablissement (Si une seule valeur est disponible))
        docman.setMetadataAttribute(node, category.name, true, "Agence ID", row.db_agenceID)
        docman.setMetadataAttribute(node, category.name, true, "Region", row.db_region)
        docman.setMetadataAttribute(node, category.name, true, "Secteur", row.db_secteur)
        docman.setMetadataAttribute(node, category.name, true, "Site", row.db_site)
        docman.setMetadataAttribute(node, category.name, true, "Société", row.db_societe)

        docman.updateNode(node)
    }




    /*
    //// OUTPUT ON TABLE
    cols = [
        "dataID",
        "ac_region",
        "ac_secteur",
        "ac_agenceID",
        "ac_site",
        "ac_societe",
        "ac_agence",
        "ac_etablissement",
        "db_agenceID",
        "db_agence",
        "db_site",
        "db_secteur",
        "db_region",
        "db_societe"

    ]
    out << template.evaluateTemplate("""
#csresource(['bootstrap','bootstrap-css'])
<table class="table table-bordered table-hover">
<thead>
#foreach(\$col in \$cols)
    <th>\$col</th>
#end
</thead>
#foreach(\$row in \$resultListDocument)
    <tr>
        #foreach(\$col in \$cols)
            <td>\$row[\$col]</td>
        #end
    </tr>
#end
</table>
""")
*/
}catch(e){
    out << e
}



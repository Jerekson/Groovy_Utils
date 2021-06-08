/* Koceila HADDOUCHE - 01/03/21
 * Ce script récupère dans un xls une liste comprenant : un noeud, un attribut à remplacer, le nouvel attribut
 * L'objectif de remplacer la valeur de l'attribut "Agence" de tous les noeuds. 
 *
*/

// INITIALISATION GENERAL
def excelTemplate = asCSNode(nickname:"DIGIDOC_SOCIETE_MAL_TAGUES")    //Fichier de données
def csPathSeparator = ":"
def languageSeparator = ";"
def itemNumberSeparator = ";"


for (line in xlsx.loadSpreadsheet(excelTemplate).getWorksheet(0).readRows(1)) {
    
    def docID = line.value.A // Document ID
    def agence = line.value.F // new agence
    
    out << docID
    out << "<br>"

    // Retrieve which society is for the agence ID agence
    result = sql.runSQL("""SELECT * 
    FROM SGDBF_COMPANIES
    """).rows



    
    
}


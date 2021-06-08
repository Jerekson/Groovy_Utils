// INITIALISATION GENERAL
def excelTemplate = asCSNode(nickname:"DIGIDOC_SOCIETE_MAL_TAGUES")    //Fichier de données
def csPathSeparator = ":"
def languageSeparator = ";"
def itemNumberSeparator = ";"


for (line in xlsx.loadSpreadsheet(excelTemplate).getWorksheet(0).readRows(1)) {
    out << line
    out << "<br>"
    out << line.value.A
    out << "<br><br>"
}
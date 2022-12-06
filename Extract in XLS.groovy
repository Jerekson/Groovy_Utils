
// Date
import java.text.SimpleDateFormat
Date today = new Date();
formater = new SimpleDateFormat("'le' dd/MM/yyyy 'à' HH'h'mm");
newDate = formater.format(today)

// Xls Creation
def myDoc = xlsx.createSpreadsheet("Node Information")

myDoc.A1 = "Namé"
myDoc.B1 = "ID"
myDoc.C1 = "Type"
myDoc.D1 = "SubType"
myDoc.E1 = "Modified Date"
myDoc.F1 = "Created Date"
myDoc.G1 = "Created By"
myDoc.H1 = "Description"

// add rows
def index = 3 // row number
def sheet = 0 // sheet number

// add rows
myDoc.newRow(sheet, index, ["it.name", 
                    "it.ID", 
                    "it.type",
                    "it.subtype",
                    "test",
                    "test",
                    "it.createdBy?.displayName",
                    "it.comment"])

// Save
return myDoc.save() // to dl the Excel
return myDoc.save(parentRapportNode, "NAME ${newDate}") // return in the



/* HELP  
addRows(int sheet, long position, List<List<Object>> values)


it.modifyDate.format('dd/MM/yyyy'),
it.createDate.format('dd/MM/yyyy'), 
it.createdBy?.displayName,

*/
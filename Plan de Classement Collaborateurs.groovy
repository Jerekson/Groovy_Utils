CheckDocument = {CSNode node, myDoc, index -> 
    node.getChildren().each{it -> 
        if (it.getIsContainer())
        {
            CheckDocument(it, myDoc, index)
        }
        myDoc.newRow(0, index, [it.name, 
                                it.ID, 
                                it.type,
                                it.subtype,
                                it.modifyDate.format('dd/MM/yyyy'),
                                it.createDate.format('dd/MM/yyyy'),
                                it.createdBy?.displayName,
                                it.comment])
    }
}

 


// List Folder Content in Excel Spreadsheet

 

def myDoc = xlsx.createSpreadsheet("Node Information")

 

def nodes = docman.getNode(256321)

 

myDoc.A1 = "Name"
myDoc.B1 = "ID"
myDoc.C1 = "Type"
myDoc.D1 = "SubType"
myDoc.E1 = "Modified Date"
myDoc.F1 = "Created Date"
myDoc.G1 = "Created By"
myDoc.H1 = "Description"

 


nodes?.eachWithIndex{ node, index ->
    
    CheckDocument(node, myDoc, index)
    
}

return myDoc.save()




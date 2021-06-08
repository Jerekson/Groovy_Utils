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
                map.put("prenom",it.ValStr)
            break;
            case(attributeNom):
                map.put("nom",it.ValStr)
            break;
            case(attributeDentree):
                map.put("entree",it.ValDate?.format("dd-MM-yyyy"))
            break;
            case(attributeDsortie):
                map.put("sortie",it.ValDate?.format("dd-MM-yyyy"))
            break;
        } 
    } 
    
    return map
}



String sqlRequest = """
SELECT *
FROM Dtree
"""

result = sql.runSQL(sqlRequest, true, false, 20).rows
data = []
cols = [
    "SGID",
    "prenom",
    "nom",
    "entree",
    "sortie"
]

result.each{
    data.add(actualAttribute(category.ID, it.ID))
}  

    out << template.evaluateTemplate("""
#csresource(['bootstrap','bootstrap-css'])
<table class="table table-bordered table-hover">
<thead>
#foreach(\$col in \$cols)
    <th>\$col</th>
#end
</thead>
#foreach(\$row in \$data)
    <tr>
        #foreach(\$col in \$cols)
            <td>\$row[\$col]</td>
        #end
    </tr>
#end
</table>
""")


}
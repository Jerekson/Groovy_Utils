/*
* KUAF Contient tous les utilisateurs ET d'autre data, comment faire la différence entre ces données. 
* 
* Simuler une élévation de privilège dans un des serveurs et voir ce que la table DAuditNew contient dans la colonne AuditStr (Sur quelle machine ? dev ? re7 ? Perso ?)
* AUCUN des utilisateurs ne doit avoir les droits d'admin. (Contrôle toutes les heures + alerte si ça arrive ?)
*/

// retrieve the log file
logDocument = new File('D:\\Log_System','log_privilege.txt')
Newline=System.getProperty("line.separator") // allows the program to add new line

adminPrivilegeNumber = 2431 // or >2431

// Check if a users has new privileges 
myRequest = """
    SELECT 
	    Value1 as PreviousPrivileges,
	    Value2 as NewPrivileges,
	    PerformerID,
	    UserID,
	    AuditDate as Date
    FROM 
	    DAuditNew
    WHERE
	    ValueKey = 'UserPrivs'
	    ORDER BY AuditDate DESC;
"""

result = sql.runSQL(myRequest)

result?.rows?.each { row ->
    out << "<br><br>"
    out << """
        ${row?.Date.format("yyyy-MM-dd HH:mm:ss,SSS")} : 
        Il est passé des droits ${row?.PreviousPrivileges}
        a ${row?.NewPrivileges},
        
        Donnée par l'utilisateur ${row?.PerformerID}
        à l'utilisateur ${row?.UserID}
        
        ${Newline}
    """
    //logDocument.append()
}




/* 
Go voir les droits en version smart view et voir où est ce que c'est traité, et comment c'est traité. 
http://sgdbf.aerowteam.com/OTCS/cs.exe/app/nodes/654018/permissions/explorer

*/

/*
* Author : Koceila HADDOUCHE
* Date : 17/02/2020
* Description : retrieve any privileges escalation for all users
* This script will be run at 1:00 AM every day 
*/

// retrieve the log file
logDocument = new File('D:\\Log_System','log_privilege.txt')
newline=System.getProperty("line.separator") // allows the program to add new line

// retrieve today's date and last day's date to format them so that they conform to the database
Date thisDate = new Date().clearTime()
pDate = thisDate.previous()

tDate = thisDate.format("MM/dd/yyyy")
pDate = pDate.format('MM/dd/yyyy')

// give me the right for a specific user
whichRights = { priv -> 
    myrights = []
    priv -= 14 // Base
    while(priv > 0){
        if(priv >= 2048){
            myrights.add("Public Access enabled")
			priv -= 2048
        }else if(priv >= 256){
            myrights.add("System administration rights")
			priv -= 256
        }else if(priv >= 64){
            myrights.add("Can create/modify groups")
			priv -= 64
        }else if(priv >= 32){
            myrights.add("Can create/modify users")
			priv -= 32
        }else if(priv >= 16){
            myrights.add("User administration rights")
			priv -= 16
        }else if(priv >= 1){
            myrights.add("Log-in enabled")
			priv -= 1
        }else{
            myrights.add("Unkwown")
        }
    }
    return myrights
}

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
        AND AuditDate >=  '${pDate}' AND AuditDate < '${tDate}'
		AND CONVERT(INT,REPLACE(Value2,'''','')) ^ CONVERT(INT,REPLACE(Value1,'''','')) & (32+64+256) > 0
		AND CONVERT(INT,REPLACE(Value2,'''','')) & (32+64+256) > 0
		ORDER BY AuditDate ASC;
"""

result = sql.runSQL(myRequest)

// For each lines, the informations are retrieve and write in file
result?.rows?.each { row ->
    out << "<br><br>"
    out << ""
    previousRights = whichRights((row?.PreviousPrivileges).replaceAll("'","") as int)
    newRights = whichRights((row?.NewPrivileges).replaceAll("'","") as int)
    logDocument.append("""
${row?.Date.format("yyyy-MM-dd HH:mm:ss,SSS")} : L'utilisateur ${users.getUserById(row?.PerformerID).name} (ID : ${row?.PerformerID}) à élévé les droits de ${users.getUserById(row?.UserID).name} (ID : ${row?.UserID}).
    Anciens droits : ${(row?.PreviousPrivileges).replaceAll("'","")}""")
    previousRights?.each{
        logDocument.append("""
        ${it}""")
    }
    logDocument.append("""
    Nouveaux droits : ${(row?.NewPrivileges).replaceAll("'","")}""")
    newRights?.each{
        logDocument.append("""
        ${it}""")
    }
}


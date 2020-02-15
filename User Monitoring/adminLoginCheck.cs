/*
* Author : Koceila HADDOUCHE
* Date : 11/02/2020
* Description : Check if an specific user login this last day then retrieve these informations in log file. 
* This script will be run at 1:00 AM every day 
*/

//Imports
import java.time.*
import java.time.format.DateTimeFormatter

// retrieve the log file
logDocument = new File('D:\\Log_System','OTCS_Admin_Connection.txt')
Newline=System.getProperty("line.separator") // allows the program to add new line

// retrieve today's date and last day's date to format them so that they conform to the database
Date thisDate = new Date().clearTime()
pDate = thisDate.previous()

tDate = thisDate.format("MM/dd/yyyy")
pDate = pDate.format('MM/dd/yyyy')


String myRequest = """
SELECT 
	AuditStr as Connection,
	AuditDate as Date,
	UserID,
	Value1 as AdresseIP,
	Value2 as Name
FROM 
	DAuditNew 
WHERE
    (AuditStr = 'Login' OR AuditStr = 'Logout') 
    AND AuditDate >=  '${pDate}' AND AuditDate < '${tDate}'
    AND UserID = '1000'
    ORDER BY AuditDate DESC;
"""

result = sql.runSQL(myRequest) // execute the sql request

// fill the log document
result?.rows?.each { row ->
    logDocument.append("${row?.Date.format("yyyy-MM-dd HH:mm:ss,SSS")} : The User ${row?.Name} ${row?.UserID} has ${row?.Connection} | IP Adress = ${row?.AdresseIP} ${Newline}")
}


















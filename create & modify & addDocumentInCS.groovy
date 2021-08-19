
// retrieve the log file
logDocument = new File('D:\\Log_System','logLostEtab.txt')
newline=System.getProperty("line.separator") // allows the program to add new line
logDocument.append("""tese${newline}dfaed""")
Date thisDate = new Date().clearTime()
docman.createDocument(asCSNode(35409 as long), "logLostEtab-${thisDate.format("MM/dd/yyyy")}.txt", logDocument)
logDocument.delete()


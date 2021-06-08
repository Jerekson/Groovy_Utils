/*
La première requête modifie le nom de tous les documents en se basant sur le nom initial d'un document et le fait qu'il soit d'un subtype 0. 
La deuxième requête modifie l'affichage du nom de tous les document en se basant cette uniquement sur le nom du document initial. 

La troisième paramètre doit rester à false afin d'éviter les bêtises. 
Il est conseillé de l'exécuter une première fois (dans un try catche) en laissant le false afin de voir si la requête pose problème. 
Lorsque c'est bon. Ce paramètre doit être à true pour qu'il s'exécute correctement. 
*/

sql.runSQL("""
UPDATE DTreeCore SET Name = 'EVP Variables de paie'
WHERE Name = 'EVP variables paie'
AND Subtype = 0
""", false, false, 1)


sql.runSQL("""
UPDATE WebNodesMeta_en SET Name = 'EVP Variables de paie'
WHERE Name = 'EVP variables paie'
""", false, false, 1)
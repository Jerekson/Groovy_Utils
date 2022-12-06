import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit ;
import groovy.io.FileType
import java.io.File
import java.lang.String
import java.io.*;

public int getLengthExcel(CSDocument xlsdoc){
    def speadsheet = xlsx.loadSpreadsheet(xlsdoc)
    def length = 0;
    for (row in speadsheet.getWorksheet(0).readRows(2))
        length++;
    return length;
}

createFile = { String filename ->
    return new File(filename)
}

writeToFile = { File file, String line ->
    file.withWriterAppend("UTF-8") {it.write(line)}
}

renameWithTimeStamp = { String NOM_DOC, String date ->
    int ind = NOM_DOC.lastIndexOf('.')
    
    def ext = NOM_DOC[ind+1..-1]
    def prefix = NOM_DOC[0..ind-1]
    
    def newName = prefix + "_" + date + "." + ext
    
    return newName
}

public String parsingNumVersion (String version){
    int longueur = version.length();
    if (longueur == 1){
        version = "00"+version;
    }
    else if(longueur == 2){
        version = "0"+version;
    }
    else{
        version = version;
    }
    return version;
}

public boolean isValidPath(String path) {
    if (path){
        return true;
    }
    else{
        return false;
    }
}

public String settingOwner(def userName){
    def user = users.getMemberByLoginName(userName)
    if(user){
        return userName
    }
    else{
        return "Admin"
    }
}

public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
    long diffInMillies = date2.getTime() - date1.getTime();
    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
}

private static void copyFileUsingStream(File source, File dest) throws IOException {
    InputStream is = null;
    OutputStream os = null;
    try {
        is = new FileInputStream(source);
        os = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    } 
    
    finally {
        is.close();
        os.close();
    }
}

public void sendEmailSuccess(Date debut, Date fin){
    long duree = getDateDiff(debut,fin,TimeUnit.SECONDS)
	email = mail.create("<b>Script de création du XML de reprise des livrables BackBone - Digitalisation Plan de boite Fibre</b><br/>" 
						+ "Le script a commencé à : " + debut.format("dd/MM/yyyy hh:mm:ss").toString() + "<br/>" 
						+ "Le script a terminé à : " + fin.format("dd/MM/yyyy hh:mm:ss").toString() + "<br/>"
						+ "Durée : " + duree + " secondes <br/>"
                        + "XML créé avec succès... <br/>"
                        + "XML déplacé vers le répertoire Object Importer pour Import... <br/>" 
                       );		
	email.to("mourouguesh.coundjidapadame@sfr.com");
    email.to("guillaume.lambert@aerow.fr");
	email.from("noreply@livelink.sfr.fr");      
	String sub =  "|Success| - Exécution du script de création du XML de Reprise des livrables BackBone du " + debut.format("dd/MM/yyyy").toString() ;
	email.subject(sub);
    //email.attach(xmlReprise);
	email.send();
}

public void sendEmailFailure(Date debut){
	email = mail.create("<b>Script de création du XML de reprise des livrables BackBone - Digitalisation Plan de boite Fibre</b><br/>" 
						+ "Absence de fichier de mapping pour création du XML"
                       );		
	email.to("mourouguesh.coundjidapadame@sfr.com");
	email.from("noreply@livelink.sfr.fr");      
	String sub =  "|Failure| - Exécution du script de création du XML de Reprise des livrables BackBone du " + debut.format("dd/MM/yyyy").toString() ;
	email.subject(sub);
	email.send();
}

public void sendEmailError(Date debut){
	email = mail.create("<b>Script de création du XML de reprise des livrables BackBone - Digitalisation Plan de boite Fibre</b><br/>" 
						+ "Erreur lors de la création du fichier XML de reprise des livrables"
                       );		
	email.to("mourouguesh.coundjidapadame@sfr.com");
	email.from("noreply@livelink.sfr.fr");      
	String sub =  "|Failure| - Exécution du script de création du XML de Reprise des livrables BackBone du " + debut.format("dd/MM/yyyy").toString() ;
	email.subject(sub);
	email.send();
}

public String replaceColon(def nomProjet){
    nomProjet = nomProjet.replaceAll(":", "-");
    return nomProjet;
}

public java.lang.Integer getChildrenz() {
    String query = "SELECT dt.DATAID FROM DTree dt WHERE dt.ParentID = 10704694" ;
    def datid = sql.runSQLFast(query, false, false, 1, [])?.rows?.getAt(0)?.DATAID ;  
    return datid ;                            
}

StartDocument = { Date latestdate -> 
    def file = createFile("D:\\flux\\DigitalisationPlanDeBoite\\RepriseHistoriqueChambres\\XML\\${renameWithTimeStamp("Import.xml", latestdate.format("ddMMyyyyHHmmss"))}")	
    return file
}

StartDocument2 = { def index -> 
    def dateToday = new Date();
    dateToday = dateToday.format("ddMMyyyyHHmmss");
    def file = createFile("D:\\flux\\DigitalisationPlanDeBoite\\RepriseHistoriqueChambres\\XML\\RepriseLivrableChambre_Import_" +index+"_"+dateToday+".xml")	
    return file
}

public void writeToMultipleFiles(int maxLines, String prefix, CSDocument xlsdoc){
    def lineCounter = 1;
    def indexFile = 0;
    String filename = ""; 
	def xmlFile = null;
	
	//def dataid = getChildrenz();
    def dataid = 1;
	if (dataid){
		def speadsheet = xlsx.loadSpreadsheet(xlsdoc)
		def sizeExcel = getLengthExcel(xlsdoc)
		def maxPage = (sizeExcel + maxLines - 1).intdiv(maxLines)
	   
		def codeSPE = ""
		def nomChambre = ""
		def numVersion = ""
		def etatVersion = ""
		def stit = ""
		def numProjet = ""
		def modifiedBy = ""
		def modifiedDate = ""
		def commentaireStatut = ""
		def impact = ""
		def link = ""

		for (int page = 0 ; page < maxPage; page++){ 
			
			indexFile ++ ;
			filename = prefix + indexFile;   
			log.error(filename);
			xmlFile = StartDocument2(indexFile)
			//Date start = new Date();
			//xmlFile = StartDocument(start)
		   
			def _min = Math.min(maxLines,(sizeExcel-lineCounter + 1 ))
		  
			def xmlWriter=new StringWriter()
			def builder = new MarkupBuilder(xmlWriter)
			builder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
			
			builder."import" {
				
				for (int i = 0 ; i < _min ; i++){
					
					def row = speadsheet.getWorksheet(0).readRow(lineCounter + 1) // attention, on commence à la ligne 2 du fichier Excel
					codeSPE = row['A']?.trim()
					nomChambre = row['B']?.trim()
					numVersion = row['C']?.trim()
					etatVersion = row['D']?.trim()
					stit = row['E']?.trim()
					numProjet = row['F']?.trim()
					modifiedBy = row['G']?.trim()
					modifiedDate = row['H']?.trim()
					commentaireStatut = row['I']?.trim()
					impact = row['J']?.trim()
					link = row['K']?.trim()
					
					lineCounter++;
							
					if (!isValidPath(link)){
							link = "";
					}
					log.error("codeSPE : ${codeSPE}")
					codeSPE = replaceColon(codeSPE);
					nomChambre = replaceColon(nomChambre);			
					def numeroVersion = parsingNumVersion(numVersion)	
					numProjet = replaceColon(numProjet);
					
					if(impact=="Non"){
						impact = false;
					}
					else{
						impact = true;
					}
					Date dateModification = null ;
					try{
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
						dateModification = format.parse(modifiedDate); 
					}
					catch (e) {
						log.error("Erreur lors du parsing de la date")
					}
					def mapStatut = ["Diffusion pour MaJ":'dif',
					"Diffusion pour travaux":'dif',
					"Rec NOK intégré":'rec_nok',
					"Rec OK intégré":'rec_ok',
					"Annulé":'ann',
					"En cours de diffusion pour MàJ":'dif',
					"En cours d'intégration":'rec']
				
					if (codeSPE){
						if(numProjet){
							File f = new File(link);
							if (f.exists()){
								"node"(action:'sync', type:'folder') {
								"category"(name:'Content Server Categories:Plan de boite'){
									"attribute" name:'Version', numeroVersion
									"attribute" name:'Statut', etatVersion
									"attribute" name:'Impact', impact
								}
								"category"(name:'Content Server Categories:Reprise Chambre'){
									"attribute" name:'Migration GEDv4', "Oui / Validé"
									"attribute" name:'STIT concerné', stit
									"attribute" name:'Modifié par', modifiedBy
									"attribute" name:'Modifié le', dateModification.format('yyyyMMdd')
								}
								"acl" group:"STIT", permissions:"110000000"
								"acl" group:"RT", permissions:"110000000"
								
								"location" "Enterprise:Dossier chambre:${codeSPE}"
								"title" numProjet
								"modified" dateModification.format('yyyyMMdd')
								"created" dateModification.format('yyyyMMdd')
								"owner" settingOwner(modifiedBy)
								}
								
								def extension = f.getName().lastIndexOf('.').with {it != -1 ? f.getName().substring(it+1):''}
								def docname = codeSPE + "_" + numeroVersion + "_" + mapStatut[etatVersion] + "_" + nomChambre + "." + extension
								
								"node"(action:'sync', type:'document') {
								"category"(name:'Content Server Categories:Plan de boite'){
									"attribute" name:'Commentaires', commentaireStatut
								}
								"file" link
								"location" "Enterprise:Dossier chambre:${codeSPE}:${numProjet}"
								"title" docname
								"modified" dateModification.format('yyyyMMdd')
								"created" dateModification.format('yyyyMMdd')
								"owner" settingOwner(modifiedBy)
								}
								
								"node"(action:'sync', type:'folder') {
								"location" "Enterprise:Dossier chambre:${codeSPE}"
								"title" numProjet
								"modified" dateModification.format('yyyyMMdd')
								"created" dateModification.format('yyyyMMdd')
								}
							}
							else{
								if(etatVersion.contains("En cours")){
									"node"(action:'sync', type:'folder') {
									"category"(name:'Content Server Categories:Plan de boite'){
										"attribute" name:'Version', numeroVersion
										"attribute" name:'Statut', etatVersion
										"attribute" name:'Impact', impact
									}
									"category"(name:'Content Server Categories:Reprise Chambre'){
										"attribute" name:'Migration GEDv4', "Oui / Non validé"
										"attribute" name:'STIT concerné', stit
										"attribute" name:'Modifié par', modifiedBy
										"attribute" name:'Modifié le', dateModification.format('yyyyMMdd')
									}
									"acl" group:"STIT",permissions:"110000000"
									"acl" group:"RT",permissions:"110000000"
									
									"location" "Enterprise:Dossier chambre:${codeSPE}"
									"title" numProjet
									"modified" dateModification.format('yyyyMMdd')
									"created" dateModification.format('yyyyMMdd')									
									"owner" settingOwner(modifiedBy)
									}
								}
								else{	
									"node"(action:'sync', type:'folder') {
									"category"(name:'Content Server Categories:Plan de boite'){
										"attribute" name:'Version', numeroVersion
										"attribute" name:'Statut', etatVersion
										"attribute" name:'Impact', impact
									}
									"category"(name:'Content Server Categories:Reprise Chambre'){
										"attribute" name:'Migration GEDv4', "Oui / Non validé"
										"attribute" name:'STIT concerné', stit
										"attribute" name:'Modifié par', modifiedBy
										"attribute" name:'Modifié le', dateModification.format('yyyyMMdd')
									}
									"acl" group:"STIT",permissions:"110000000"
									"acl" group:"RT",permissions:"110000000"
									
									"location" "Enterprise:Dossier chambre:${codeSPE}"
									"title" numProjet
									"modified" dateModification.format('yyyyMMdd')
									"created" dateModification.format('yyyyMMdd')
									"owner" settingOwner(modifiedBy)
									}
									
									def extension = f.getName().lastIndexOf('.').with {it != -1 ? f.getName().substring(it+1):''}
									def docname = codeSPE + "_" + numeroVersion + "_" + mapStatut[etatVersion] + "_" + nomChambre + ".xlsx"
									
									"node"(action:'sync', type:'document') {
									"category"(name:'Content Server Categories:Plan de boite'){
										"attribute" name:'Commentaires', commentaireStatut
									}
									"file" "\\\\datasfr\\Reference_Nationale\\InfraSpecs\\Dossier vide\\Livrables inexistants.xlsx"
									"location" "Enterprise:Dossier chambre:${codeSPE}:${numProjet}"
									"title" docname
									"modified" dateModification.format('yyyyMMdd')
									"created" dateModification.format('yyyyMMdd')
									"owner" settingOwner(modifiedBy)
									}
									"node"(action:'sync', type:'folder') {
									"location" "Enterprise:Dossier chambre:${codeSPE}"
									"title" numProjet
									"modified" dateModification.format('yyyyMMdd')
									"created" dateModification.format('yyyyMMdd')
									}
								}						
							}
						}
					}
				}
			}
			writeToFile(xmlFile, xmlWriter.toString())
		}
	}
	else{
		sendEmailFailure(start)
	}	
}

CSDocument xlsDoc = docman.getNodeByNickname("RepriseLivrableGEDV4")

Date start = new Date();
writeToMultipleFiles(11000, "FileXML", xlsDoc);
Date end = new Date();
sendEmailSuccess(start, end)





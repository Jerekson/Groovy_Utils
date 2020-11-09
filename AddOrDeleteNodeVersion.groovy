def mondoc = asCSNode(4487838) // classic
def mondoc2 = asCSNode(4486509) // major
def doc = mondoc2
def newvers = 40
def vnumber
def vinitial 

if(!doc.isAdvancedVersionControl){ // Standard document 
    vnumber = doc.versionNum
    vinitial = vnumber
    for(vnumber; vnumber < newvers; vnumber++){ // différence entre la version actuelle et la nouvelle
        docman.addVersion(doc, doc.content.content) // ajout des versions. 
    }
    def currentMajorVersion = doc.versionNum
    log.error("${currentMajorVersion}")
    doc.versions.each{
        log.error("it ${it.number} vinitial ${vinitial}")
        if(it.number > vinitial && it.number < currentMajorVersion){ // suppression des version qui ont été ajouté précédemment hors mis la dernière
            docman.deleteVersion(it)
        }
    }    
    
}else{ // Advanced Document (la même que plus haut en version advanced du document)
    vnumber = doc.lastVersion.verMajor
    vinitial = vnumber
    for(vnumber; vnumber < newvers; vnumber++){
        docman.addMajorVersion(doc, doc.content.content)
    }
    def currentMajorVersion = doc.lastVersion.verMajor
    log.error("${currentMajorVersion}")
    doc.versions.each{
        log.error("it.verMajor ${it.verMajor} vinitial ${vinitial}")
        if(it.verMajor > vinitial && it.verMajor < currentMajorVersion){
            docman.deleteVersion(it)
        }//Ajout de la suppression de la version mineur. 
    }
}

docman.updateNode(doc) // PAS OUBLIER

/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils


def strTab=[:]

scalarExtractor = { str ->
    //out << "we need to analyze ${str}<br/>"
    def outStr = ""
    def strSep = "'"
    def inString = false
    def strId=0
    def tmpStr
    // Parsing
    str.each() { chr ->
        /*out << chr
        out << "<br>"*/
        if (chr == strSep) {
            //out << "inString -> ${inString}<br>"
            if (inString) {
                // String is over, store it in tab
                strTab.put("s${strId}" as String,tmpStr)
                outStr += "s${strId}"
                inString=false
                strId+=1
            } else {
                // New string detected
                tmpStr=""
                inString=true
            }
        } else {
            if (inString) {
                tmpStr += chr
            } else {
            	outStr += chr
            }
        }
    }
    return outStr
}

analyseBasicString = { str -> 
    //out << "value -> " + str[0] + " ----> ${str}<br>"
    if (str.size() == 0) {
        log.error("Empty String")
    } else if (str[0] == "{" || str[0] == "[") {
        //out << "list  <br>"
        return [
            "type":"list",
            "children":extractListElement(extractListData(str))
            ]
    } else if (str.size() > "A<1,?".size() && str.substring(0,"A<1,?".size()) == "A<1,?") {
        //out << "assoc  <br>"
        return [
            "type":"assoc",
            "children":extractAssocElement(extractAssocData(str))
            ]
    } else if (str[0] == "s") {
        //out << "string1  <br>"
        return [
            "type":"string",
            "name":str,
            "value":strTab[str]
            ]
    } else if (str[0] == "X") {
        //out << "string2  <br>"
        return [
        	"type":"string",
            "value":str
            ]
    } else if (StringUtils.isNumeric(str[0]) || str[0] == "-") {
        //out << "Integer  <br>"
        return [
        	"type":"integer",
            "value":str
            ]
	} else if (str in ["true","false"]) {
        //out << "Boolean  <br>"
        return [
        	"type":"boolean",
            "value":str
            ]
	} else if (str[0] == "V") {
        //out << "RecArray<br>"
        return [
            "type":"RecArray",
            "children":extractRecArrayData(str)
            ]
    } else if (str[0] == "?") {
        //out << "QuestionMark<br>"
        return [
            "type":"QuestionMark",
            "value":str
            ]
    } else if (str[0] == "D") {   
       	// out << "Date<br>"
        date = str.substring(2,str.size())+"+0000" //Date Format
        def newdate = new Date().parse("yyyy/MM/dd:HH:mm:ssZ", date) //Parse Date //Wed Jul 25 19:04:55 CEST 2018
        str = JsonOutput.toJson(newdate) //Groovy to json. //"2018-07-25T17:04:55+0000"
        return [
            "type":"Date",
            "value":str
            ]
    } else if (str[0] == "G") {
        //out << "Real  <br>"
        return [
        	"type":"Real",
            "value":str.substring(1,str.size())
            ]
	} else if (str[0] == "R") {
        //out << "Record  <br>"
        return [
        	"type":"Record",
            "children":extractRecordElement(extractRecordData(str))
            ]
    } else if (str[0] == "(" && str ==~ /\(\d+,\d+\)/) {
        // Should be like (x,y)
        def m = (str =~ /\((\d)+,(\d+)\)/)
        return [
            "type":"Coord",
            "x": m[0][1],
            "y": m[0][2]
            ]
	} else {
        log.error("Unkown type: ${str}")
    }
}

extractRecordData = { str ->
    def dataString = ""
    def nbOpenedTags = 1
    def pos="R<".size()
    def list = []
    
    // Retrieve column titles
    while (nbOpenedTags > 0 && pos < str.size()-1) {
        if (str[pos] == "<") {
            nbOpenedTags+=1
        } else if (str[pos] == ">") {
            nbOpenedTags-=1
        }
		
        if (nbOpenedTags > 0) {
        	dataString+=str[pos]
        }
        pos+=1
    }
    return dataString
}

extractRecordElement = { str ->
    log.error("(Record) Extract first element from ${str}")
    // We need to extract data like 'sX=....,....'
    def key = str.split("=")[0]
    // Search for significant coma
    def value=""
    
    def nbOpenedTags=0
    def pos="${key}=".size()
    
    while (pos < str.size() && (str[pos] != "," || (str[pos] == "," && nbOpenedTags>0))) {
        if (str[pos] in ["<","{"]) {
            nbOpenedTags+=1
        } else if (str[pos] in [">","}"]) {
            nbOpenedTags-=1
        }
        
        value+=str[pos]
        pos+=1
    }

    def tmpList=[[
        "key":(strTab[key])?strTab[key]:key, // Key can be either a field in strTab or an integer
        "value":analyseBasicString(value)
        ]]
    
    if (pos < str.size() && str[pos]==",") {
    	tmpList.addAll(extractRecordElement(str.substring(pos+1,str.size())))
    }

    return tmpList
}

extractRecArrayData = { str -> 
    def dataString = ""
    def nbOpenedTags = 1
    def pos="V{<".size()
    def list = []
    def recList = []
    def listTitle=[]
    
    // Retrieve column titles
    while (nbOpenedTags > 0 && pos < str.size()-1) {
        if (str[pos] == "<") {
            nbOpenedTags+=1
       	} else if (str[pos] == ">") {
            nbOpenedTags-=1
        }
		
        if (nbOpenedTags > 0) {
        	dataString+=str[pos]
        }
        pos+=1
    }
    def colTitles=dataString.split(",")
    log.error("ColTitles = ${colTitles}")
    
    // Read all records
    dataString=""
    recString = ""
    
    
    def outList=extractRecArrayElement(colTitles,str.substring(pos,str.size()-1))
    
    /****/
    
    //out << "<br>"
    //out << strTab[colTitles[0]] +"<br>"
    for(i=0;i<colTitles.size();i++){
        listTitle.add(analyseBasicString(colTitles[i]))
    }
    //out << listTitle
    //out << "<br>"
    
    recList.push(["key":listTitle,"value":outList])
    //out << "<br>recList<br>${recList}<br><br>"
    /****/
    log.error("Outlist is ${outList}")
    //out << "<br>${outList}<br>"
    
    
    return recList
}

extractRecArrayElement = { titles, str -> 
    log.error("(Recarray) Extract first element from ${str}")
    // We need to extract data like 'sX=....,....'
    //def key = str.split("=")[0]
    // Search for significant coma
    if (str != "") {
        def value=""
        def prec=""
        def nbOpenedTags=0
        def pos=0
        while (pos < str.size() && ((str[pos] != "<" || prec != ">") || ((str[pos] == "<" && prec==">") && nbOpenedTags>0))) {
            if (str[pos] in ["<","{","("]) {
                nbOpenedTags+=1
            } else if (str[pos] in [">","}",")"]) {
                nbOpenedTags-=1
            }
            value+=str[pos]
            prec=str[pos]
            pos+=1
        }
        
        def valuesList=extractListElement(value.substring(1,value.size()-1))
        
        def tmpNewElement=[]
        def index=0
        
        titles.each() { key ->
            tmpNewElement.push(["key":strTab[key],"value":valuesList[index]])
            index+=1        
        }
        
        def tmpList=[tmpNewElement]
    
        //def tmpList=[(analyseBasicString(titles.first())):analyseBasicString(value)]
        
        if (pos < str.size() && str[pos]=="<" && prec == ">") {
            tmpList.addAll(extractRecArrayElement(titles,str.substring(pos,str.size())))
        }
        log.error("extractRecArrayElement : ${tmpList}")
        
        return tmpList
    } else {
        return []
    }
}

extractAssocData = { str ->
    def dataString = ""
    def nbOpenedTags = 1
    def pos="A<1,?,".size()
    
    while (nbOpenedTags > 0 && pos < str.size()) {
        if (str[pos] == "<") {
            nbOpenedTags+=1
        } else if (str[pos] == ">") {
            nbOpenedTags-=1
        }
		
        if (nbOpenedTags > 0) {
        	dataString+=str[pos]
        }
        pos+=1
    }
    
    return dataString
}

extractAssocElement = { str ->
    log.error("(Assoc) Extract first element from ${str}")
    if (str != "") {
        // We need to extract data like 'sX=....,....'
        def key = str.split("=")[0]
        // Search for significant coma
        def value=""
        
        def nbOpenedTags=0
        def pos="${key}=".size()
        
        while (pos < str.size() && (str[pos] != "," || (str[pos] == "," && nbOpenedTags>0))) {
            if (str[pos] in ["<","{","("]) {
                nbOpenedTags+=1
            } else if (str[pos] in [">","}",")"]) {
                nbOpenedTags-=1
            }
            
            value+=str[pos]
            pos+=1
        }
    
        def tmpList=[[
            "key":(strTab[key])?strTab[key]:key, // Key can be either a field in strTab or an integer
            "value":analyseBasicString(value)
            ]]
        
        if (pos < str.size() && str[pos]==",") {
            tmpList.addAll(extractAssocElement(str.substring(pos+1,str.size())))
        }
    
        return tmpList
    } else {
        return [:]
    }
}

extractListData = { str ->
    def dataString = ""
    def nbOpenedTags = 1
    def pos="{".size()
    
    while (nbOpenedTags > 0 && pos < str.size()) {
        if (str[pos] == "{") {
            nbOpenedTags+=1
        } else if (str[pos] == "}") {
            nbOpenedTags-=1
        }
		
        if (nbOpenedTags > 0) {
        	dataString+=str[pos]
        }
        pos+=1
    }
    
    return dataString
}

extractListElement = { str ->
    log.error("(List) Extract first element from ${str}")
    // We need to extract data like 'sX=....,....'
    //def key = str.split("=")[0]
    // Search for significant coma
    def value=""
    
    def nbOpenedTags=0
    def pos=0
    
    while (pos < str.size() && (str[pos] != "," || (str[pos] == "," && nbOpenedTags>0))) {
        //log.error("(List) read ${str[pos]}, nbOpenedTags=${nbOpenedTags}")
        if (str[pos] in ["<","{","("]) {
            nbOpenedTags+=1
        } else if (str[pos] in [">","}",")"]) {
            nbOpenedTags-=1
        }
        
        value+=str[pos]
        pos+=1
    }
    
    log.error("(List) First element should be ${value}")

    def tmpList=[
        analyseBasicString(value)
        ]
    
    if (pos < str.size() && str[pos]==",") {
    	tmpList.addAll(extractListElement(str.substring(pos+1,str.size())))
    }

    return tmpList
}

fullObjectToData = { data ->
    log.error("Parsing data ${data}")
    //out << "Parsing data ${data} <br>"
    if (data && data.type) {
        if (data.type == "assoc") {
            def tmpList = [:]
            data.children.each() { elt ->
                tmpList.put(elt.key, fullObjectToData(elt.value))
            }
            return tmpList
        } else if (data.type == "list") {
            def tmpList = []
            data.children.each() { elt ->
                tmpList.push(fullObjectToData(elt))
            }
            return tmpList
        } else if (data.type == "string") {
            return data.value
        } else if (data.type == "boolean") {
            return (data.value=="true")
        } else if (data.type == "integer") {
            return data.value
        } else if (data.type == "RecArray") {
            def tempList = []
            data.children.value[0].each() { elt -> 
                def tmpMap = [:]
                elt.each() { eltChild ->
                    tmpMap.put(eltChild.key, fullObjectToData(eltChild.value))
                }
                tempList.add(tmpMap)
            }
            return tempList
        } else if (data.type == "QuestionMark") {
            return data.value
        } else if (data.type == "Date") {
            return data.value
        } else if (data.type == "Real") {
            return data.value
        } else if (data.type == "Record") {
           	def tmpMap = [:]
            data.children.each() { elt ->
                //elt.each() { elt2 -> 
                    tmpMap.put(elt.key, fullObjectToData(elt.value))
                //}
            }
            return tmpMap
        } else if (data.type == "Coord") {
            return [
                "x": data.x,
                "y": data.y
                ]
        } else {
            log.error("ERROR: unknown type ${data}")
            out << "ERROR: unknown type ${data} <br>"
        }
    } else {
        log.error("ERROR: type not found: ${data}")
    }
}

parseAssoc = { assocData ->

    def strBasicData=scalarExtractor(assocData)
    //out << "strBasicData -> ${strBasicData}<br/>"
    log.error("Basic data: ${strBasicData}")
    
    def fullObject = analyseBasicString(strBasicData)
    //out << "fullObject -> ${fullObject}<br/>"
    
    return fullObjectToData(fullObject)
    
}

findNullKey = { obj ->
    if (obj && obj instanceof ArrayList) {
        log.error("Analyze list ${obj}")
        obj.each() { elt ->
            str = findNullKey(elt)
            if (str != "") {
                return str
            }
        }
    } else if (obj && obj instanceof LinkedHashMap) {
        log.error("Analyze map")
        ((Map) obj).each() { key, val ->
            log.error("Analyze map - ${key}")
            if (key == "") {
                return "ERROR on ${obj}"
            } else {
                str = findNullKey(val)
                if (str != "") {
                    return str
                }
            }
        }
    }
    // Try ton JSONfy it
    if (obj) {
        log.error("JSON from $obj")
        def builder = new JsonBuilder()
        builder(obj)
        json(builder.toString())
    }
    return ""
}

assoc = "A<1,?,'String'='TestStringValue','leftComponents'={'response','blabla'},'name'=X,'Integer'=1000,'RecArray'=V{<'ID','NAME','ADDRESS','NOTE'><1,'premier',R<'Test'='Valeur','Toto'=1,'Titi'=G3.1415>,'Mensonge'><2,'deuxieme','Tremoille',?>},'Boolean'=true,'Date'=D/2018/7/25:17:4:55,'Real'=G3.14,'reccord'=R<'Test'='Valeur','Toto'=1,'Titi'=G3.1415>>," //'liste2'={'valueListe2.1','valueListe2.2'}>,"
assocRecaray = "A<1,?,'RecArray'=V{<'ID','NAME','ADDRESS','NOTE'><1,R<'Test'='Valeur','Toto'=1,'Titi'=G3.1415>,'F1','note'><2,'deuxieme','Tremoille',?>}>" //R<'Test'='Valeur','Toto'=1,'Titi'=G3.1415>
assocList = "A<1,?,'leftComponents'={'response','blabla',R<'Test'='Valeur','Toto'=1,'Titi'=G3.1415>}>"
assocRecord = "A<1,?,'reccord'=R<'Test'='Valeur','Toto'=1,'Titi'=G3.1415>>"
assocMap="{6,0,R<'MAPINFO'=R<'INITIATECB'=?,'COMPLETECB'=?,'DELETECB'=?,'SUSPENDCB'=?,'RESUMECB'=?,'STOPCB'=?,'ARCHIVECB'=?,'PAINTER'=?,'FLAGS'=32,'TITLE'='SynlabReopenLevertonCaseWorkflow','DESCRIPTION'='','INSTRUCTIONS'=?,'PRIORITY'=?,'PROJECT'=?,'MANAGERID'=0,'DUEDURATION'=?,'DUEDATE'=?,'DUETIME'=?,'STARTDATE'=?,'TYPE'=1,'SUBTYPE'=1,'USERFLAGS'=V{<'ID','FLAGS','TYPE','NAME'>},'PROMPT'=false,'USERDATA'=?,'EXATTS'=A<1,?,'CustomMsg'=?,'LL_Role'=?,'MaxTaskID'=11>,'CUSTOMDATA'=A<1,?>,'MAPOBJID'=443774>,'TASKS'=V{<'SUBMAPID','PERFORMERID','INITIATECB','COMPLETECB','READYCB','DONECB','KILLCB','RESURRECTCB','PERFORMERCB','SUBMAPIDCB','CONDITIONCB','CONDITION','FORM','PAINTER','DUEDURATION','DUEDATE','DUETIME','FLAGS','TITLE','DESCRIPTION','STARTDATE','INSTRUCTIONS','PRIORITY','TYPE','SUBTYPE','USERFLAGS','TASKID','WORKPKGINFO','USERDATA','EXATTS','CUSTOMDATA','BEFORESENDCB'><?,?,?,?,?,?,?,?,?,?,?,?,A<1,?,'AttributeForm'={A<1,?,'columns'='1','fields'={A<1,?,'key'=A<1,?,'id'='5','object'='Attribute'>,'label'=A<1,?,'_en_US'='CaseOwner','_fr'='','_it'=''>,'readonly'=false,'required'=false>,A<1,?,'key'=A<1,?,'id'='2','object'='Attribute'>,'label'=A<1,?,'_en_US'='TargetCase','_fr'='','_it'=''>,'readonly'=false,'required'=false>},'label'=A<1,?,'_en_US'='Define some information ...','_fr'='','_it'=''>>}>,{(30,50),'Start Step'},?,?,?,?,'Start Step',?,?,'Set attachements',?,1,100,?,1,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},A<1,?,'Display'=0>,A<1,?,'InitiateInSmartView'=false,'SetDueDate'=false,'Signature'=0>,A<1,?>,?><?,?,?,?,?,?,?,?,?,?,?,?,A<1,?,'AttributeForm'={A<1,?,'columns'='1','fields'={A<1,?,'key'=A<1,?,'id'='StartDate','object'='SystemField'>,'label'=A<1,?,'_en_US'='Date Initiated','_fr'='','_it'=''>,'readonly'=true,'required'=false>,A<1,?,'key'=A<1,?,'id'='Owner','object'='SystemField'>,'label'=A<1,?,'_en_US'='Initiator','_fr'='','_it'=''>,'readonly'=true,'required'=false>},'label'=A<1,?,'_en_US'='Workflow history','_fr'='','_it'=''>>,A<1,?,'columns'='1','fields'={A<1,?,'key'=A<1,?,'id'='1_3','object'='FormAttribute'>,'label'=A<1,?,'_en_US'='SynlabReopenLevertonCaseTemplate Form : targetFolderId','_fr'='','_it'=''>,'readonly'=true,'required'=false>,A<1,?,'key'=A<1,?,'id'='3','object'='Attribute'>,'label'=A<1,?,'_en_US'='Check this box to confirm you have reopened the Case','_fr'='','_it'=''>,'readonly'=false,'required'=true>},'label'=A<1,?,'_en_US'='Case details','_fr'='','_it'=''>>}>,{(430,40),?},?,?,?,8192,'Release Case',?,?,'Following case need to be released in Leverton, please reopen it and valid the form. Once it will be reopened, all new documents will be sent to Leverton, and extraction will have to be performed in order to update the Case.',50,1,101,A<1,?,'DispositionDefault'=1,'Dispositions'={'Approve','Reject'},'DispositionsLocalized'=A<1,?,'Approve'=A<1,?>,'Reject'=A<1,?>>,'Permissions'=22,'SmartView'=true>,4,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},?,A<1,?,'DispoChoice'='Concensus','EnableEmail'=true,'GroupFlags'=0,'PerformerData'=A<1,?,'Data'='1_5','Key'='1_4','Type'=2>>,A<1,?>,?><?,?,?,?,?,?,?,?,?,?,{A<1,?,'ExpressionData'={A<1,?,'Key'='2','Operand'='4','Operator'='=','Value'='Approve'>},'Steps'={11}>},?,?,{(660,40),'Case Owner Decision'},?,?,?,1,'Case Owner Decision',?,?,?,?,1,102,?,5,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},?,A<1,?>,A<1,?>,?><?,?,?,?,?,?,?,?,?,?,?,?,?,{(1140,50),'Closed'},0,?,?,3,'Closed',?,?,?,?,1,103,?,7,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},?,A<1,?>,A<1,?>,?><?,?,?,?,?,?,?,?,?,?,?,?,?,{(910,190),'Refused'},0,?,?,3,'Refused',?,?,?,?,1,103,?,9,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},?,A<1,?>,A<1,?>,?><?,?,?,?,?,?,?,?,?,?,{A<1,?,'Steps'={}>},?,A<1,?,'FORM_FORMS'={A<1,?,'ID'=1,'Name'='SynlabReopenLevertonCaseTemplate Form','View'='Initiate'>}>,{(200,40),'InitWorkflow'},?,?,?,1,'InitWorkflow',?,?,?,?,43200,100,?,10,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},?,A<1,?,'CSOutVar'=6,'CSscriptID'='852425'>,A<1,?,'EXATTS'=A<1,?,'CSOutVar'=6,'CSscriptID'='852425'>>,?><?,?,?,?,?,?,?,?,?,?,{A<1,?,'Steps'={}>},?,A<1,?,'FORM_FORMS'={A<1,?,'ID'=1,'Name'='SynlabReopenLevertonCaseTemplate Form','View'=?>}>,{(880,40),'Move Documents'},?,?,?,1,'Move Documents',?,?,?,?,43200,100,?,11,V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'>},?,A<1,?,'CSOutVar'=7,'CSscriptID'='859563'>,A<1,?,'EXATTS'=A<1,?,'CSOutVar'=7,'CSscriptID'='859563'>>,?>},'LINKS'=V{<'FROMTASK','TOTASK','LINKTYPE','POINTS','EXITCON','ENTRYCON'><2,3,0,?,?,?><3,5,16,{},?,A<1,?,'PERIMETER'=0,'X'=G0,'Y'=G0.5>><1,6,0,{},?,A<1,?,'PERIMETER'=1,'X'=G0,'Y'=G0.5>><6,2,15,?,?,?><3,7,15,?,?,?><7,4,15,?,?,?>},'WORK_PACKAGES'=V{<'TYPE','SUBTYPE','USERDATA','DESCRIPTION'><1,1,867004,?><1,3,A<1,?,'BuildInstruction'=X<WEBWFP_HTMLLABEL.AddAttributeItemsMsg>,'Content'=A<1,?,'RootSet'=A<1,?,'Children'={A<1,?,'DisplayName'='TargetCase','FixedRows'=true,'ID'=2,'IsNodeID'=true,'MaxRows'=1,'Name'='TargetCase','NodeTypes'={136},'NumRows'=1,'Type'=305>,A<1,?,'DisplayName'='CaseOwner','FixedRows'=true,'ID'=5,'MaxRows'=1,'Name'='CaseOwner','NumRows'=1,'Required'=false,'Search'=false,'SelectGroup'=false,'Type'=14>,A<1,?,'DisplayName'='CaseOpened','FixedRows'=true,'ID'=3,'MaxRows'=1,'Name'='CaseOpened','NumRows'=1,'Search'=false,'Type'=5>},'DisplayName'='Attributes','FixedRows'=true,'ID'=1,'MaxRows'=1,'Name'='Attributes','NextID'=6,'NumRows'=1,'Type'=-18,'ValueTemplate'=A<1,?,'ID'=1,'Values'={A<1,?,2=A<1,?,'ID'=2,'Values'={?}>,3=A<1,?,'ID'=3,'Values'={false}>,5=A<1,?,'ID'=5,'Values'={?}>>}>>>,'Version'=true>,?><43200,100,{A<1,?,'ID'=1,'Name'='SynlabReopenLevertonCaseScript','nodeID'=852425>,A<1,?,'ID'=2,'Name'='SynlabReopenLevertonCaseMoveDocs','nodeID'=859563>},?><1,4,'{A<1,?,\'DisplayAttachments\'=false,\'ID\'=1,\'Name\'=\'SynlabReopenLevertonCaseTemplate Form\',\'Required_Form\'=false,\'StorageMech\'=0,\'SubForms\'={},\'SubFormTemplateIDs\'={},\'TemplateID\'=852389,\'Version\'=true,\'View\'=\'Initiate\'>}',?>}>}"
assocTab="{A<1,?,2=A<1,?,'ID'=2,'Values'={?}>,3=A<1,?,'ID'=3,'Values'={false}>,5=A<1,?,'ID'=5,'Values'={?}>>}"

assocCoord="{(430,40),?}"

assocInit = "A<1,?>"


assocData=parseAssoc(assocMap)	

//out << assocData

//out << findNullKey(assocData)

def builder = new JsonBuilder()
builder(assocData)

json(builder.toString())







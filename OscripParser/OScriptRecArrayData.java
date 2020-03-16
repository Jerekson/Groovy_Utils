/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

import OScriptListData

class OScriptRecArrayData extends OScriptParseData{
    //TODO FUNCTION : addRecord(record) | removeRecord(record) | extractRecarrayData(str) | extractRecarrayElement(str) | getValue(key)
	
	public def extractRecArrayData(str){
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
		
		// Read all records
		dataString=""
		recString = ""
		
		def outList=extractRecArrayElement(colTitles,str.substring(pos,str.size()-1))
		
		for(i=0;i<colTitles.size();i++){
			listTitle.add(analyseBasicString(colTitles[i]))
		}
		
		recList.push(["key":listTitle,"value":outList])    
		
		return recList
	}

	public def extractRecArrayElement(titles, str){
		// Extract first element from ${str} -> log.error("(Recarray) Extract first element from ${str}")
		// We need to extract data like 'sX=....,....'
		// Search for significant coma
		def value=""
		def prec=""
		def nbOpenedTags=0
		def pos=0
		while (pos < str.size() && ((str[pos] != "<" || prec != ">") || ((str[pos] == "<" && prec==">") && nbOpenedTags>0))) {
			if (str[pos] in ["<","{"]) {
				nbOpenedTags+=1
			} else if (str[pos] in [">","}"]) {
				nbOpenedTags-=1
			}
			value+=str[pos]
			prec=str[pos]
			pos+=1
		}
		
		def valuesList=OScriptListData.extractListElement(value.substring(1,value.size()-1))
		
		def tmpNewElement=[]
		def index=0
		
		titles.each() { key ->
			tmpNewElement.push(["key":strTab[key],"value":valuesList[index]])
			index+=1        
		}
		
		def tmpList=[tmpNewElement]
		
		if (pos < str.size() && str[pos]=="<" && prec == ">") {
			tmpList.addAll(extractRecArrayElement(titles,str.substring(pos,str.size())))
		}
		
		return tmpList
	}
	
	public def addRecord(record, newValue){
		
	}
	
	public def removeRecord(record, value){
		
	}
	
	public def getValue(key){
		
	}
}
/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

class OScriptRecordData extends OScriptParseData{
    //TODO FUNCTION : addKey() | getKey(index) | getValue(key) | extractRecordData(str)
	
	public def extractRecordData(str){
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
	
	public def extractRecordElement(str){
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
	
	public def getKey(record){
		
	}
	
	public def getValue(record, key){
		
	}
}
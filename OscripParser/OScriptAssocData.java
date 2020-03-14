/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

class OScriptAssocData extends OScriptParseData{
    //TODO FUNCTION : parseAssoc() | extractAssocData(str) | extractAssocElement(str)
	
	public def extractAssocData(str){
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

	public def extractAssocElement(str){
		// Extract first element from ${str} -> log.error("(Assoc) Extract first element from ${str}")
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
			"key":strTab[key],
			"value":analyseBasicString(value)
			]]
		
		if (pos < str.size() && str[pos]==",") {
			tmpList.addAll(extractAssocElement(str.substring(pos+1,str.size())))
		}

		return tmpList
	}

	public def toJson(str){
		
	}
	
	public def toString(str){
		
	}
	
}

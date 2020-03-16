/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

class OScriptListData extends OScriptParseData{
    //TODO FUNCTION : addItem(item) | removeItem(item or index) | getValue(index) | extractListData(str) | extractListElement(str)
	
	public def addItem(list, item){
		list.add(item)
	}
	
	public def removeAllItem(list, item){
		list.removeAll{ it == item}
		//or
		list.removeAll([item] as Object[])
	}
	
	public def removeItem(list, index){
		list.removeAt(index)
	}
	
	public def removeItem(list, item){	//polymorphisme
		list.remove(item)
	}
	
	public def getValue(list, index){
		return list[index]
	}
	
	public def extractListData(str){
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
	
	public def extractListElement(str){
		//Extract first element from ${str} -> log.error("(List) Extract first element from ${str}")
		// We need to extract data like 'sX=....,....'
		// Search for significant coma
		def value=""
		
		def nbOpenedTags=0
		def pos=0
		
		while (pos < str.size() && (str[pos] != "," || (str[pos] == "," && nbOpenedTags>0))) {
			if (str[pos] in ["<","{"]) {
				nbOpenedTags+=1
			} else if (str[pos] in [">","}"]) {
				nbOpenedTags-=1
			}
			
			value+=str[pos]
			pos+=1
		}

		def tmpList=[
			analyseBasicString(value)
			]
		
		if (pos < str.size() && str[pos]==",") {
			tmpList.addAll(extractListElement(str.substring(pos+1,str.size())))
		}

		return tmpList
	}

	public def toJson(str){
		
	}
	
	public def toString(str){
		
	}
	
}
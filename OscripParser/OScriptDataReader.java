/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

class OScriptDataReader extends OScriptParseData{
	//TODO FUNCTION : toString(str) | toJson(str)
	
	public def toJson(data){		
		return super.parse(data)
	}
	
	public def toString(data){
		
	}
	
}
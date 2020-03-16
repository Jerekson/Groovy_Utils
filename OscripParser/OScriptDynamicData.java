 /*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

class OScriptDynamicData extends OScriptParseData{
	//TODO FUNCTION : scalarExtractor(String str)
	
	public OScriptTypeData(str){
		//def value
		data = super.scalarExtractor(str)
		fElement = data[0]
		type = super.analyseBasicString(fElement)
		
		//print type 
		//out << type
		
		//call the right function. 
	}
	
}
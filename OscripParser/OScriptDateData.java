/*
 * Parse OScript Value
 */

import org.apache.commons.lang3.StringUtils

class OScriptDateData extends OScriptParseData{
    //TODO FUNCTION : dateFromatToJson() | dateFormatToString()
	public def dateFromatToJson(str){
																		//if date = D/2018/7/25:17:4:55
		date = str.substring(2,str.size())+"+0000" 						//Date Format
		def newdate = new Date().parse("yyyy/MM/dd:HH:mm:ssZ", date) 	//Parse Date 		=> Wed Jul 25 19:04:55 CEST 2018
		return JsonOutput.toJson(newdate) 								//Groovy to json. 	=> "2018-07-25T17:04:55+0000"
	}
	
	public def dateFormatToString(date){
		slurper = new groovy.json.JsonSlurper()
		return slurper.parseText(date)
	}
}
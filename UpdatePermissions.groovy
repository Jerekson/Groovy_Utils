/**
 * Update Permission
 * @author: Koceila HADDOUCHE
 */

runCS("SGDBF_FUSION_SGID_CONFIG")
def config = GetConfig()
def FusionSGID = apm.loadClass("SGDBF_Fusion_SGID:Utils:FusionSGID", true)
def sample = FusionSGID.newInstance(this)

mycache = cache.get("${config.configNickname}_UpdatePermissions")
log.error("update : ${mycache}")
node = asCSNode(mycache.BWID as long)
BW = node
log.error("nodeID = ${node}")

updatePermissions = { node ->
    node.getChildren().each{
        log.error("node name -> ${it.name}")
        docman.clonePermissions(BW, it)
        if(it.subtype == 0){
            updatePermissions(it)
        }
    }
} 
    
updatePermissions(node)




/*
        }else{
            try{
                def permList = []
                permList << "SEE"
                //permList << "SEECONTENTS"
                //permList << "MODIFY"
                //permList << "EDITATTRIBUTES"
                //permList << "RESERVE"
                //permList << "ADDITEMS"
                //permList << "DELETEVERSIONS"
                //permList << "DELETE"
                //permList << "EDITPERMISSIONS"                

                def userlistPermission = [:]
                def user = users.getGroupByName(csvars.GROUP_ACCESS_FORM)
                if(user){
                    userlistPermission.put(user,permList)
                }

                def resMap =[:]
                if(userlistPermission){
                    resMap = docman.updateNodeRights(it, userlistPermission, "Replace", "Node", false, 100)
                    log.error("${resMap}")
                }else{
                    log.error("No users found")
                }
            }catch(e){
                log.error("Unable to update permissions for node {}", node, e)
                printError(e)
            }
*/







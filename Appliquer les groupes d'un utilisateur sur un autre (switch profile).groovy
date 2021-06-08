/*
* Script Name: switch profile
* @@@Author: AEROW KHA
* Date: 11/05/2021
* Description: Allow users to copy all groups in utilisateurTest1 from an other users
*/

try{
    // HTML 
    def userSGID
    def usersTest1 = users.getUserByLoginName("utilisateurTest1")

    // Check if the user exists
    if(params.userSGID){
        if(users.getUserByLoginName(params.userSGID as String)){
            userSGID = users.getUserByLoginName(params.userSGID as String)
        }else{
            out << "<h5>l'utilisateur '${params.userSGID}' n'existe pas</h5>"
        }
    }

    out << """
<br>
<form action="" method="post">
    <input type="hidden" name="func" value="ll"/>
    <input type="hidden" name="objId" value="${self.ID}"/>
    <input type="hidden" name="objAction" value="Execute"/>

    <label for="society">Entrer le SGID d'un utilisateur : </label>
    <input type="text" name="userSGID" id="userSGID" required>

    <input type="submit" value="Rechercher">
</form>
<br><br>
"""
    verifyGroups = { groupsList ->
        blackListGroups = [
            "DefaultGroup",
            "Business Administrators",
            "Administrator",
            "SGDBF-DSI",
            "SGDBF-RH"
        ]

        blackListGroups.each{
            if(groupsList.contains(it as String)){
                groupsList.removeAll(Collections.singleton(it as String));
            }
        }
        return groupsList
    }

    retrieveGroups = { userID -> 
        //userID = users.getUserByLoginName("koceila.haddouche");
        groupList = [];

        String sqlRequest = """
    SELECT * FROM KUAFChildren WHERE ChildID = ${userID.ID}
    """

        result = sql.runSQL(sqlRequest).rows

        result.each{
            try{
                if(users.getGroupById(it.ID as long)){
                    groupList.add(users.getGroupById(it.ID).name)
                }
            }catch(e){
                //out << e
            }
        }   

        return verifyGroups(groupList)
    }

    addGroupsToUsers = { userTest, groupsName -> 
        try{
            groupsName.each{
                userTest.addToGroup(users.getGroupByName(it.toString()))
            }
        }catch(e){
            out << e
        }
    }

    removeFromAllGroups = {userTest, groupsName ->
        groupsName.each{
            users.removeMemberFromGroup(users.getGroupByName("${it}") as CSGroup, userTest)
        }
    }



    if(userSGID){
        out << """
    Bonjour ${users.CURRENTUSER().firstName}, tous les groupes de l'utilisateurs ${userSGID.name} ont été transféré à l'utilisateur : ${usersTest1.name}<br>
    """
        //retrieve usersTest groups
        userTestgroups = retrieveGroups(usersTest1)

        // Purge all utilisateurTest1 groups
        if(userTestgroups){
            removeFromAllGroups(usersTest1, userTestgroups)
        }

        // Retrieve all user groups
        userGroups = retrieveGroups(userSGID)

        // Add all groups to utilisateurTest1
        if(userGroups){
            addGroupsToUsers(usersTest1, userGroups)
        }
    }
}catch(e){
    out << e
}



